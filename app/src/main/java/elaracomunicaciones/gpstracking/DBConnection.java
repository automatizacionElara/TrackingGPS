package elaracomunicaciones.gpstracking;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 * Created by daniel sosa on 27/07/2016.
 */
public class DBConnection {
    private static  DBConnection instance = null;
    private static final String URL = "jdbc:jtds:sqlserver://172.31.248.4/Elara_Legal;";
    private static final String USER= "sa";
    private static final String PASS= "$D34dP00l#";
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
