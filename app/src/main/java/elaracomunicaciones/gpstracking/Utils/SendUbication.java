package elaracomunicaciones.gpstracking.Utils;

import android.os.AsyncTask;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import elaracomunicaciones.gpstracking.Utils.DBConnection;

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

        public SendUbication(int IdServ, double Long, double Lat) {
            latitude = Lat;
            longitude = Long;
            IdService = IdServ;

            DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Fecha = df.format(Calendar.getInstance().getTime());
        }

    public SendUbication(int IdServ, double Long, double Lat, String fecha)
    {
         latitude = Lat;
         longitude = Long;
         IdService = IdServ;
         Fecha = fecha;
    }


    @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String query = "";
                String msg = "";
                Connection con = new DBConnection().getInstance().getConnection();

                if (con == null)
                {
                    msg = "Error en la Conexión con SQL server";
                }
                else
                {
                    query = String.format("INSERT INTO FieldServiceTracking (IdService, DateTracking, Latitude, Longitude) VALUES (%1d,'%2s',%3f,%4f)",
                            IdService, Fecha, latitude, longitude);
                    Statement stmt = null;

                    try {
                        stmt = con.createStatement();
                        stmt.execute(query);
                        stmt.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    finally {
                        con.close();
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

