package elaracomunicaciones.gpstracking;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    // Set the duration of the splash screen
    private static final long SPLASH_SCREEN_DELAY = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // Set portrait orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        /** Hiding Title bar of this activity screen */
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        /** Making this activity, full screen */
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);


        TimerTask task = new TimerTask() {
            @Override
            public void run() {

                String IdTecnico = "";
                int IdTechnician= 0;
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
                        IdTechnician = Integer.parseInt(IdTecnico.toString());
                        Intent ListServices = new Intent(getApplicationContext(), ToDoServices.class);
                        ListServices.putExtra("IdTecnico", IdTechnician);
                        startActivity(ListServices);
                        FileExist = true;
                    }
                }


                if(!FileExist){
                    // Start the next activity
                    Intent mainIntent = new Intent().setClass(
                            SplashActivity.this, LoginActivity.class);
                    startActivity(mainIntent);
                }

                // Close the activity so the user won't able to go back this
                // activity pressing Back button
                finish();
            }
        };

        // Simulate a long loading process on application startup.
        Timer timer = new Timer();
        timer.schedule(task, SPLASH_SCREEN_DELAY);


    }
}
