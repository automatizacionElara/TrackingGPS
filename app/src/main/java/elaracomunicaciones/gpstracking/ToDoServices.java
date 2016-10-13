package elaracomunicaciones.gpstracking;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

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
    private TextView tbReference, tbTicket, tbETA, tbType, tbRequired;
    private Spinner servicesSpinner;
    private Button buttonInit;
    CheckConnection cc;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todoservices);

        cc = new CheckConnection();

        tbReference = (TextView)findViewById(R.id.tbReference);
        tbTicket = (TextView)findViewById(R.id.tbTicket);
        tbETA = (TextView)findViewById(R.id.tbETA);
        tbType = (TextView)findViewById(R.id.tbType);
        servicesSpinner = (Spinner)findViewById(R.id.servicesSpinner);
        tbRequired = (TextView)findViewById(R.id.tbRequired);

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

                Intent LoginActivity = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(LoginActivity);
                finish();
            }
        });


        buttonInit = (Button) findViewById(R.id.checkin);
        buttonInit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(getApplicationContext(), "Servicio Iniciado", Toast.LENGTH_SHORT).show();
                Intent Checkin = new Intent(getApplicationContext(), StartService.class);

                Service service = servicesList.get(servicesSpinner.getSelectedItemPosition());

                Checkin.putExtra("IdTecnico",idTechnician);
                Checkin.putExtra("IdServicio",service.idService);
                Checkin.putExtra("Status", 0);

                startActivity(Checkin);
            }
        });

        final Button buttonRefresh = (Button)findViewById(R.id.buttonRefresh);

        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                buttonRefresh.setEnabled(false);

                if (!cc.isOnlineNet())
                {
                    Toast.makeText(getBaseContext(),
                            "No se pudieron cargar los servicios. Comprueba tu conexión a Internet.", Toast.LENGTH_LONG)
                            .show();

                    buttonInit.setEnabled(false);
                }
                else {
                    loadServices();
                }
                buttonRefresh.setEnabled(true);
            }
        });

        if (!cc.isOnlineNet())
        {
            Toast.makeText(getBaseContext(),
                    "No se pudieron cargar los servicios. Comprueba tu conexión a Internet.", Toast.LENGTH_LONG)
                    .show();

            buttonInit.setEnabled(false);
        }
        else
        {
            loadServices();
        }
    }

    private void loadServices()
    {
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
        };

        JSONArray jsonServices = null;

        try
        {
            jsonServices = new JSONArray(webServiceResult);
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

                serv.idService = jsonService.getInt("idService");
                serv.elaraReference = jsonService.getString("referenciaElara");
                serv.estimatedTimeA = jsonService.getString("ETA");
                serv.ticket = jsonService.getInt("ticket");
                serv.type = jsonService.getString("type");

                JSONArray required = new JSONArray(jsonService.getString("required"));

                if (required != null && required.length() > 0)
                {
                    serv.required = new ArrayList<>();

                    for (int j=0;j<required.length();j++){
                        serv.required.add(required.get(j).toString());
                    }
                }

                servicesList.add(serv);

                list.add("TT " + serv.ticket + " - " + serv.elaraReference);

            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

        }

        if(list.size() > 0)
        {
            buttonInit.setEnabled(true);
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Feliciades, no tienes ningún servicio pendiente.", Toast.LENGTH_SHORT).show();
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
                Service s = servicesList.get(position);

                tbRequired.setText("No hay registros");

                if(s.required != null)
                {
                    tbRequired.setText("");
                    for (int i = 0; i < servicesList.get(position).required.size(); i++) {
                        tbRequired.append("- " + servicesList.get(position).required.get(i) + "\n");
                    }
                }
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


