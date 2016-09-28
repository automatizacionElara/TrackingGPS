package elaracomunicaciones.gpstracking;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.sourceforge.jtds.jdbc.cache.SQLCacheKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class ToDoServices extends AppCompatActivity
{
    private List<Service> servicesList = new ArrayList<>();
    private int idTechnician = 0;
    private TextView tbReference, tbTicket, tbETA, tbType;
    private Spinner servicesSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todoservices);
        VerifiedConnection vc = new VerifiedConnection();
        if (!vc.verificaConexion(this)) {
            Toast.makeText(getBaseContext(),
                    "Comprueba tu conexión a Internet. Saliendo ... ", Toast.LENGTH_SHORT)
                    .show();
            this.finish();
        }
        tbReference = (TextView)findViewById(R.id.tbReference);
        tbTicket = (TextView)findViewById(R.id.tbTicket);
        tbETA = (TextView)findViewById(R.id.tbETA);
        tbType = (TextView)findViewById(R.id.tbType);
        servicesSpinner = (Spinner)findViewById(R.id.servicesSpinner);

        Intent inte = getIntent();

        idTechnician = inte.getIntExtra("IdTecnico",0);

        Button LogOut = (Button) findViewById(R.id.LogOut);
        LogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File dir = getFilesDir();
                File file = new File(dir,"access.txt");
                boolean deleted = file.delete();
                Toast.makeText(getApplicationContext(), "Sesión Cerrada", Toast.LENGTH_SHORT).show();
                Intent LogOut = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(LogOut);
            }
        });


        Button Checkin = (Button) findViewById(R.id.checkin);
        Checkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(getApplicationContext(), "Servicio Iniciado", Toast.LENGTH_SHORT).show();
                Intent Checkin = new Intent(getApplicationContext(), StartService.class);

                Service service = servicesList.get(servicesSpinner.getSelectedItemPosition());

                Checkin.putExtra("IdTecnico",idTechnician);
                Checkin.putExtra("IdServicio",service.idService);

                startActivity(Checkin);
            }
        });

        String webServiceResult = "";

        WSSoap client = new WSSoap(webServiceResult);

        try
        {
            webServiceResult = client.execute().get();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        catch (ExecutionException e)
        {
            e.printStackTrace();
        }
        ;

        JSONObject jsonResult = null;
        JSONArray jsonServices = null;

        try
        {
            jsonResult = new JSONObject(webServiceResult);
            jsonServices = jsonResult.getJSONArray("data");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }



        List<String> list;

        list = new ArrayList<String>();

        JSONObject jsonService = null;

        Service serv = null;

        for (int i = 0; i < jsonServices.length(); i++)
        {
            try
            {
                jsonService = jsonServices.getJSONObject(i);

                serv = new Service();

                serv.idService = jsonService.getInt("Id");
                serv.elaraReference = jsonService.getString("ReferenciaElara");
                serv.estimatedTimeA = jsonService.getString("ETA");
                serv.ticket = jsonService.getInt("Ticket");
                serv.type = jsonService.getString("Servicio");

                servicesList.add(serv);

                list.add("TT " + serv.ticket + " - " + serv.elaraReference);

            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

        }

        servicesSpinner
                .setAdapter(new ArrayAdapter<String>(ToDoServices.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        list));

        servicesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                tbReference.setText(servicesList.get(position).elaraReference);
                tbTicket.setText(String.valueOf(servicesList.get(position).ticket));
                tbETA.setText(servicesList.get(position).estimatedTimeA);
                tbType.setText(servicesList.get(position).type);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });
    }

    public class WSSoap extends AsyncTask<Void, Void, String>
    {
        String webServiceResult = "";
        String result = "";
        String namespace = "http://tempuri.org/";
        String url = "http://201.131.60.39:8092/AndroidWebService/SeguimientoCuadrillasWS.asmx";
        String methodName = "getActiveServices";

        String SOAP_ACTION;
        SoapObject request = null, objMessages = null;
        SoapSerializationEnvelope envelope;
        HttpTransportSE  androidHttpTransport;

        WSSoap(String webServiceResult) {
            this.webServiceResult = webServiceResult;
        }

        @Override
        protected String doInBackground(Void... arg0)
        {
            try {

                SOAP_ACTION = namespace + methodName;
                request = new SoapObject(namespace, methodName);

                request.addProperty("techId", idTechnician);

                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

                envelope.dotNet = true;

                envelope.setOutputSoapObject(request);
                androidHttpTransport = new HttpTransportSE(url);
                androidHttpTransport.debug = true;

                androidHttpTransport.call(SOAP_ACTION, envelope);

                result = envelope.getResponse().toString();

            }
            catch (Exception e)
            {
                result = e.getMessage();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String res) {
            webServiceResult = result;
            super.onPostExecute(res);
        }



    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //preventing default implementation previous to android.os.Build.VERSION_CODES.ECLAIR
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
