package elaracomunicaciones.gpstracking;

import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.EmptyStackException;

/**
 * Created by daniel sosa on 29/08/2016.
 */

    public class SendUbication extends AsyncTask<Void, Void, Boolean> {

        private final double latitude;
        private final double longitude;
        private final int IdService;
        private final String Fecha;
        private boolean IsSuccess;
        String msg = "";

        SendUbication(int IdServ, double Long, double Lat) {
            latitude = Lat;
            longitude = Long;
            IdService = IdServ;
            Fecha = "";
        }

    SendUbication(int IdServ, double Long, double Lat, String fecha)
    {
         latitude = Lat;
         longitude = Long;
        IdService = IdServ;
         Fecha = fecha;
    }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String Elara_ES_TrackingDetails = "";
                String msg = "";
                Connection con = DBConnection.getInstance().getConnection();
                if (con == null) {
                    msg = "Error en la Conexión con SQL server";
                } else {
                    if(Fecha == ""){
                        Elara_ES_TrackingDetails = "INSERT INTO Elara_ES_TrackingDetails VALUES(" + IdService + ", GETDATE()," +  latitude + "," + longitude + ");";
                    }else{
                        Elara_ES_TrackingDetails = "INSERT INTO Elara_ES_TrackingDetails VALUES(" + IdService + ","+ Fecha + "," +  latitude + "," + longitude + ");";
                    }

                    Statement stmt = null;

                    try {
                        stmt = con.createStatement();
                        stmt.executeQuery(Elara_ES_TrackingDetails);
                        stmt.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception ex) {
                IsSuccess = false;
                msg = "Exceptions";
            }

            return null;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            super.onPostExecute(success);

        }

        @Override
        protected void onCancelled() {

        }

    }

