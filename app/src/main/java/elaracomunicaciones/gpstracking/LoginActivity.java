package elaracomunicaciones.gpstracking;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.test.suitebuilder.annotation.SmallTest;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
                // Create MD5 Hash
                MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
                digest.update(mPassword.getBytes());
                byte messageDigest[] = digest.digest();

                // Create Hex String
                StringBuffer hexString = new StringBuffer();
                for (int i=0; i<messageDigest.length; i++)
                    hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
                hexString.toString();

                Connection con = DBConnection.getInstance().getConnection();
                if (con == null) {
                    z = "Error en la Conexión con SQL server";
                } else {
                    String query = "SELECT IdTechnician from Elara_S_Users WHERE UserName ='" + muser + "' AND Password='" + mPassword + "'" + "AND Status= 1";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);


                    if (rs.next()) {
                        IdTechnician = rs.getInt("IdTechnician");
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
}

