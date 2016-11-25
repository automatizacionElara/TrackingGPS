package elaracomunicaciones.gpstracking;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.Manifest;
import android.content.Context;
import android.widget.Toast;
import android.content.IntentFilter;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.Calendar;


/**
 * Created by luis aranda on 23/11/2016.
 */

public class SendBDLocal extends Activity {

    IntentFilter myFilter;
    CheckConnection isOnline = new CheckConnection();
    TrackingDbHelper bdLocal = new TrackingDbHelper(getApplicationContext());
    PhotoDbHelper bdPhotos = new PhotoDbHelper(getApplicationContext());
    boolean answer = isOnline.isOnlineNet();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        TrackingDbHelper helper = new TrackingDbHelper(getApplicationContext());
        Cursor c = helper.getAllTracking();
        if (answer == true)

        {
            if (c.moveToFirst()) {
                do {
                    String IdTracking = c.getString(0);
                    String IdService = c.getString(1);
                    String DateTracking = c.getString(2);
                    String Latitude = c.getString(3);
                    String Longitude = c.getString(4);
                    Toast.makeText(getApplicationContext(), "Ubicación Enviada Después", Toast.LENGTH_SHORT).show();
                    SendUbication su = new SendUbication(Integer.parseInt(IdService), Double.parseDouble(Longitude), Double.parseDouble(Latitude), DateTracking);
                    su.execute();
                    bdLocal.deleteTracking(IdTracking);
                } while (c.moveToNext());
            }
        }

        PhotoDbHelper phelper = new PhotoDbHelper(getApplicationContext());
        Cursor p = phelper.getAllPhotos();
        if (answer == true)

        {
            if (p.moveToFirst()) {
                do {
                    String IdPhoto = p.getString(0);
                    int IdService = p.getInt(1);
                    int idType = p.getInt(2);
                    String Photo = p.getString(3);
                    SendPhoto sp = new SendPhoto(IdService, Photo, idType);
                    Toast.makeText(getApplicationContext(), "Foto Enviada", Toast.LENGTH_SHORT).show();
                    sp.execute();
                    bdPhotos.deletePhoto(IdPhoto);
                } while (p.moveToNext());
            }
        }
    }

    public void broadcastIntent(View view)
    {
        Intent intent = new Intent();
        intent.setAction("com.trackinggps.CUSTOM_INTENT");
        sendBroadcast(intent);
    }
}
