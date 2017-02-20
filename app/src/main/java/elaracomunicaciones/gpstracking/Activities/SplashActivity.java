package elaracomunicaciones.gpstracking.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

import elaracomunicaciones.gpstracking.R;

public class SplashActivity extends AppCompatActivity {

    // Set the duration of the splash screen
    private static final long SPLASH_SCREEN_DELAY = 3000;
    private static final String EMPTY_STRING = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        /* Se configura la pantalla de forma vertical */

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        /* Se aculta el título de la pantalla */

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        /* Se crea el splash del tamaño de toda la pantalla */

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        /* Se liga la activity con su vista */

        setContentView(R.layout.activity_splash);

        /* Inicia la creación de la tarea a ejecutar que se realiza mientras se muestra el splash */

        TimerTask task = new TimerTask()
        {
            @Override
            public void run() {

                /* Declaración e inicialización de variables */

                String idTechnician = EMPTY_STRING;
                String idService = EMPTY_STRING;
                String idStatus = EMPTY_STRING;
                int idType = 0;
                String elaraReference = EMPTY_STRING;

                boolean fileExist = false;

                /* Obtención de la lista de archivos existentes con la información del usuario logeado
                 * en la aplicación en caso de un cierre inesperado o que el equipo se apagó */

                String[] files = fileList();

                for (String file : files) {
                    if (file.equals("access.txt")) {
                        try
                        {
                            InputStreamReader fraw = new InputStreamReader(openFileInput("access.txt"));;
                            BufferedReader brin = new BufferedReader(fraw);
                            idTechnician= brin.readLine();
                            fraw.close();
                        }
                        catch (Exception ex)
                        {
                            Log.e("Ficheros", "Error al leer fichero desde recurso raw");
                        }
                        fileExist = true;
                    }

                    /* Obtención de la lista de archivos existentes con la información del servicio actual
                     * en la aplicación en caso de un cierre inesperado o que el equipo se apagó */

                    if(file.equals("activeService.txt")){
                        try{
                            InputStreamReader fraw = new InputStreamReader(openFileInput("activeService.txt"));;
                            BufferedReader brin = new BufferedReader(fraw);
                            idService= brin.readLine();
                            idStatus = brin.readLine();
                            fraw.close();
                        }
                        catch (Exception ex)
                        {
                            Log.e("Ficheros", "Error al leer fichero desde recurso raw");
                        }
                    }

                    if (file.equals("sitio.txt")) {
                        try
                        {
                            InputStreamReader fraw = new InputStreamReader(openFileInput("sitio.txt"));;
                            BufferedReader brin = new BufferedReader(fraw);
                            elaraReference = brin.readLine();
                            idType = Integer.parseInt(brin.readLine());

                            fraw.close();
                        }
                        catch (Exception ex)
                        {
                            Log.e("Ficheros", "Error al leer fichero desde recurso raw");
                        }
                    }
                }


                if(!fileExist)
                {
                    /* Si no existe el archivo con el registro del usuario loggeado
                     * se ejecuta la activity de inicio de sesión */

                    Intent mainIntent = new Intent().setClass(SplashActivity.this, LoginActivity.class);
                    startActivity(mainIntent);
                }
                else
                {
                    /* Si existe el archivo con el registro del usuario loggeado
                     * se valida si la aplicación se encontraba en ejecución de un servicio */

                    if(idService != "")
                    {
                        /* Si se encontraba un servicio en ejecución se ejecuta la activity del seguimiento
                         * del servicio y se le pasan los parámetros necesario */

                        Intent StartService = new Intent(getApplicationContext(), elaracomunicaciones.gpstracking.Activities.StartService.class);
                        StartService.putExtra("idTechnician", Integer.parseInt(idTechnician));
                        StartService.putExtra("idService", Integer.parseInt(idService));
                        StartService.putExtra("idStatus", Integer.parseInt(idStatus));
                        StartService.putExtra("elaraReference", elaraReference);
                        StartService.putExtra("idType", idType);
                        startActivity(StartService);
                    }
                    else
                    {
                        /* Si no encontraba un servicio en ejecución se ejecuta la activity
                         * que muestra todos los servicios disponibles para realizar */

                        Intent ListServices = new Intent(getApplicationContext(), ToDoServices.class);
                        ListServices.putExtra("idTechnician", Integer.parseInt(idTechnician));
                        startActivity(ListServices);
                    }
                }

                /* Se cierra la activity de splash para que no se puede
                 * regresar con el botón del celular */

                finish();
            }
        };

        /* Se crea un timer que ejecute el splash con un retardo configurado */

        Timer timer = new Timer();
        timer.schedule(task, SPLASH_SCREEN_DELAY);
    }

}
