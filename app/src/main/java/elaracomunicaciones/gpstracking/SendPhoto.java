package elaracomunicaciones.gpstracking;

import android.os.AsyncTask;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


    public class SendPhoto extends AsyncTask<Void, Void, Boolean> {

        private final String stringPhoto;
        private final int IdService;
        private final int IdType;
        private boolean IsSuccess;
        String msg = "";

        SendPhoto(int IdServ, String photo, int idtype) {
            IdService = IdServ;
            stringPhoto = photo;
            IdType = idtype;
        }

    /*SendPhoto(int IdServ, double Long, double Lat, String fecha)
    {
         latitude = Lat;
         longitude = Long;
        IdService = IdServ;
         Fecha = fecha;
    }*/



    @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String Elara_ES_TrackingDetails = "";
                String msg = "";
                Connection con = DBConnection.getInstance().getConnection();
                if (con == null) {
                    msg = "Error en la Conexión con SQL server";
                } else
                {

                        Elara_ES_TrackingDetails = "INSERT INTO Elara_ES_ServiceImages VALUES(" + IdService + ",'" + stringPhoto + "," + IdType + ");";

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

