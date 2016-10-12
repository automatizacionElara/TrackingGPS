package elaracomunicaciones.gpstracking;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 * Created by daniel sosa on 27/07/2016.
 * Esta clase hace la conexión a la base de datos del servidor en la nube
 * conexion
 */
public class DBConnection {
    private static  DBConnection instance = null;
    private static final String URL = "jdbc:jtds:sqlserver://104.130.231.193/Elara_SeguimientoCuadrillas;";
    private static final String USER= "Connection";
    private static final String PASS= "C0nn3ctAnD!";
    private static Connection connection = null;

    private DBConnection(){}

    public static DBConnection getInstance(){
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
            con = DriverManager.getConnection(URL,USER,PASS);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return con;
    }

}
