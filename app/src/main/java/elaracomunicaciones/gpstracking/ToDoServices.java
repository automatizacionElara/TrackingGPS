package elaracomunicaciones.gpstracking;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.sql.Connection;

public class ToDoServices extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todoservices);
        try {
            DBConnectionMySQL.StartConnection();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
