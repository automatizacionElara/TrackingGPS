package elaracomunicaciones.gpstracking.Utils;

import android.os.AsyncTask;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import elaracomunicaciones.gpstracking.Utils.DBConnection;


public class SendPhoto extends AsyncTask<Void, Void, Boolean> {

        private final String stringPhoto;
        private final int idService;
        private final int idType;
        private final String date;
        String msg = "";

        public SendPhoto(int idService, String photo, int idType, String date) {
            this.idService = idService;
            stringPhoto = photo;
            this.idType = idType;
            this.date = date;
        }

    @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String query = "";
                String msg = "";
                Connection con = new DBConnection().getInstance().getConnection();
                if (con == null) {
                    msg = "Error en la Conexión con SQL server";
                } else
                {
                    query = String.format("SELECT COUNT(*) AS count FROM FieldServicePhoto WHERE IdPhotoCatalog = %1d AND IdService =  %2d",
                            idType, idService);

                    Statement stmt = null;

                    stmt = con.createStatement();
                    stmt.executeQuery(query);

                    ResultSet rs = stmt.getResultSet();

                    boolean exists  = false;

                    while (rs.next()) {
                        exists = rs.getInt("count") > 0;
                    }

                    if(!exists)
                    {
                        query = String.format("INSERT INTO FieldServicePhoto (IdService, IdPhotoCatalog, PhotoDate, ImageFile)"  +
                                " VALUES(%1d, %2d, '%3s', '" + stringPhoto + "')", idService, idType, date);

                    }
                    else
                    {
                        query = String.format("UPDATE FieldServicePhoto SET ImageFile = '"+ stringPhoto +"', PhotoDate = '%1s'" +
                                " WHERE IdPhotoCatalog = %2d AND IdService = %3d", date, idType, idService);
                    }

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
            } catch (Exception ex)
            {
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

