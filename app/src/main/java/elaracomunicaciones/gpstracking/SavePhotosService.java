package elaracomunicaciones.gpstracking;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.util.concurrent.ExecutionException;

public class SavePhotosService extends AppCompatActivity {

    private int idTechnician = 0;
    private int idService = 0;
    private int status = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savephotosservice);

        Intent intent = getIntent();

        idTechnician = intent.getIntExtra("IdTecnico",0);
        idService = intent.getIntExtra("IdServicio",0);
        status = intent.getIntExtra("Status",0);

        final Button btn_EndService = (Button) findViewById(R.id.btnEndService);
        btn_EndService.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                RegisterService terminado = new RegisterService(idTechnician, idService,6);
                try{
                    terminado.execute().get();
                }catch (InterruptedException e){
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                File dir = getFilesDir();
                File file = new File(dir,"activeService.txt");
                boolean deleted = file.delete();

                Toast.makeText(getApplicationContext(), "Servicio Finalizado", Toast.LENGTH_SHORT).show();
                Intent LogOut = new Intent(getApplicationContext(), ToDoServices.class);
                LogOut.putExtra("IdTecnico",idTechnician);
                startActivity(LogOut);
            }
        });
    }
}
