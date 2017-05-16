package elaracomunicaciones.gpstracking.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import elaracomunicaciones.gpstracking.Models.PhotoDbHelper;
import elaracomunicaciones.gpstracking.Models.ServiceWorkflow;
import elaracomunicaciones.gpstracking.Models.ServiceWorkflowDbHelper;
import elaracomunicaciones.gpstracking.Models.TrackingDbHelper;
import elaracomunicaciones.gpstracking.Utils.CheckConnection;
import elaracomunicaciones.gpstracking.Utils.SaveStatus;
import elaracomunicaciones.gpstracking.Utils.SendPhoto;
import elaracomunicaciones.gpstracking.Utils.SendUbication;

/**
 * Created by sandro manzano on 21/02/2017.
 */

public class SendingService extends Service
{
    private Context context;
    private boolean isActive = true;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        context = getApplicationContext();
        Toast.makeText(context, "Servicio Activo", Toast.LENGTH_LONG).show();

        new Thread(new Runnable(){
            public void run() {
                // TODO Auto-generated method stub
                while(isActive)
                {
                    CheckConnection checkCon = new CheckConnection();

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    boolean isOnline = checkCon.isOnlineNet();


                    if (isOnline == true)
                    {
                        sendLocation();
                        sendPhotos();
                        sendStatus();
                    }
                }
            }
        }).start();
    }

    private void sendLocation()
    {
        TrackingDbHelper bdLocal = new TrackingDbHelper(getApplicationContext());

        Cursor c = bdLocal.getAllTracking();
        int count = 0;

        if (c.moveToFirst())
        {
            do {
                String IdTracking = c.getString(0);
                String IdService = c.getString(1);
                String DateTracking = c.getString(2);
                String Latitude = c.getString(3);
                String Longitude = c.getString(4);
                SendUbication su = new SendUbication(Integer.parseInt(IdService), Double.parseDouble(Longitude), Double.parseDouble(Latitude), DateTracking);
                su.execute();

                count++;

                bdLocal.deleteTracking(IdTracking);
            } while (c.moveToNext());

            Log.d("X", String.valueOf(count));
        }
    }

    private void sendPhotos()
    {
        PhotoDbHelper bdPhotos = new PhotoDbHelper(getApplicationContext());
        PhotoDbHelper phelper = new PhotoDbHelper(getApplicationContext());
        Cursor p = phelper.getAllPhotos();

        int count = 0;

        if (p.moveToFirst())
        {
            do {
                try {


                    String IdPhoto = p.getString(0);
                    int IdService = p.getInt(1);
                    int idType = p.getInt(2);
                    String Photo = p.getString(3);
                    String date = p.getString(5);

                    SendPhoto sp = new SendPhoto(IdService, Photo, idType, date);

                    sp.execute();

                    count++;

                    bdPhotos.deletePhoto(IdPhoto);
                }
                catch (Exception ex)
                {
                    continue;
                }

            } while (p.moveToNext());

            Log.d("X", String.valueOf(count));
        }
    }

    private void sendStatus()
    {
        ServiceWorkflowDbHelper bdLocal = new ServiceWorkflowDbHelper(getApplicationContext());

        Cursor c = bdLocal.getAllServiceWorkflow();

        int count = 0;

        if (c.moveToFirst())
        {
            do {
                int idService = c.getInt(0);
                int idStatus = c.getInt(1);
                String dateTracking = c.getString(2);
                String latitude = c.getString(3);
                String longitude = c.getString(4);
                SaveStatus saveStatus = new SaveStatus(idService, idStatus, dateTracking, latitude, longitude);
                saveStatus.execute();

                count++;

                bdLocal.deleteServiceWorkflow(idService, idStatus);
            } while (c.moveToNext());

            Log.d("X", String.valueOf(count));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isActive = false;
        Toast.makeText(this, "Servicio Finalizado", Toast.LENGTH_LONG).show();
    }
}
