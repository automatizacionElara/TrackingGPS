package elaracomunicaciones.gpstracking;

import java.sql.*;
/**
 * Created by daniel sosa on 05/08/2016.
 */
public class DBConnectionMySQL {

    static String host      = "192.168.27.33";
    static String dataBase = "cus";
    static String user   = "automator";
    static String pass  = "N3mqtj!!";
    static String cadCon	= "jdbc:mysql://"+host+"/"+dataBase;

    public static Connection con;
    public static Statement st;

    public static void StartConnection() throws InstantiationException, IllegalAccessException, ClassNotFoundException {

        Class.forName( "com.mysql.jdbc.Driver").newInstance();

        try{
            con = DriverManager.getConnection( cadCon, user, pass);
            st = con.createStatement();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    /**
     * Cierra la conexion con la BBDD
     */
    public static void CloseConnection() {
        try {
            if (st != null) {
                st.close();
            }
            if (con != null) {
                con.close();
            }
        } catch (SQLException e) {
        }
    }

}
