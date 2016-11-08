package elaracomunicaciones.gpstracking;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
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
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.Calendar;


public class StartService extends AppCompatActivity {

    TextView mensaje1;
    TextView mensaje2;

    boolean EndService = false; // ROJO

    private int idTechnician = 0;
    private int idService = 0;
    private int status = 0;
    private String Reference = "";
    private boolean Edit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent i = new Intent();
        i.setAction(".CREATE_RECEIVER_APP");
        this.sendBroadcast(i);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_service);

        Intent intent = getIntent();

        idTechnician = intent.getIntExtra("IdTecnico",0);
        idService = intent.getIntExtra("IdServicio",0);
        status = intent.getIntExtra("Status",0);
        Reference = intent.getStringExtra("reference");
        Edit = intent.getBooleanExtra("EditAddress",false);



        if(Reference != null){
            try
            {
                OutputStreamWriter site=
                        new OutputStreamWriter(
                                openFileOutput("sitio.txt", Context.MODE_PRIVATE));

                site.write(String.valueOf(Reference));
                site.close();
            }
            catch (Exception ex)
            {
                Log.e("Ficheros", "Error al escribir fichero a memoria interna");
            }
        }


        mensaje1 = (TextView) findViewById(R.id.tbReference);
        mensaje2 = (TextView) findViewById(R.id.tbDirection);

        if(status == 1) {
            RegisterService su = new RegisterService(idTechnician, idService, 1);

            try {
                su.execute().get();
                writeFile(idService,1);
                Toast.makeText(getApplicationContext(), "Servicio Registrado", Toast.LENGTH_SHORT).show();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        String IdTecnico = "";
        String IdService = "";
        String Status = "";
        String SitioNombre = "";
        boolean FileExist = false;
        String[] files = fileList();

        for (String file : files) {
            if (file.equals("access.txt")) {
                try
                {
                    InputStreamReader fraw = new InputStreamReader(openFileInput("access.txt"));;
                    BufferedReader brin = new BufferedReader(fraw);
                    IdTecnico= brin.readLine();
                    fraw.close();
                }
                catch (Exception ex)
                {
                    Log.e("Ficheros", "Error al leer fichero desde recurso raw");
                }
                idTechnician = Integer.parseInt(IdTecnico);
                FileExist = true;
            }

            if (file.equals("sitio.txt")) {
                try
                {
                    InputStreamReader fraw = new InputStreamReader(openFileInput("sitio.txt"));;
                    BufferedReader brin = new BufferedReader(fraw);
                    SitioNombre= brin.readLine();
                    fraw.close();
                }
                catch (Exception ex)
                {
                    Log.e("Ficheros", "Error al leer fichero desde recurso raw");
                }
                Reference = SitioNombre;
            }

            if(file.equals("activeService.txt")){
                try{
                    InputStreamReader fraw = new InputStreamReader(openFileInput("activeService.txt"));;
                    BufferedReader brin = new BufferedReader(fraw);
                    IdService= brin.readLine();
                    Status = brin.readLine();
                    fraw.close();
                }
                catch (Exception ex)
                {
                    Log.e("Ficheros", "Error al leer fichero desde recurso raw");
                }
            }
        }


        final int estado = Integer.parseInt(Status);

        if(estado == 3){
            Intent SavePhotos = new Intent(getApplicationContext(), SavePhotosService.class);
            SavePhotos.putExtra("IdTecnico",idTechnician);
            SavePhotos.putExtra("IdServicio",idService);
            SavePhotos.putExtra("Status",3);
            startActivity(SavePhotos);
        }
        //BOTONES
        final Button btn_Status = (Button) findViewById(R.id.btnStatus);
        final Button btn_EndService = (Button) findViewById(R.id.btnServicioInterrumpido);
        final Button btn_EditAdrress = (Button) findViewById(R.id.btnEditAddress);

        if(Edit){
            btn_EditAdrress.setVisibility(View.INVISIBLE);;
        }
        if(status == 2){
            btn_Status.setBackgroundColor(Color.rgb(0,166,255));
        }

        btn_EditAdrress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(getApplicationContext(), "Editar Dirección", Toast.LENGTH_SHORT).show();
                Intent Edit = new Intent(getApplicationContext(), EditAdressActivity.class);


                Edit.putExtra("IdTecnico",idTechnician);
                Edit.putExtra("IdServicio",idService);
                Edit.putExtra("reference",Reference);
                Edit.putExtra("Status", status);
                startActivity(Edit);
            }
        });


        btn_Status.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String IdService = "";
                String Status = "";
                boolean FileExist = false;
                String[] files = fileList();

                for (String file : files) {

                    if(file.equals("activeService.txt")){
                        try{
                            InputStreamReader fraw = new InputStreamReader(openFileInput("activeService.txt"));;
                            BufferedReader brin = new BufferedReader(fraw);
                            IdService= brin.readLine();
                            Status = brin.readLine();
                            fraw.close();
                        }
                        catch (Exception ex)
                        {
                            Log.e("Ficheros", "Error al leer fichero desde recurso raw");
                        }
                    }
                }

                int es = Integer.parseInt(Status);
                switch (es)
                {
                    case 1:
                        status = 2;
                        writeFile(idService,status);
                        btn_Status.setText("Ya entré");
                        btn_Status.setBackgroundColor(Color.rgb(0,166,255));
                        break;
                    case 2:
                        status = 3;
                        writeFile(idService,status);
                        Intent Photos = new Intent(getApplicationContext(), SavePhotosService.class);
                        Photos.putExtra("IdTecnico",idTechnician);
                        Photos.putExtra("IdServicio",idService);
                        Photos.putExtra("Status",3);
                        startActivity(Photos);
                        break;
                    default:
                        break;
                }

                int seguimiento = status;

                RegisterService status = new RegisterService(idTechnician, idService, seguimiento);
                try{
                    status.execute().get();
                }catch (InterruptedException e){
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });


        //Estatus del Servicio - TERMINAR SERVICIO (Rojo)

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
                EndService = true;
                LogOut.putExtra("IdTecnico",idTechnician);
                startActivity(LogOut);
            }
        });


		/* Uso de la clase LocationManager para obtener la localizacion del GPS */
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Localizacion Local = new Localizacion();
        Local.setMainActivity(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
            Toast.makeText(this, "Activar GPS, para poder iniciar el servicio", Toast.LENGTH_LONG).show();
        }

        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0,
                (LocationListener) Local);

        mensaje1.setText("Localizando tu ubicación ...");
        mensaje2.setText("");
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

    public void setLocation(Location loc) {
        //Obtener la direccion de la calle a partir de la latitud y la longitud
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

    /* Aqui empieza la Clase Localizacion */
    public class Localizacion implements LocationListener {
        StartService mainActivity;
        CheckConnection isOnline = new CheckConnection();
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

            loc.getLatitude();
            loc.getLongitude();
            boolean answer = isOnline.isOnlineNet();

            if(!EndService)
            {

                if(answer == true)
                {
                    Toast.makeText(getApplicationContext(), "Ubicación Enviada", Toast.LENGTH_SHORT).show();
                    SendUbication su = new SendUbication(idService,loc.getLongitude(), loc.getLatitude());
                    su.execute();
                }
                else
                {
                    Calendar calander = Calendar.getInstance();
                    int Day = calander.get(Calendar.DAY_OF_MONTH);
                    int Month = calander.get(Calendar.MONTH) + 1;
                    int Year = calander.get(Calendar.YEAR);
                    int Hour = calander.get(Calendar.HOUR);
                    int Minute = calander.get(Calendar.MINUTE);
                    int Second = calander.get(Calendar.SECOND);
                    String dateNow = String.format(Year + "-" + Month + "-" + Day + " " + Hour + ":" + Minute + ":" + Second);
                    Tracking trackingNow = new Tracking(idService,dateNow, loc.getLatitude(),loc.getLongitude());
                    bdLocal.saveTracking(trackingNow);
                    Toast.makeText(getApplicationContext(), String.format("Registro en BD Interna"), Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                TrackingDbHelper helper = new TrackingDbHelper(getApplicationContext());
                Cursor c = helper.getAllTracking();
                if(answer == true)
                {
                    if(c.moveToFirst())
                    {
                        do
                        {
                            String IdTracking = c.getString(0);
                            String IdService = c.getString(1);
                            String DateTracking = c.getString(2);
                            String Latitude = c.getString(3) ;
                            String Longitude = c.getString(4);
                            Toast.makeText(getApplicationContext(), "Ubicación Enviada Después", Toast.LENGTH_SHORT).show();
                            SendUbication su = new SendUbication(Integer.parseInt(IdService),Double.parseDouble(Longitude),Double.parseDouble(Latitude), DateTracking);
                            su.execute();
                            bdLocal.deleteTracking(IdTracking);
                        }while(c.moveToNext());
                    }
                }
            }


                String Coordenadas = "Lat = " + loc.getLatitude() + " Long = " + loc.getLongitude();
                String Text = Reference;
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
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // Este metodo se ejecuta cada vez que se detecta un cambio en el
            // status del proveedor de localizacion (GPS)
            // Los diferentes Status son:
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
}
