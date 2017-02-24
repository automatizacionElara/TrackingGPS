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
    private final String street;
    private final String extNum;
    private final String intNum;
    private final String Neig;
    private final int postalCode;
    private final int IdTecnico;

    String msg = "";

    public SendAddress(String elaraReference, String street, String extNum, String intNum, String Neig, String postalCode, int IdTec) {
        this.elaraReference = elaraReference;
        this.street = street.contentEquals("") ? "null" : "'" + street + "'";
        this.extNum = extNum.contentEquals("") ? "null" : "'" + extNum + "'";
        this.intNum = intNum.contentEquals("") ? "null" : "'" + intNum + "'";
        this.Neig = Neig.contentEquals("") ? "null" : "'" + Neig + "'";
        this.postalCode = postalCode.contentEquals("") ? -1 : Integer.parseInt(postalCode);
        IdTecnico = IdTec;
    }


    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            String msg = "";
            Connection con = new DBConnection().getInstance().getConnection();
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

                    if(postalCode == -1)
                    {
                        query = String.format("INSERT INTO VsatAddress (IdVsatService, Street, StreetNumber,"
                                + "ApartmentNumber, Neighborhood, Approved, Rejected, IdTechnician)"
                                + "VALUES(%1d, %2s, %3s, %4s, %5s, 0, 0, %7d)", idVsatService, street, extNum, intNum, Neig, IdTecnico);

                    }
                    else
                    {
                        query = String.format("INSERT INTO VsatAddress (IdVsatService, Street, StreetNumber,"
                                + "ApartmentNumber, Neighborhood, PostalCode, Approved, Rejected, IdTechnician)"
                                + "VALUES(%1d, %2s, %3s, %4s, %5s, %6d, 0, 0, %7d)", idVsatService, street, extNum, intNum, Neig, postalCode, IdTecnico);

                    }

                    stmt = con.createStatement();
                    stmt.execute(query);
                    stmt.close();

                    return true;
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
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
