package elaracomunicaciones.gpstracking.Utils;

import android.os.AsyncTask;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import elaracomunicaciones.gpstracking.Utils.DBConnection;

/**
 * Created by daniel sosa on 05/09/2016.
 */
public class SaveService extends AsyncTask<Void, Void, Boolean>
{
    private static final String EMPTY_STRING = "";
    private final int idTechnician;
    private final int idService;
    private final int idVsatService;
    private final String elaraReference;
    private final int ticket;

    String msg = EMPTY_STRING;

    public SaveService(int idTechnician, int idVsatService, String elaraReference, int idService, int ticket) {
        this.idTechnician = idTechnician;
        this.idService = idService;
        this.elaraReference = elaraReference;
        this.idVsatService = idVsatService;
        this.ticket = ticket;
    }

    @Override
    protected Boolean doInBackground(Void... params)
    {
        try
        {
            String msg = EMPTY_STRING;
            boolean exists = false;

            Connection con = new DBConnection().getInstance().getConnection();

            if (con == null)
            {
                msg = "Error en la Conexión con SQL server";
            }
            else
            {
                String query = "SELECT COUNT(*) AS count FROM VsatService WHERE IdVsatService = " + idVsatService;
                //String Elara_ES_TrackingUpdate = "UPDATE dbo.Elara_ES_Tracking SET Idstatus = "+ status + " WHERE idService = " + idService + ";";
                Statement stmt = null;

                try {

                    stmt = con.createStatement();
                    stmt.executeQuery(query);

                    ResultSet rs = stmt.getResultSet();
                    while (rs.next()) {
                        exists = rs.getInt("count") > 0;
                    }

                    if(!exists)
                    {
                        query = String.format("INSERT INTO VsatService (IdVsatService, ElaraReference, Latitude, Longitude) VALUES (%1d,'%2s',0.0,0.0)",
                                idVsatService, elaraReference);

                        stmt.execute(query);
                    }

                    query = String.format("INSERT INTO FieldService (IdService, IdVsatService, IdTechnician, Ticket, IdStatus) VALUES (%1d,%2d,%3d,%4d,1)",
                            idService, idVsatService, idTechnician, ticket);

                    stmt.execute(query);

                    DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:00");
                    String date = df.format(Calendar.getInstance().getTime());

                    query = String.format("INSERT INTO ServiceWorkflow (IdService, IdStatus, Date) VALUES (%1d,%2d,'%3s')",
                            idService, 1, date);

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


