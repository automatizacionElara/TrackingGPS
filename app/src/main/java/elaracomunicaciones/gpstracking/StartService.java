package elaracomunicaciones.gpstracking;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import net.sourceforge.jtds.jdbc.DateTime;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class StartService extends AppCompatActivity {

    TextView mensaje1;
    TextView mensaje2;
    boolean EndService = false;
    boolean Hellegado = false;
    boolean Recinto = false;
    boolean EnEspera = false;

    private int idTechnician = 0;
    private int idService = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_service);

        Intent intent = getIntent();

        idTechnician = intent.getIntExtra("IdTecnico",0);
        idService = intent.getIntExtra("IdServicio",0);

        mensaje1 = (TextView) findViewById(R.id.mensaje_id);
        mensaje2 = (TextView) findViewById(R.id.mensaje_id2);

        RegisterService su = new RegisterService(idTechnician, idService,1);

        try
        {
            su.execute().get();
            Toast.makeText(getApplicationContext(), "Servicio Registrado", Toast.LENGTH_SHORT).show();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        catch (ExecutionException e)
        {
            e.printStackTrace();
        }


        //Estatus del Servicio - HE LLEGADO
        final Button btn_llegado = (Button) findViewById(R.id.hellegado);
        if(Hellegado){btn_llegado.setEnabled(false);}
        btn_llegado.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                RegisterService llegado = new RegisterService(idTechnician, idService,2);

                try{
                    llegado.execute().get();
                    Toast.makeText(getApplicationContext(), "He llegado al Domicilio", Toast.LENGTH_SHORT).show();
                }catch (InterruptedException e){
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                Hellegado = true;
                btn_llegado.setEnabled(false);
            }
        });

        //Estatus del Servicio - SERVICIO INICIADO
        final Button btn_ServicioIniciado = (Button) findViewById(R.id.recinto);
        if(!Hellegado){btn_ServicioIniciado.setEnabled(true);}else{btn_ServicioIniciado.setEnabled(false);}
        btn_ServicioIniciado.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                RegisterService iniciado = new RegisterService(idTechnician, idService,5);

                try{
                    iniciado.execute().get();
                    Toast.makeText(getApplicationContext(), "Entré al recinto", Toast.LENGTH_SHORT).show();
                }catch (InterruptedException e){
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                Recinto = true;
                btn_ServicioIniciado.setEnabled(false);
            }
        });

        //Estatus del Servicio - EN ESPERA
        final Button btn_Espera = (Button) findViewById(R.id.enespera);
        if(EnEspera){btn_Espera.setEnabled(false);}
        btn_Espera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                RegisterService espera = new RegisterService(idTechnician, idService,3);

                try{
                    espera.execute().get();
                    Toast.makeText(getApplicationContext(), "En espera", Toast.LENGTH_SHORT).show();
                }catch (InterruptedException e){
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                EnEspera = true;
                btn_Espera.setEnabled(false);
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

            if(!EndService){
                Toast.makeText(getApplicationContext(), "Ubicación Enviada", Toast.LENGTH_SHORT).show();
                SendUbication su = new SendUbication(idService,loc.getLongitude(), loc.getLatitude());
                su.execute();
            }


                String Text = "Lat = " + loc.getLatitude() + " Long = " + loc.getLongitude();
                mensaje1.setText(Text);
                this.mainActivity.setLocation(loc);

            Button LogOut = (Button) findViewById(R.id.EndService);
            LogOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RegisterService terminado = new RegisterService(idTechnician, idService,6);

                    try{
                        terminado.execute().get();
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(), "Servicio Finalizado", Toast.LENGTH_SHORT).show();
                    Intent LogOut = new Intent(getApplicationContext(), ToDoServices.class);
                    EndService = true;
                    LogOut.putExtra("IdTecnico",idTechnician);
                    startActivity(LogOut);
                }
            });


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
