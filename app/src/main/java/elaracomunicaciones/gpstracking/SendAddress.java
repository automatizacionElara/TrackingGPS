package elaracomunicaciones.gpstracking;

import android.os.AsyncTask;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Daniel Sosa on 07/11/2016.
 */

public class SendAddress extends AsyncTask<Void, Void, Boolean> {

    private final int IdServ;
    private final String Calle;
    private final String NoExterior;
    private final String NoInterior;
    private final String Colonia;
    private final int CP;
    private boolean IsSuccess;
    String msg = "";

    SendAddress(int IdServicio, String Cal, String NoExt, String NoInt, String Col, int CPostal) {
        IdServ = IdServicio;
        Calle = Cal;
        NoExterior = NoExt;
        NoInterior = NoInt;
        Colonia = Col;
        CP = CPostal;
    }


    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            String Elara_ES_Address = "";
            String msg = "";
            Connection con = DBConnection.getInstance().getConnection();
            if (con == null) {
                msg = "Error en la Conexión con SQL server";
            } else {
                Elara_ES_Address = "INSERT INTO Elara_ES_Address (IdServicio, Calle, NoExterior, NoInterior, Colonia, CP) VALUES("+ IdServ +", '"+ Calle + "', '" + NoExterior + "', '" + NoInterior + "', '" + Colonia + "'," + CP + ");";

                Statement stmt = null;

                try {
                    stmt = con.createStatement();
                    stmt.executeQuery(Elara_ES_Address);
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
