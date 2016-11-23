package elaracomunicaciones.gpstracking;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SavePhotosService extends AppCompatActivity {

    private static int TAKE_PICTURE = 1;
    private static int SELECT_PICTURE = 2;
    private String name = "";
    int code = TAKE_PICTURE;
    final static int cons = 0;
    private int idTechnician = 0;
    private int idService = 0;
    private int idType = 0;
    private int status = 0;
    private int actualPhoto = 2;
    private int qtyPhotos;
    List<PhotoCatalog> ListPhotos;
    Bitmap bmp;
    private String error;
    private getListOfPhotos ListofPhotos = null;
    PhotoDbHelper bdLocalPhotos;
    String PhotoActual;


    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK)
        {
            Bundle extras = data.getExtras();
            bmp = (Bitmap)extras.get("data");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
            byte[] b = baos.toByteArray();
            String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
            Photo photo = new Photo(idService, ListPhotos.get(actualPhoto-1).IdPhotoCatalog, encodedImage);
            PhotoDbHelper bdLocalPhotos = new PhotoDbHelper(getApplicationContext());
            bdLocalPhotos.savePhoto(photo);

            if(actualPhoto == qtyPhotos)
            {
                //findViewById(R.id.btnEndService).setEnabled(false);
                findViewById(R.id.takePhoto).setEnabled(false);
            }
            else
            {
                actualPhoto = actualPhoto + 1;
            }
            /*SendPhoto sphoto = new SendPhoto(idService, encodedImage, idType);
            try
            {sphoto.execute();}
            catch (Exception e)
            {
                String error = e.getMessage();
            }*/
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savephotosservice);

        Intent intent = getIntent();

        idTechnician = intent.getIntExtra("IdTecnico",0);
        idService = intent.getIntExtra("IdServicio",0);
        status = intent.getIntExtra("Status",0);
        idType = intent.getIntExtra("IdType",0);

        final Button takePhoto = (Button)findViewById(R.id.takePhoto);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                PhotoActual = ListPhotos.get(actualPhoto -1).PhotoDescription;
                Toast.makeText(getApplicationContext(), String.format("Foto a Tomar Número " + PhotoActual), Toast.LENGTH_SHORT).show();
                Intent intentCamera =  new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intentCamera, cons);
            }
        });
        final Button btn_EndService = (Button) findViewById(R.id.btnEndService);
        btn_EndService.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                RegisterService terminado = new RegisterService(idTechnician, idService,6);
                try{
                    terminado.execute().get();
                }catch (InterruptedException e){
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                File dir = getFilesDir();
                File file = new File(dir,"activeService.txt");
                boolean deleted = file.delete();

                Toast.makeText(getApplicationContext(), "Servicio Finalizado", Toast.LENGTH_SHORT).show();
                Intent LogOut = new Intent(getApplicationContext(), ToDoServices.class);
                LogOut.putExtra("IdTecnico",idTechnician);
                startActivity(LogOut);
            }
        });
    }

    @Override
    protected void onStart()
    {

        super.onStart();
        getListOfPhotos getListOfPhotos = new getListOfPhotos(idType);
        try
        {
            ListPhotos = getListOfPhotos.execute().get();
        }
        catch (Exception ex)
        {
            error = ex.getMessage();
        }
        qtyPhotos = ListPhotos.size();
        PhotoDbHelper bdLocalPhotos = new PhotoDbHelper(getApplicationContext());
        Cursor cp = bdLocalPhotos.getPhotoService(String.valueOf(idService));
        int localPhotos = cp.getCount();
        if(localPhotos != 0)
        {
            actualPhoto = localPhotos + 1;
        }
        PhotoActual = ListPhotos.get(actualPhoto -1).PhotoDescription;
        Toast.makeText(getApplicationContext(), String.format("Foto a Tomar Número " + PhotoActual), Toast.LENGTH_SHORT).show();
    }


    public class getListOfPhotos extends AsyncTask<Void, Void, List<PhotoCatalog>> {

        private List<PhotoCatalog> temp;
        public int IdType;
        String z = "";

        getListOfPhotos(int idType) {
            IdType = idType;

        }

        @Override
        protected List<PhotoCatalog> doInBackground(Void... params)
        {
            Connection con= null;
            try {
                con = DBConnection.getInstance().getConnection();
                if (con == null) {
                    error = "Error en la Conexión con SQL server";
                } else {

                    String query = "SELECT Elara_S_PhotoCatalog.[IdPhotoCatalog], Elara_S_PhotoCatalog.[PhotoDescription] FROM Elara_S_PhotoCatalog LEFT JOIN Elara_S_PhotosPerService ON Elara_S_PhotoCatalog.IdPhotoCatalog = Elara_S_PhotosPerService.IdPhotoCatalog WHERE Elara_S_PhotosPerService.IdServiceType =  " + idType + ";";
                    Statement stmt2 = con.createStatement();
                    ResultSet result = stmt2.executeQuery(query);
                    int index = 0;
                    temp = new ArrayList<>();
                    while(result.next())
                        {
                          PhotoCatalog tempr = new PhotoCatalog();
                            tempr.IdPhotoCatalog = result.getInt(1);
                            tempr.PhotoDescription = result.getString(2);
                            temp.add(index,tempr);
                            index++;
                        }


                }
            } catch (Exception ex){
                error = ex.getMessage();
            }
            return temp;
        }

        @Override
        protected void onPostExecute(final List<PhotoCatalog> temp)
        {
            super.onPostExecute(temp);
        }

    }

}