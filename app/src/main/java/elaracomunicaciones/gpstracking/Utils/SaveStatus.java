package elaracomunicaciones.gpstracking.Utils;

import android.os.AsyncTask;
import android.renderscript.ScriptIntrinsicYuvToRGB;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SaveStatus extends AsyncTask<Void, Void, Boolean>
{
    private static final String EMPTY_STRING = "";
    private final int idStatus;
    private final int idService;
    private final String latitude;
    private final String longitude;

    String msg = EMPTY_STRING;

    public SaveStatus(int idService, int idStatus, String latitude, String longitude) {
        this.idStatus = idStatus;
        this.idService = idService;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    protected Boolean doInBackground(Void... params)
    {
        try
        {
            String msg = EMPTY_STRING;
            boolean exists = false;

            Connection con = DBConnection.getInstance().getConnection();

            if (con == null)
            {
                msg = "Error en la Conexión con SQL server";
            }
            else
            {

                String query = String.format("UPDATE FieldService SET IdStatus = %1d WHERE IdService = %2d", idStatus, idService);
                Statement stmt = null;

                try {
                    stmt = con.createStatement();

                    stmt.execute(query);

                    DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:00");
                    String date = df.format(Calendar.getInstance().getTime());

                    query = String.format("INSERT INTO ServiceWorkflow (IdService, IdStatus, Date, Latitude, Longitude) VALUES (%1d,%2d,'%3s',%4s,%5s)",
                            idService, idStatus, date, latitude, longitude);

                    stmt.execute(query);

                    stmt.close();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception ex) {
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


