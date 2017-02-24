package elaracomunicaciones.gpstracking.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.database.Cursor;

import elaracomunicaciones.gpstracking.Models.Photo;
import elaracomunicaciones.gpstracking.Models.PhotoCatalog;
import elaracomunicaciones.gpstracking.Models.PhotoDbHelper;
import elaracomunicaciones.gpstracking.R;
import elaracomunicaciones.gpstracking.Utils.CheckConnection;
import elaracomunicaciones.gpstracking.Utils.DBConnection;
import elaracomunicaciones.gpstracking.Utils.GetExistentPhotos;
import elaracomunicaciones.gpstracking.Utils.SaveStatus;

public class SavePhotosService extends AppCompatActivity {

    private static final String EMPTY_STRING = "";

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
    private int alreadyPhoto = 0;
    private ArrayList<String> nameOfPhotos;
    private ArrayAdapter<String> adapter;
    private Button btnEndService;
    int actualPhotos;
    boolean flag = true;
    List<PhotoCatalog> ListPhotos;
    Bitmap bmp;
    private String error;
    private getListOfPhotos ListofPhotos = null;
    PhotoDbHelper bdLocalPhotos;
    String PhotoActual;

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
            PhotoDbHelper bdLocalPhotos = new PhotoDbHelper(getApplicationContext());
            if(alreadyPhoto == 0)
            {
                Photo photo = new Photo(idService, ListPhotos.get(actualPhoto).IdPhotoCatalog, PhotoActual, encodedImage, 1);
                bdLocalPhotos.savePhoto(photo);
            }
            else
            {
                bdLocalPhotos.updateAlreadyPhoto(String.valueOf(idService), PhotoActual, encodedImage);
            }
            alreadyPhoto = 0;
            Cursor pt = bdLocalPhotos.getPhotoService(String.valueOf(idService));
            actualPhotos = pt.getCount();

            listViewPhotos = (ListView) findViewById(R.id.listViewPhotos);
            listViewPhotos.setItemChecked(actualPhoto,true);

//            btnEndService.setEnabled(true);
//
//            for(int i= 0; i < listViewPhotos.getChildCount(); i++)
//            {
//                if(!listViewPhotos.isItemChecked(i))
//                {
//                    btnEndService.setEnabled(false);
//                    break;
//                }
//            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

         /* Se liga la activity con su vista */

        setContentView(R.layout.activity_savephotosservice);

         /* Inicialización de controles de la vista */

        btnEndService = (Button)(findViewById(R.id.btnEndService));

        btnEndService.setEnabled(false);

        /* Se obtienen los parámetros del intent */

        Intent intent = getIntent();

        idTechnician = intent.getIntExtra("idTechnician", 0);
        idService = intent.getIntExtra("idService", 0);
        status = intent.getIntExtra("idStatus", 0);
        idType = intent.getIntExtra("idType", 0);

        if (idType != 2 && idType != 5 && idType != 11)
        {
            PhotoDbHelper bdLocalPhotos = new PhotoDbHelper(getApplicationContext());
            ListView listViewPhotos = (ListView) findViewById(R.id.listViewPhotos);
            listViewPhotos.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
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
            adapter = new ArrayAdapter<String>(this, R.layout.list_item, nameOfPhotos);

            listViewPhotos.setAdapter(adapter);


            listViewPhotos.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    ListView list = (ListView) findViewById(R.id.listViewPhotos);

                    list.setItemChecked(position, false);

                    actualPhoto = position;
                    PhotoActual = ListPhotos.get(position).PhotoDescription;

                    alreadyPhoto = 0;
                    Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intentCamera, cons);
                }
            });

        }
        else
        {
            btnEndService.setEnabled(true);
        }

        btnEndService.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {


                CheckConnection con = new CheckConnection();

                boolean isOnline = con.isOnlineNet();

                if(isOnline)
                {
                    SaveStatus saveStatus = new SaveStatus(idService, 5, null, null);
                    try{
                        saveStatus.execute().get();
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    //Escribir en la base interna
                }

                File dir = getFilesDir();
                File file = new File(dir, "activeService.txt");
                boolean deleted = file.delete();

                Toast.makeText(getApplicationContext(), "Servicio Finalizado", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), ToDoServices.class);
                intent.putExtra("idTechnician", idTechnician);
                startActivity(intent);

                }
        });



    }


    @Override
    protected void onStart() {

        super.onStart();

        getListOfPhotos getListOfPhotos = new getListOfPhotos(idType);
        try {
            ListPhotos = getListOfPhotos.execute().get();
        } catch (Exception ex) {
            error = ex.getMessage();
        }

        listViewPhotos = (ListView) findViewById(R.id.listViewPhotos);


        GetExistentPhotos getExistentPhotos = new GetExistentPhotos(idService);

        String[] existentPhotos = null;

        try {
            existentPhotos = getExistentPhotos.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if(existentPhotos != null)
        {
            for (int i = 0; i < existentPhotos.length; i++ )
            {
                if(existentPhotos[i] == null)
                {
                    break;
                }

                for (int j = 0; j < listViewPhotos.getCount(); j++)
                {

                    if(listViewPhotos.getItemAtPosition(j).toString().contentEquals(existentPhotos[i]))
                    {
                        listViewPhotos.setItemChecked(j,true);
                        break;
                    }
                }
            }
        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run()
            {
                btnEndService.setEnabled(true);
                for(int i= 0; i < listViewPhotos.getChildCount(); i++)
                {
                    if(!listViewPhotos.isItemChecked(i))
                    {
                        btnEndService.setEnabled(false);
                        break;
                    }
                }

                /*if (ListPhotos.size() != 0 && flag)
                {
                    int i = 0;
                    while (i < ListPhotos.size())
                    {
                        String searchPhoto = ListPhotos.get(i).PhotoDescription;
                        PhotoDbHelper helper = new PhotoDbHelper(getApplicationContext());

                        Cursor ap = helper.getPhotoByDescription(String.valueOf(idService), searchPhoto);
                        if (ap.getCount() > 0)
                        {
                           listViewPhotos.getChildAt(i).setActivated(true);
                        }
                        i++;
                    }
                    flag = false;
                }*/
            }
        }, 1000);
    }

    @Override
    public void onStop() {
        super.onStop();
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
                con = new DBConnection().getInstance().getConnection();
                if (con == null) {
                    error = "Error en la Conexión con SQL server";
                } else {

                    String query = "SELECT PC.IdPhotoCatalog, PC.PhotoDescription" +
                            " FROM PhotoCatalog PC" +
                            " LEFT JOIN PhotoPerFieldService PFS ON PC.IdPhotoCatalog = PFS.IdPhotoCatalog" +
                            " WHERE PFS.IdServiceType =  " + idType + ";";
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