package elaracomunicaciones.gpstracking;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.sql.Connection;

public class ToDoServices extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todoservices);

        Button LogOut = (Button) findViewById(R.id.LogOut);
        LogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Sesión Cerrada", Toast.LENGTH_SHORT).show();
                Intent LogOut = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(LogOut);
            }
        });

        Button Checkin = (Button) findViewById(R.id.checkin);
        Checkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Servicio Iniciado", Toast.LENGTH_SHORT).show();
                Intent Checkin = new Intent(getApplicationContext(), StartService.class);
                startActivity(Checkin);
            }
        });
    }
}
