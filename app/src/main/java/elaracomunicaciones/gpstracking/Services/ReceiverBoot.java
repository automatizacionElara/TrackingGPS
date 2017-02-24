package elaracomunicaciones.gpstracking.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import elaracomunicaciones.gpstracking.Activities.SplashActivity;

/**
 * Created by sandro manzano on 21/02/2017.
 */

public class ReceiverBoot extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // LANZAR SERVICIO
        Intent serviceIntent = new Intent(context, SendingService.class);
        context.startService(serviceIntent);
    }

}
