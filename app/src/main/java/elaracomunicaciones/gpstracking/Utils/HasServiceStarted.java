package elaracomunicaciones.gpstracking.Utils;

import android.os.AsyncTask;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class HasServiceStarted extends AsyncTask<Void, Void, Boolean>
{
    private static final String EMPTY_STRING = "";
    private final int idService;
    String msg = EMPTY_STRING;

    public HasServiceStarted(int idService)
    {
        this.idService = idService;
    }

    @Override
    protected Boolean doInBackground(Void... params)
    {
        try
        {

            boolean exists = false;

            Connection con = new DBConnection().getInstance().getConnection();

            if (con == null)
            {
                msg = "Error en la Conexión con SQL server";
            }
            else
            {
                String query = "SELECT COUNT(*)" +
                        " FROM FieldService" +
                        " WHERE IdService = " + idService;

                Statement stmt = null;

                try {

                    stmt = con.createStatement();
                    stmt.executeQuery(query);

                    ResultSet rs = stmt.getResultSet();

                    int i = 0;

                    while (rs.next())
                    {
                        exists = rs.getInt(1) > 0;
                    }

                    stmt.close();

                    return exists;
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
    protected void onPostExecute(final Boolean success) {
        super.onPostExecute(success);

    }

    @Override
    protected void onCancelled() {

    }

}


