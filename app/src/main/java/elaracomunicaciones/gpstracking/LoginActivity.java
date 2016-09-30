package elaracomunicaciones.gpstracking;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.test.suitebuilder.annotation.SmallTest;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * A login screen that offers login via user/password.
 */
public class LoginActivity extends AppCompatActivity  {

    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView muserView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        VerifiedConnection vc = new VerifiedConnection();
        boolean connect = vc.verificaConexion(getApplicationContext());
        if (!vc.verificaConexion(this)) {
            Toast.makeText(getBaseContext(),
                    "Comprueba tu conexión a Internet y vuelve a inicar la Aplicación ", Toast.LENGTH_SHORT)
                    .show();
            this.finish();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //display in long period of time
        // Set up the login form.
        muserView = (AutoCompleteTextView) findViewById(R.id.user);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();

                    return true;
                }
                return false;
            }
        });

        Button muserSignInButton = (Button) findViewById(R.id.user_sign_in_button);
        muserSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        muserView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String user = muserView.getText().toString();
        String password = mPasswordView.getText().toString();
        boolean isSuccess = false;
        boolean cancel = false;
        View focusView = null;

        // Revisa que el Usuario y Contraseña no sea Nulos.
        if (TextUtils.isEmpty(user) || TextUtils.isEmpty(password)) {
            muserView.setError(getString(R.string.error_field_required));
            focusView = muserView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(user, password, isSuccess);
            mAuthTask.execute();

            boolean valor = isSuccess;
        }
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String muser;
        private final String mPassword;
        private boolean mIsSuccess;
        private boolean IsSuccess;
        public int IdTechnician;
        String z = "";

        UserLoginTask(String user, String password, boolean isSuccess) {
            muser = user;
            mPassword = password;
            mIsSuccess = isSuccess;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

                try {
                    Connection con = DBConnection.getInstance().getConnection();
                    if (con == null) {
                        z = "Error en la Conexión con SQL server";
                    } else {
                        String desEncrypt = "SELECT dbo.DecryptingPassword( (SELECT [Password] FROM Elara_S_Users Where UserName = '"+ muser +"')) AS Contrasena, IdTechnician FROM dbo.Elara_S_Users WHERE UserName='" + muser +"';;";
                        Statement stmt2 = con.createStatement();
                        ResultSet rs2 = stmt2.executeQuery(desEncrypt);
                        String Pass = "";

                        if(rs2.next()) {
                            Pass = rs2.getString(1);
                            IdTechnician = rs2.getInt(2);
                        }


                        if (mPassword.contentEquals(Pass)) {

                            z = "Login Correcto";
                            IsSuccess = true;
                        } else {
                            z = "Credenciales Invalidas";
                            IsSuccess = false;
                            Intent ListServices = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(ListServices);
                        }
                    }
                } catch (Exception ex) {
                    IsSuccess = false;
                    z = "Exceptions";
                }

        return null;
    }

        @Override
        protected void onPostExecute(final Boolean success) {
            mIsSuccess = IsSuccess;
            super.onPostExecute(success);
            showProgress(false);

            if(IsSuccess)
            {
                try
                {
                    OutputStreamWriter fout=
                            new OutputStreamWriter(
                                    openFileOutput("access.txt", Context.MODE_PRIVATE));

                    fout.write(String.valueOf(IdTechnician));
                    fout.close();
                }
                catch (Exception ex)
                {
                    Log.e("Ficheros", "Error al escribir fichero a memoria interna");
                }

                Intent ListServices = new Intent(getApplicationContext(), ToDoServices.class);
                ListServices.putExtra("IdTecnico", IdTechnician);
                startActivity(ListServices);
            }else{
                Toast.makeText(getApplicationContext(), "Usuario y Contraseña Invalidos", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
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

