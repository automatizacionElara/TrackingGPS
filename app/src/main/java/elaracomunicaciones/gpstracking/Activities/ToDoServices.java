package elaracomunicaciones.gpstracking.Activities;

/* Librerías */

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.OutputStreamWriter;
import java.util.List;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/* Librerías para webservice .net*/

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import elaracomunicaciones.gpstracking.Models.Service;
import elaracomunicaciones.gpstracking.R;
import elaracomunicaciones.gpstracking.Services.SendingService;
import elaracomunicaciones.gpstracking.Utils.CheckConnection;
import elaracomunicaciones.gpstracking.Utils.SaveService;

public class ToDoServices extends AppCompatActivity
{
    /* Declaración de variables globales */

    private List<Service> servicesList = new ArrayList<>();
    private int idTechnician = 0;
    private TextView lbReference, lbTicket, lbETA, lbType, lbRequired, lbAddress;
    private TextView tbReference, tbTicket, tbETA, tbType, tbRequired, tbAddress;
    private Spinner servicesSpinner;
    private Button btnInit, btnRefresh,  btnLogOut;
    private CheckConnection webConnection;
    private ProgressBar progressBar;
    private ScrollView scrollInfo;

    /* Inicialización de la actividad */

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        /* Se liga la activity con su vista */

        setContentView(R.layout.activity_todoservices);

        /* Lectura de parámetros recibidos de la actividad de Login */

        Intent inte = getIntent();
        idTechnician = inte.getIntExtra("idTechnician",0);

        /* Creación de instancia de conexión a internet */

        webConnection = new CheckConnection();

        /* Inicialización de variables y elementos de la vista */

        servicesSpinner = (Spinner)findViewById(R.id.servicesSpinner);

        scrollInfo = (ScrollView)findViewById(R.id.scrollInfo);
        lbReference = (TextView)findViewById(R.id.lbReference);
        lbTicket = (TextView)findViewById(R.id.lbTicket);
        lbETA = (TextView)findViewById(R.id.lbETA);
        lbType = (TextView)findViewById(R.id.lbType);
        lbRequired = (TextView)findViewById(R.id.lbRequired);
        lbAddress = (TextView)findViewById(R.id.lbAddress);

        tbReference = (TextView)findViewById(R.id.tbReference);
        tbTicket = (TextView)findViewById(R.id.tbTicket);
        tbETA = (TextView)findViewById(R.id.tbETA);
        tbType = (TextView)findViewById(R.id.tbType);
        tbRequired = (TextView)findViewById(R.id.tbRequired);
        tbAddress = (TextView)findViewById(R.id.tbAddress);

        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        /* Acción del botón para cierre de sesión */

        btnLogOut = (Button) findViewById(R.id.btnLogOut);

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File dir = getFilesDir();
                File file = new File(dir,"access.txt");
                boolean deleted = file.delete();
                Toast.makeText(getApplicationContext(), "Sesión Cerrada", Toast.LENGTH_SHORT).show();

                Intent LoginActivity = new Intent(getApplicationContext(), elaracomunicaciones.gpstracking.Activities.LoginActivity.class);
                startActivity(LoginActivity);
                finish();
            }
        });

        /* Acción del botón para iniciar servicio */

        final Context con = this;

        btnInit = (Button) findViewById(R.id.btnInit);
        btnInit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Service service = servicesList.get(servicesSpinner.getSelectedItemPosition());

                String msg = "¿Estás listo para iniciar el servicio " + service.elaraReference + " [" + service.ticket + "]?";

                new AlertDialog.Builder(con)
                        .setMessage(msg)
                        .setCancelable(false)
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                Service service = servicesList.get(servicesSpinner.getSelectedItemPosition());

                                if(saveService())
                                {
                                    Toast.makeText(getApplicationContext(), "Buen viaje!", Toast.LENGTH_SHORT).show();

                                    Intent onMyWay = new Intent(getApplicationContext(), StartService.class);

                                    onMyWay.putExtra("idVsatService",service.idVsatService);
                                    onMyWay.putExtra("idTechnician",idTechnician);
                                    onMyWay.putExtra("idService",service.idService);
                                    onMyWay.putExtra("elaraReference",service.elaraReference);
                                    onMyWay.putExtra("idType",service.idType);
                                    onMyWay.putExtra("status", 1);
                                    onMyWay.putExtra("editAddress",false);

                                    startActivity(onMyWay);
                                }
                                else
                                {
                                    Toast.makeText(getBaseContext(),
                                            "No se pudo iniciar el servicio. Comprueba tu conexión a Internet.", Toast.LENGTH_LONG)
                                            .show();
                                }
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        /* Acción del botón para actualizar la lista de servicios disponibles */

        btnRefresh = (Button)findViewById(R.id.btnRefresh);

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                btnRefresh.setEnabled(false);
                btnInit.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                scrollInfo.setVisibility(View.GONE);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadServices();
                    }
                }, 1000);
            }
        });

        btnRefresh.setEnabled(false);
        btnInit.setEnabled(false);

        showLabels(false);
    }

    @Override
    protected void onStart() {
        super.onStart();

        /* Obtención de servicios disponibles para el proveedor */
        progressBar.setVisibility(View.VISIBLE);
        scrollInfo.setVisibility(View.GONE);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadServices();
            }
        }, 1000);

        Intent intent = new Intent(getApplicationContext(), SendingService.class);
        stopService(intent);
    }

    private void enableActions(boolean enable)
    {
        btnInit.setEnabled(enable);
    }

    private void showLabels(boolean enable)
    {
        if(enable)
        {
            lbReference.setVisibility(View.VISIBLE);
            lbTicket.setVisibility(View.VISIBLE);
            lbETA.setVisibility(View.VISIBLE);
            lbType.setVisibility(View.VISIBLE);
            lbRequired.setVisibility(View.VISIBLE);
            lbAddress.setVisibility(View.VISIBLE);
        }
        else
        {
            lbReference.setVisibility(View.GONE);
            lbTicket.setVisibility(View.GONE);
            lbETA.setVisibility(View.GONE);
            lbType.setVisibility(View.GONE);
            lbRequired.setVisibility(View.GONE);
            lbAddress.setVisibility(View.GONE);
        }
    }

    private void loadServices()
    {
        if (!webConnection.isOnlineNet())
        {
            Toast.makeText(getBaseContext(),
                    "No se pudieron cargar los servicios. Comprueba tu conexión a Internet.", Toast.LENGTH_LONG)
                    .show();

            enableActions(false);
            btnRefresh.setEnabled(true);
            progressBar.setVisibility(View.GONE);
            return;
        }

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

                serv.idVsatService = jsonService.getInt("idSitio");
                serv.idService = jsonService.getInt("idService");
                serv.elaraReference = jsonService.getString("referenciaElara");
                serv.estimatedTimeA = jsonService.getString("ETA");
                serv.ticket = jsonService.getInt("ticket");
                serv.type = jsonService.getString("type");
                serv.address = jsonService.getString("address");
                serv.city = jsonService.getString("city");
                serv.district = jsonService.getString("district");
                serv.idType = jsonService.getInt("idType");

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
            enableActions(true);
            showLabels(true);
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Felicidades, no tienes ningún servicio pendiente.", Toast.LENGTH_SHORT).show();
        }

        servicesSpinner
                .setAdapter(new ArrayAdapter<String>(ToDoServices.this,
                        elaracomunicaciones.gpstracking.R.layout.spinner_layout,
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
                tbAddress.setText(servicesList.get(position).address + ", " + servicesList.get(position).city + ", " + servicesList.get(position).district);
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

        progressBar.setVisibility(View.GONE);
        scrollInfo.setVisibility(View.VISIBLE);
        btnRefresh.setEnabled(true);
    }

    public class WSSoap extends AsyncTask<Void, Void, String>
    {
        String webServiceResult = "";
        String result = "";
        String namespace = "http://tempuri.org/";
        String url = "http://201.131.60.39:8092/AndroidWebService/SeguimientoCuadrillasWS.asmx";
        //String url = "http://172.31.248.4/AndroidWebService/SeguimientoCuadrillasWS.asmx";
        String methodName = "getMyServices";

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


    private boolean saveService()
    {
        /* Escritura en la base de datos */

        Service service = servicesList.get(servicesSpinner.getSelectedItemPosition());

        boolean isOnline = webConnection.isOnlineNet();

        if(!isOnline)
        {
            return false;
        }

        SaveService su = new SaveService(idTechnician, service.idVsatService, service.elaraReference, service.idService, service.ticket);

        try
        {
            su.execute().get();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
            return false;
        } catch (ExecutionException e)
        {
            e.printStackTrace();
            return false;
        }

        /* Escritura en el archivo del equipo */

        try {
            OutputStreamWriter fout =
                    new OutputStreamWriter(
                            openFileOutput("activeService.txt", Context.MODE_PRIVATE));

            fout.write(String.valueOf(service.idService));
            fout.write("\n1");
            fout.close();
        } catch (Exception ex)
        {
            Log.e("Ficheros", "Error al escribir fichero a memoria interna");
            return false;
        }

        /* Se guarda la referencia Elara y el tipo de servicio */

        try
        {
            OutputStreamWriter site=
                    new OutputStreamWriter(
                            openFileOutput("sitio.txt", Context.MODE_PRIVATE));

            site.write(String.valueOf(service.elaraReference));
            site.write("\n" + String.valueOf(service.idType));
            site.close();
        }
        catch (Exception ex)
        {
            Log.e("Ficheros", "Error al escribir fichero a memoria interna");
        }

        return true;
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


