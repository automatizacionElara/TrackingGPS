package elaracomunicaciones.gpstracking;

import android.os.AsyncTask;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by daniel sosa on 05/09/2016.
 */
public class RegisterService extends AsyncTask<Void, Void, Boolean> {

    private final int IdTechnician;
    private final int IdService;
    private final int Estatus;
    private boolean IsSuccess;
    String msg = "";

    RegisterService(int IdTech, int IdServ, int Status) {
        IdTechnician = IdTech;
        IdService = IdServ;
        Estatus = Status;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            String msg = "";
            Connection con = DBConnection.getInstance().getConnection();
            if (con == null) {
                msg = "Error en la Conexión con SQL server";
            } else {
                String Elara_ES_Tracking= "INSERT INTO Elara_ES_Tracking (IdTechnician, IdService, IdStatus) VALUES(" + IdTechnician + "," + IdService + "," + Estatus+ ");";
                String Elara_ES_TrackingUpdate = "UPDATE dbo.Elara_ES_Tracking SET IdStatus = "+ Estatus + " WHERE IdService = " + IdService + ";";
                Statement stmt = null;

                try {
                    stmt = con.createStatement();
                    if(Estatus != 1){
                        stmt.executeQuery(Elara_ES_TrackingUpdate);
                    }else{
                        stmt.executeQuery(Elara_ES_Tracking);
                    }
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


