package elaracomunicaciones.gpstracking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {
    public MyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Toast.makeText(context, "Servicio iniciado desde BroadcastReceiver", Toast.LENGTH_SHORT).show();
        //context.startService(new Intent(context, MyService.class));
        context.startService(new Intent(context, SendBDLocal.class));
    }
}
