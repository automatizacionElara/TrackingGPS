package elaracomunicaciones.gpstracking.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.AsyncTask;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import elaracomunicaciones.gpstracking.Utils.CheckConnection;
import elaracomunicaciones.gpstracking.Utils.DBConnection;
import elaracomunicaciones.gpstracking.R;

public class LoginActivity extends AppCompatActivity
{
    private static final String EMPTY_STRING = "";

    private UserLoginTask authTask = null;

    /* Declaración de controles de la vista */

    private AutoCompleteTextView tbUser;
    private EditText etPassword;
    private View progressBar;
    private Button btnLogin;

    /* Constructor */

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        /* Se liga la activity con su vista */

        setContentView(R.layout.activity_login);

        /* Inicialización de controles de la vista */

        tbUser = (AutoCompleteTextView) findViewById(R.id.user);
        etPassword = (EditText) findViewById(R.id.password);

        btnLogin = (Button) findViewById(R.id.user_sign_in_button);

        btnLogin.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                progressBar.setVisibility(View.VISIBLE);
                btnLogin.setEnabled(false);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        attemptLogin();
                    }
                }, 1000);
            }
        });

        progressBar = findViewById(R.id.progressBar);
    }

    protected  void onStart()
    {
        super.onStart();

        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "La aplicación requiere permisos de ubicación para poder iniciar.", Toast.LENGTH_LONG).show();
            finish();
        }

        if(!isLocationEnabled(getApplicationContext()))
        {
            Toast.makeText(getApplicationContext(), "Por favor activa tu ubicación para iniciar la aplicación.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    private void attemptLogin()
    {
        if (authTask != null) {
            return;
        }

        /* Revisa la conexión y acceso a internet. */

        CheckConnection cc = new CheckConnection();

        if (!cc.isOnlineNet())
        {
            Toast.makeText(getBaseContext(),
                    "No se pudo inciar sesión. Comprueba tu conexión a Internet.", Toast.LENGTH_SHORT)
                    .show();

            progressBar.setVisibility(View.GONE);
            btnLogin.setEnabled(true);

            return;
        }

        /* Reset de los errores de los campos */

        tbUser.setError(null);
        etPassword.setError(null);

        /* Se obtienen los valores ingresados por le usuario */

        String user = tbUser.getText().toString();
        String password = etPassword.getText().toString();
        boolean cancel = false;
        View focusView = null;

        /* Revisa que los campos no sean nulos. */

        if (TextUtils.isEmpty(user))
        {
            progressBar.setVisibility(View.GONE);
            btnLogin.setEnabled(true);


            tbUser.setError(getString(R.string.error_field_required));
            focusView = tbUser;
            return;
        }

        if (TextUtils.isEmpty(password))
        {
            progressBar.setVisibility(View.GONE);
            btnLogin.setEnabled(true);

            etPassword.setError(getString(R.string.error_field_required));
            focusView = etPassword;
            return;
        }

        authTask = new UserLoginTask(user, password);
        authTask.execute();
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean>
    {
        private final String muser;
        private final String mPassword;
        private boolean isSuccess;
        public int idTechnician;
        String msg = EMPTY_STRING;

        UserLoginTask(String user, String password)
        {
            muser = user;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params)
        {
            Connection con = null;

            try
            {
                 con = DBConnection.getInstance().getConnection();

                 String query = "SELECT dbo.DecryptingPassword( (SELECT [Password] FROM Users Where UserName = '"
                         + muser +"')) AS Password, IdTechnician FROM dbo.Users WHERE UserName='" + muser +"';;";
                 Statement stmt = con.createStatement();
                 ResultSet result = stmt.executeQuery(query);
                 String Pass = EMPTY_STRING;

                 if(result.next())
                 {
                     Pass = result.getString(1);
                     idTechnician = result.getInt(2);
                 }

                 if (mPassword.contentEquals(Pass))
                 {
                     isSuccess = true;
                 }
                 else
                 {
                     isSuccess = false;
                     msg = "El usuario y/o contraseña son incorrectos.";
                 }
            } catch (Exception ex)
            {
                isSuccess = false;
                msg = ex.getMessage();
            }

        return null;
    }

        @Override
        protected void onPostExecute(final Boolean success)
        {
            super.onPostExecute(success);

            if(isSuccess)
            {
                try
                {
                    OutputStreamWriter fout=
                            new OutputStreamWriter(
                                    openFileOutput("access.txt", Context.MODE_PRIVATE));

                    fout.write(String.valueOf(idTechnician));
                    fout.close();
                }
                catch (Exception ex)
                {
                    Log.e("Ficheros", "Error al escribir fichero a memoria interna");
                }

                Intent ListServices = new Intent(getApplicationContext(), ToDoServices.class);
                ListServices.putExtra("idTechnician", idTechnician);
                finish();
                startActivity(ListServices);

            }
            else
            {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);
                authTask = null;
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            authTask = null;
        }

    }

    /* Bloqueo del boton regresar */

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}

