package elaracomunicaciones.gpstracking;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.concurrent.ExecutionException;

public class SavePhotosService extends AppCompatActivity {

    private static int TAKE_PICTURE = 1;
    private static int SELECT_PICTURE = 2;
    private String name = "";
    int code = TAKE_PICTURE;
    final static int cons = 0;
    private int idTechnician = 0;
    private int idService = 0;
    private int status = 0;
    Bitmap bmp;

    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK)
        {
            Bundle extras = data.getExtras();
            bmp = (Bitmap)extras.get("data");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
            byte[] b = baos.toByteArray();
            String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
            String FECHA = "2016-11-18";
            SendPhoto sphoto = new SendPhoto(idService, encodedImage, FECHA);
            try
            {sphoto.execute();}
            catch (Exception e)
            {
                String error = e.getMessage();
            }
            String prueba = "";

        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savephotosservice);

        Intent intent = getIntent();

        idTechnician = intent.getIntExtra("IdTecnico",0);
        idService = intent.getIntExtra("IdServicio",0);
        status = intent.getIntExtra("Status",0);

        Intent intentCamera =  new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intentCamera, cons);


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
