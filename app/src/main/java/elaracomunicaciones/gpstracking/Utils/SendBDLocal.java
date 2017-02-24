package elaracomunicaciones.gpstracking.Utils;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;
import android.content.IntentFilter;

import elaracomunicaciones.gpstracking.Models.PhotoDbHelper;
import elaracomunicaciones.gpstracking.Models.TrackingDbHelper;


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
                    String date = p.getString(4);

                    SendPhoto sp = new SendPhoto(IdService, Photo, idType, date);
                    Toast.makeText(getApplicationContext(), "Foto Enviada", Toast.LENGTH_SHORT).show();
                    sp.execute();
                    bdPhotos.deletePhoto(IdPhoto);
                } while (p.moveToNext());
            }
        }
    }
}
