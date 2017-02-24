package elaracomunicaciones.gpstracking.Utils;

import android.os.AsyncTask;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class GetExistentPhotos extends AsyncTask<Void, Void, String[]>
{
    private static final String EMPTY_STRING = "";
    private final int idService;
    private String[] existentPhotos;

    String msg = EMPTY_STRING;

    public GetExistentPhotos(int idService)
    {
        this.idService = idService;
        existentPhotos = new String[30];
    }

    @Override
    protected String[] doInBackground(Void... params)
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
                String query = "SELECT PhotoDescription" +
                        "  FROM PhotoCatalog PC" +
                        "  JOIN FieldServicePhoto FSP ON PC.IdPhotoCatalog = FSP.IdPhotoCatalog" +
                        "  WHERE IdService = " + idService;

                Statement stmt = null;

                try {

                    stmt = con.createStatement();
                    stmt.executeQuery(query);

                    ResultSet rs = stmt.getResultSet();

                    int i = 0;

                    while (rs.next()) {
                        existentPhotos[i] = rs.getString("PhotoDescription");
                        i++;
                    }

                    stmt.close();

                    return existentPhotos;
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
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
    protected void onPostExecute(final String [] success) {
        super.onPostExecute(success);

    }

    @Override
    protected void onCancelled() {

    }

}


