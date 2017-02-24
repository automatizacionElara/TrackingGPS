package elaracomunicaciones.gpstracking.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

import elaracomunicaciones.gpstracking.R;
import elaracomunicaciones.gpstracking.Utils.SendAddress;

public class EditAddressActivity extends AppCompatActivity {

    private  Button btn_cancel, btn_save;
    private int idTechnician = 0;
    private String elaraReference = "";

    EditText Calle, NoExterior, NoInterior, Colonia, CP ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editadress);

        Intent intent = getIntent();

        idTechnician = intent.getIntExtra("idTechnician",0);
        elaraReference = intent.getStringExtra("elaraReference");

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
                finish();
            }
        });

        btn_save = (Button) findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                final String calle = Calle.getText().toString();
                final String NoExt = NoExterior.getText().toString();
                final String NoInt = NoInterior.getText().toString();
                final String Col = Colonia.getText().toString();
                final String CPos = CP.getText().toString();

                if(calle.contentEquals("") && NoExt.contentEquals("") &&
                        NoInt.contentEquals("") && Col.contentEquals("") &&
                        CPos.contentEquals(""))
                {
                    Toast.makeText(getApplicationContext(), "Ingrese al menos un campo para solicitar el cambio de dirección.", Toast.LENGTH_LONG).show();
                    return;
                }

                SendAddress sendA = new SendAddress(elaraReference, calle, NoExt, NoInt, Col, CPos, idTechnician);

                boolean result = false;

                try
                {
                    result = sendA.execute().get();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                if(result)
                {
                    Toast.makeText(getApplicationContext(), "La dirección de guardó para aprobación.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "El sitio seleccionado ya tiene una dirección en espera de aprobación.", Toast.LENGTH_LONG).show();
                }

                finish();
            }
        });
    }
}
