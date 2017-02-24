package elaracomunicaciones.gpstracking.Activities;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.Calendar;

import elaracomunicaciones.gpstracking.R;
import elaracomunicaciones.gpstracking.Services.SendingService;
import elaracomunicaciones.gpstracking.Utils.CheckConnection;
import elaracomunicaciones.gpstracking.Utils.SaveStatus;
import elaracomunicaciones.gpstracking.Utils.SendUbication;
import elaracomunicaciones.gpstracking.Models.Tracking;
import elaracomunicaciones.gpstracking.Models.TrackingDbHelper;


public class StartService extends AppCompatActivity
{

    /* Declaración de variables globales */

    TextView mensaje1;
    TextView mensaje2;

    private int idTechnician = 0;
    private int idVsatService = 0;
    private int idService = 0;
    private int idStatus = 0;
    private int idType = 0;
    private String elaraReference = "";
    private boolean editAddress = false;
    private Button btnStatus, btnEndService, btnEditAddress;
    private double latitude = -1;
    private double longitude = -1;
    private TextView tbCoordinates;
    LocationManager mlocManager = null;
    Localizacion Local =  null;
    /* Inicialización de la actividad */

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        Local = new Localizacion();
        mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        super.onCreate(savedInstanceState);

         /* Se liga la activity con su vista */

        setContentView(R.layout.activity_start_service);


        boolean idRunning = isMyServiceRunning(SendingService.class);

        if(!idRunning)
            startService(new Intent(this, SendingService.class));

        /* Inicialización de controles de la vista */

        mensaje1 = (TextView) findViewById(R.id.tbReference);
        mensaje2 = (TextView) findViewById(R.id.tbDirection);
        tbCoordinates = (TextView) findViewById(R.id.tbCoordinates);

         /* Se obtienen los parámetros del intent */

        Intent intent = getIntent();

        idTechnician = intent.getIntExtra("idTechnician",0);
        idService = intent.getIntExtra("idService",0);
        idStatus = intent.getIntExtra("idStatus",0);
        elaraReference = intent.getStringExtra("elaraReference");
        idType = intent.getIntExtra("idType",0);

        editAddress = intent.getBooleanExtra("editAddress",false);

        /* Id para actualizar las coordenadas del sitio */
        idVsatService = intent.getIntExtra("idVsatService",0);

        if(idStatus == 3){
            Intent SavePhotos = new Intent(getApplicationContext(), SavePhotosService.class);
            SavePhotos.putExtra("idTechnician",idTechnician);
            SavePhotos.putExtra("idService",idService);
            SavePhotos.putExtra("idStatus",3);
            SavePhotos.putExtra("idType", idType);
            startActivity(SavePhotos);
            finish();
        }

        btnStatus = (Button) findViewById(R.id.btnStatus);
        btnEndService = (Button) findViewById(R.id.btnServicioInterrumpido);
        btnEditAddress = (Button) findViewById(R.id.btnEditAddress);

        if(editAddress){
            btnEditAddress.setVisibility(View.INVISIBLE);;
        }
        if(idStatus == 2)
        {
            btnStatus.setActivated(true);
        }

        final Context con  = this;

        btnEditAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String msg = "¿Estás seguro de editar la dirección del sitio? " +
                        "La información que ingreses se guardará para aprobación.";

                new AlertDialog.Builder(con)
                        .setMessage(msg)
                        .setCancelable(false)
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                Intent Edit = new Intent(getApplicationContext(), EditAddressActivity.class);

                                Edit.putExtra("idTechnician",idTechnician);
                                Edit.putExtra("elaraReference",elaraReference);
                                startActivity(Edit);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });


        btnStatus.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int idService = 0;
                int idStatus = 0;
                boolean FileExist = false;
                String[] files = fileList();

                for (String file : files) {

                    if(file.equals("activeService.txt")){
                        try{
                            InputStreamReader fraw = new InputStreamReader(openFileInput("activeService.txt"));;
                            BufferedReader brin = new BufferedReader(fraw);
                            idService= Integer.parseInt(brin.readLine());
                            idStatus = Integer.parseInt(brin.readLine());
                            fraw.close();
                        }
                        catch (Exception ex)
                        {
                            Log.e("Ficheros", "Error al leer fichero desde recurso raw");
                        }
                    }
                }

                switch (idStatus)
                {
                    case 1:
                        idStatus = 2;
                        writeFile(idService,idStatus);
                        btnStatus.setText("Ya entré");
                        btnStatus.setActivated(true);
                        break;
                    case 2:
                        idStatus = 3;
                        writeFile(idService,idStatus);

                        mlocManager.removeUpdates(Local);

                        Intent Photos = new Intent(getApplicationContext(), SavePhotosService.class);
                        Photos.putExtra("idTechnician",idTechnician);
                        Photos.putExtra("idService",idService);
                        Photos.putExtra("idStatus",3);
                        Photos.putExtra("idType", idType);
                        startActivity(Photos);
                        finish();
                        break;
                    default:
                        break;
                }

                String lat = latitude == -1 ? "null" : String.valueOf(latitude);
                String lon = longitude == -1 ? "null" : String.valueOf(longitude);

                CheckConnection con = new CheckConnection();

                boolean isOnline = con.isOnlineNet();

                if(isOnline)
                {
                    SaveStatus saveStatus = new SaveStatus(idService, idStatus, lat, lon);
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

            }
        });

        btnStatus.setEnabled(false);

        //Estatus del Servicio - TERMINAR SERVICIO (Rojo)

        btnEndService.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view)
            {
                String msg = "¿Estás seguro de finalizar el servicio, este se registrará como visita fallida?";

                new AlertDialog.Builder(con)
                        .setMessage(msg)
                        .setCancelable(false)
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                String lat = latitude == -1 ? "null" : String.valueOf(latitude);
                                String lon = longitude == -1 ? "null" : String.valueOf(longitude);

                                SaveStatus saveStatus = new SaveStatus(idService,6, lat, lon);
                                try{
                                    saveStatus.execute().get();
                                }catch (InterruptedException e){
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }

                                File dir = getFilesDir();
                                File file = new File(dir,"activeService.txt");
                                boolean deleted = file.delete();

                                mlocManager.removeUpdates(Local);

                                Toast.makeText(getApplicationContext(), "Visita Fallida", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), ToDoServices.class);
                                intent.putExtra("idTechnician",idTechnician);
                                startActivity(intent);

                                finish();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });


		/* Uso de la clase LocationManager para obtener la localizacion del GPS */


        Local.setMainActivity(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        if (!mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            Toast.makeText(this, "Activa la ubicación de tu equipo, para poder iniciar el servicio.", Toast.LENGTH_LONG).show();
        }

        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 0,
                (LocationListener) Local);

        mensaje1.setText("Localizando tu ubicación ...");
        mensaje2.setText("");



    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void writeFile(int idService, int i) {
        try {
            OutputStreamWriter fout =
                    new OutputStreamWriter(
                            openFileOutput("activeService.txt", Context.MODE_PRIVATE));

            fout.write(String.valueOf(idService));
            fout.write("\n" + i);
            fout.close();
        } catch (Exception ex) {
            Log.e("Ficheros", "Error al escribir fichero a memoria interna");
        }
    }

    public void setLocation(Location loc)
    {
        CheckConnection con = new CheckConnection();

        boolean isOnline = con.isOnlineNet();

        if(isOnline)
        {
            if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
                try {
                    Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                    List<Address> list = geocoder.getFromLocation(
                            loc.getLatitude(), loc.getLongitude(), 1);
                    if (!list.isEmpty()) {
                        Address DirCalle = list.get(0);
                        mensaje2.setText(DirCalle.getAddressLine(0));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else
        {
            mensaje2.setText("Sin Conexión");
        }
    }

    /* Aqui empieza la Clase Localizacion */
    public class Localizacion implements LocationListener {
        StartService mainActivity;
        TrackingDbHelper bdLocal = new TrackingDbHelper(getApplicationContext());

        public StartService getMainActivity() {
            return mainActivity;
        }

        public void setMainActivity(StartService mainActivity) {
            this.mainActivity = mainActivity;
        }

        @Override
        public void onLocationChanged(Location loc) {

            // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
            // debido a la deteccion de un cambio de ubicacion

            latitude = loc.getLatitude();
            longitude = loc.getLongitude();

            tbCoordinates.setText("Lat " + Math.floor(latitude * 10000) / 10000 + " / " + "Lon " + Math.floor(longitude * 10000) / 10000);

            btnStatus.setEnabled(true);

            CheckConnection con = new CheckConnection();

            boolean isOnline = con.isOnlineNet();

            if(isOnline)
            {
                SendUbication su = new SendUbication(idService,loc.getLongitude(), loc.getLatitude());
                su.execute();
            }
            else
            {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String date = df.format(Calendar.getInstance().getTime());

                Tracking trackingNow = new Tracking(idService,date, loc.getLatitude(),loc.getLongitude());
                bdLocal.saveTracking(trackingNow);
                Toast.makeText(getApplicationContext(), String.format("Registro en BD Interna"), Toast.LENGTH_SHORT).show();
            }

            String Text = elaraReference;
            mensaje1.setText(Text);
            this.mainActivity.setLocation(loc);
        }

        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado
            mensaje1.setText("GPS Desactivado");
        }

        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado
            mensaje1.setText("GPS Activado");
        }

        @Override
        public void onStatusChanged(String provider, int idStatus, Bundle extras) {
            // Este metodo se ejecuta cada vez que se detecta un cambio en el
            // idStatus del proveedor de localizacion (GPS)
            // Los diferentes idStatus son:
            // OUT_OF_SERVICE -> Si el proveedor esta fuera de servicio
            // TEMPORARILY_UNAVAILABLE -> Temporalmente no disponible pero se
            // espera que este disponible en breve
            // AVAILABLE -> Disponible
        }

    }/* Fin de la clase localizacion */

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //preventing default implementation previous to android.os.Build.VERSION_CODES.ECLAIR
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void broadcastIntent(View view)
    {
        Intent intent = new Intent();
        intent.setAction("com.trackinggps.CUSTOM_INTENT");
        sendBroadcast(intent);
    }

}
