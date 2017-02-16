package elaracomunicaciones.gpstracking.Utils;

/**
 * Created by luis aranda on 11/10/2016.
 */
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.Tag;
import android.os.IBinder;
import android.util.Log;

public class CheckConnection extends Service
{
    public CheckConnection()
    {

    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        Log.d("Tag","Servicio Creado");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d("Tag", "Servicio iniciado...");

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy()
    {
        Log.d("Tag","Servicio destruido...");
    }

    public static boolean verificaConexion(Context context) {

        boolean connected = false;

        ConnectivityManager connec = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Recupera todas las redes (tanto móviles como wifi)
        NetworkInfo[] redes = connec.getAllNetworkInfo();

        for (int i = 0; i < redes.length; i++) {
            // Si alguna red tiene conexión, se devuelve true
            if (redes[i].getState() == NetworkInfo.State.CONNECTED) {
                connected = true;
            }
        }
        return connected;
    }

    public Boolean isOnlineNet() {

        try {
            Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.es");

            int val           = p.waitFor();
            boolean reachable = (val == 0);
            return reachable;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }
}

