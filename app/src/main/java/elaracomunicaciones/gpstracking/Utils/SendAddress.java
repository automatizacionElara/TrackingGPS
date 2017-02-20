package elaracomunicaciones.gpstracking.Utils;

import android.os.AsyncTask;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import elaracomunicaciones.gpstracking.Utils.DBConnection;

/**
 * Created by Daniel Sosa on 07/11/2016.
 */

public class SendAddress extends AsyncTask<Void, Void, Boolean> {

    private final String elaraReference;
    private final String Calle;
    private final String NoExterior;
    private final String NoInterior;
    private final String Colonia;
    private final int CP;
    private final int IdTecnico;
    private boolean IsSuccess;
    String msg = "";

    public SendAddress(String elaraReference, String Cal, String NoExt, String NoInt, String Col, int CPostal, int IdTec) {
        this.elaraReference = elaraReference;
        Calle = Cal;
        NoExterior = NoExt;
        NoInterior = NoInt;
        Colonia = Col;
        CP = CPostal;
        IdTecnico = IdTec;
    }


    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            String msg = "";
            Connection con = DBConnection.getInstance().getConnection();
            if (con == null)
            {
                msg = "Error en la Conexión con SQL server";
            }
            else
            {
                int idVsatService = 0;

                String query = String.format("SELECT idVsatService FROM VsatService WHERE ElaraReference = '%1s'", elaraReference);

                Statement stmt = null;

                try {

                    stmt = con.createStatement();
                    stmt.executeQuery(query);

                    ResultSet rs = stmt.getResultSet();
                    while (rs.next()) {
                        idVsatService = rs.getInt("idVsatService");
                    }

                    query = String.format("SELECT COUNT(*) AS 'exists' FROM VsatAddress WHERE idVsatService = '%1d'", idVsatService);

                    stmt.executeQuery(query);

                    rs = stmt.getResultSet();

                    boolean exists = false;

                    while (rs.next())
                    {
                        exists = rs.getInt("exists") > 0;
                    }

                    if(exists)
                    {
                        return false;
                    }

                    query = String.format("INSERT INTO VsatAddress (IdVsatService, Street, StreetNumber,"
                            + "ApartmentNumber, Neighborhood, PostalCode, Approved, Rejected, IdTechnician)"
                            + "VALUES(%1d, '%2s', '%3s', '%4s', '%5s', %6d, 0, 0, %7d)", idVsatService, Calle, NoExterior, NoInterior, Colonia, CP, IdTecnico);

                    stmt = con.createStatement();
                    stmt.execute(query);
                    stmt.close();

                    return true;
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
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
