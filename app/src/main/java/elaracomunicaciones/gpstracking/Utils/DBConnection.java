package elaracomunicaciones.gpstracking.Utils;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by daniel sosa on 27/07/2016.
 * Esta clase hace la conexión a la base de datos del servidor en la nube
 * conexion
 */
public class DBConnection 
{
    private DBConnection instance = null;
    private final String url;
    private final String user;
    private final String pass;
    private Connection connection = null;

    public DBConnection(){
        url = "jdbc:jtds:sqlserver://104.130.231.193/Dev_SeguimientoCuadrillas;";
        user= "Connection";
        pass = "C0nn3ctAnD!";
    }

    public DBConnection getInstance(){
        if(instance == null) {
            instance = new DBConnection();
        }
        return  instance;
    }

    public Connection getConnection(){
        if(connection == null){
            connection = connect();
        }
        return connection;
    }

    private Connection connect(){
        Connection con = null;
        Driver dv = new net.sourceforge.jtds.jdbc.Driver();
        try {
            dv.getClass();
            con = DriverManager.getConnection(url,user,pass);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return con;
    }
}
