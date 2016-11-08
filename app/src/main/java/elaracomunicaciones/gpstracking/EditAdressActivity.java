package elaracomunicaciones.gpstracking;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditAdressActivity extends AppCompatActivity {

    private  Button btn_cancel, btn_save;
    private int idTechnician = 0;
    private int idService = 0;
    private int status = 0;
    private String Reference = "";

    EditText Calle, NoExterior, NoInterior, Colonia, CP ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editadress);

        Intent intent = getIntent();

        idTechnician = intent.getIntExtra("IdTecnico",0);
        idService = intent.getIntExtra("IdServicio",0);
        status = intent.getIntExtra("Status",0);
        Reference = intent.getStringExtra("reference");

        Calle = (EditText) findViewById(R.id.tb_Calle);
        NoExterior = (EditText) findViewById(R.id.tb_NoExt);
        NoInterior = (EditText) findViewById(R.id.tb_NoInt);
        Colonia = (EditText) findViewById(R.id.tb_Colonia);
        CP = (EditText) findViewById(R.id.tb_CP);


        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent Back = new Intent(getApplicationContext(), StartService.class);

                Back.putExtra("IdTecnico",idTechnician);
                Back.putExtra("IdServicio",idService);
                Back.putExtra("reference",Reference);
                Back.putExtra("Status", status);

                startActivity(Back);
            }
        });

        btn_save = (Button) findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent Back = new Intent(getApplicationContext(), StartService.class);
                final String calle = Calle.getText().toString();
                final String NoExt = NoExterior.getText().toString();
                final String NoInt = NoInterior.getText().toString();
                final String Col = Colonia.getText().toString();
                final int CPos = Integer.parseInt(CP.getText().toString());
                SendAddress sendA = new SendAddress(idService, calle, NoExt, NoInt, Col, CPos);
                sendA.execute();
                Toast.makeText(getApplicationContext(), "Dirección Guardada", Toast.LENGTH_SHORT).show();
                Back.putExtra("IdTecnico",idTechnician);
                Back.putExtra("IdServicio",idService);
                Back.putExtra("reference",Reference);
                Back.putExtra("Status", status);

                startActivity(Back);
            }
        });
    }
}
