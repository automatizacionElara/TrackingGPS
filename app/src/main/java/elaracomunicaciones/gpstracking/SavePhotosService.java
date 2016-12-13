package elaracomunicaciones.gpstracking;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
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

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

public class SavePhotosService extends AppCompatActivity {

    private static int TAKE_PICTURE = 1;
    private ListView listViewPhotos;
    private String name = "";
    int code = TAKE_PICTURE;
    final static int cons = 0;
    private int idTechnician = 0;
    private int idService = 0;
    private int idType = 0;
    private int status = 0;
    private int actualPhoto;
    private int qtyPhotos;
    private ArrayList<String> nameOfPhotos;
    private ArrayAdapter<String> adapter;
    List<PhotoCatalog> ListPhotos;
    Bitmap bmp;
    private String error;
    private getListOfPhotos ListofPhotos = null;
    PhotoDbHelper bdLocalPhotos;
    String PhotoActual;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            byte[] b = null;
            Bundle extras = data.getExtras();
            bmp = (Bitmap) extras.get("data");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
            b = baos.toByteArray();
            String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
            Photo photo = new Photo(idService, ListPhotos.get(actualPhoto).IdPhotoCatalog, PhotoActual, encodedImage, 1);
            PhotoDbHelper bdLocalPhotos = new PhotoDbHelper(getApplicationContext());
            bdLocalPhotos.savePhoto(photo);

            Cursor pt = bdLocalPhotos.getPhotoService(String.valueOf(idService));
            int actualPhotos = pt.getCount();
            if (actualPhotos == qtyPhotos) {
                findViewById(R.id.takePhoto).setEnabled(false);
                findViewById(R.id.btnEndService).setEnabled(true);
            } else {
                //StateListItem currItem = actualPhoto;
                Toast.makeText(getApplicationContext(), String.format("Se han tomado " + actualPhotos), Toast.LENGTH_SHORT).show();
                //adapter.remove(PhotoActual);

                //listViewPhotos.deferNotifyDataSetChanged();
            }
            /*if(actualPhoto == (qtyPhotos - 1))
            {
                //findViewById(R.id.btnEndService).setEnabled(false);
                findViewById(R.id.takePhoto).setEnabled(false);
                findViewById(R.id.btnEndService).setEnabled(true);
            }
            else
            {
                actualPhoto = actualPhoto + 1;
                findViewById(R.id.takePhoto).setEnabled(true);
                findViewById(R.id.btnEndService).setEnabled(false);
            }*/

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savephotosservice);


        Intent intent = getIntent();

        idTechnician = intent.getIntExtra("IdTecnico", 0);
        idService = intent.getIntExtra("IdServicio", 0);
        status = intent.getIntExtra("Status", 0);
        idType = intent.getIntExtra("IdType", 0);


        if (idType != 2 && idType != 5 && idType != 11) {
            PhotoDbHelper bdLocalPhotos = new PhotoDbHelper(getApplicationContext());
            //Cursor cp = bdLocalPhotos.getPhotoService(String.valueOf(idService));
            //actualPhoto = cp.getCount();
            //int duda = cp.getPosition();
            ListView listViewPhotos = (ListView) findViewById(R.id.listViewPhotos);
            getListOfPhotos getListOfPhotos = new getListOfPhotos(idType);

            nameOfPhotos = new ArrayList<String>();
            try {
                ListPhotos = getListOfPhotos.execute().get();
            } catch (Exception ex) {
                error = ex.getMessage();
            }

            if (ListPhotos.size() != 0) {
                int i = 0;
                while (i < ListPhotos.size()) {
                    nameOfPhotos.add(ListPhotos.get(i).PhotoDescription);
                    i++;
                }
            }
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, nameOfPhotos);
            listViewPhotos.setAdapter(adapter);
            listViewPhotos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    actualPhoto = position;
                    PhotoActual = ListPhotos.get(position).PhotoDescription;
                    Toast.makeText(getApplicationContext(), String.format("Foto a Tomar " + PhotoActual), Toast.LENGTH_SHORT).show();
                    view.setSelected(true);
                    view.setActivated(true);
                }
            });
            /*listViewPhotos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    view.setSelected(true);
                }
            });*/
        } else {
            findViewById(R.id.takePhoto).setVisibility(View.GONE);
            findViewById(R.id.btnEndService).setEnabled(true);
        }

        final Button takePhoto = (Button) findViewById(R.id.takePhoto);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                /*PhotoDbHelper bdLocalPhotos = new PhotoDbHelper(getApplicationContext());
                Cursor cp = bdLocalPhotos.getPhotoService(String.valueOf(idService));
                actualPhoto = cp.getCount();
                /*if(localPhotos != 0)
                {
                    actualPhoto = localPhotos + 1;
                }*/
                /*if(ListPhotos.size() == 0)
                {
                    getListOfPhotos getListOfPhotos = new getListOfPhotos(idType);
                }
                PhotoActual = ListPhotos.get(actualPhoto).PhotoDescription;
                Toast.makeText(getApplicationContext(), String.format("Foto a Tomar " + PhotoActual), Toast.LENGTH_SHORT).show();*/
                PhotoDbHelper phelper = new PhotoDbHelper(getApplicationContext());
                //Cursor p = phelper.getPhotoService(String.valueOf(idService));
                //Cursor pt = bdLocalPhotos.getPhotoService(String.valueOf(idService));
                Cursor p = phelper.getPhotoByDescription(String.valueOf(idService), PhotoActual);
                if (p.getCount() > 0) {
                    /*int tomada = 0;
                    if(p.moveToFirst())
                    {
                        do
                        {
                            String IdPhoto = p.getString(0);
                            int IdService = p.getInt(1);
                            int idType = p.getInt(2);
                            String Photo = p.getString(3);
                            if(PhotoActual == Photo)
                            { tomada = tomada + 1; }
                        }while (p.moveToNext());

                        if(tomada == 0)
                        {
                            Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intentCamera, cons);
                        }
                        else
                        {*/
                    Toast.makeText(getApplicationContext(), String.format("Foto ya Tomada"), Toast.LENGTH_SHORT).show();
                        /*}
                    }*/
                } else {
                    Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intentCamera, cons);
                }


            }
        });
        final Button btn_EndService = (Button) findViewById(R.id.btnEndService);
        btn_EndService.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                RegisterService terminado = new RegisterService(idTechnician, idService, 6);
                try {
                    terminado.execute().get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                File dir = getFilesDir();
                File file = new File(dir, "activeService.txt");
                boolean deleted = file.delete();

                Toast.makeText(getApplicationContext(), "Servicio Finalizado", Toast.LENGTH_SHORT).show();
                Intent LogOut = new Intent(getApplicationContext(), ToDoServices.class);
                LogOut.putExtra("IdTecnico", idTechnician);
                startActivity(LogOut);
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    @Override
    protected void onStart() {

        super.onStart();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        getListOfPhotos getListOfPhotos = new getListOfPhotos(idType);
        try {
            ListPhotos = getListOfPhotos.execute().get();
        } catch (Exception ex) {
            error = ex.getMessage();
        }
        qtyPhotos = ListPhotos.size();


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("SavePhotosService Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }


    public class getListOfPhotos extends AsyncTask<Void, Void, List<PhotoCatalog>> {

        private List<PhotoCatalog> temp;
        public int IdType;
        String z = "";

        getListOfPhotos(int idType) {
            IdType = idType;

        }

        @Override
        protected List<PhotoCatalog> doInBackground(Void... params) {
            Connection con = null;
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
                    while (result.next()) {
                        PhotoCatalog tempr = new PhotoCatalog();
                        tempr.IdPhotoCatalog = result.getInt(1);
                        tempr.PhotoDescription = result.getString(2);
                        temp.add(index, tempr);
                        index++;
                    }


                }
            } catch (Exception ex) {
                error = ex.getMessage();
            }
            return temp;
        }

        @Override
        protected void onPostExecute(final List<PhotoCatalog> temp) {
            super.onPostExecute(temp);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //preventing default implementation previous to android.os.Build.VERSION_CODES.ECLAIR
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}