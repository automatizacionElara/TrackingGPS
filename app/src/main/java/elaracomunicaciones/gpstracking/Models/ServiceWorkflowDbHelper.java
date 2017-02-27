package elaracomunicaciones.gpstracking.Models;

/**
 * Created by sandro manzano on 27/02/2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ServiceWorkflowDbHelper extends SQLiteOpenHelper
{
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ServiceWorkflow";
    String sqlCreate = "CREATE TABLE ServiceWorkflow (idService INTEGER, " +
            " idStatus INTEGER, dateTracking VARCHAR(100), latitude DOUBLE, longitude DOUBLE)";

    public ServiceWorkflowDbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        //Comandos SQL
        sqLiteDatabase.execSQL(sqlCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        //No hay operaciones
        db.execSQL("DROP TABLE IF EXISTS ServiceWorkflow");
        db.execSQL(sqlCreate);
        //onCreate(db);
    }

    public long saveServiceWorkflow(ServiceWorkflow sw)
    {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ServiceWorkflowContract.ServiceWorkflowEntry.idService, sw.idService);
        values.put(ServiceWorkflowContract.ServiceWorkflowEntry.idStatus, sw.idStatus);
        values.put(ServiceWorkflowContract.ServiceWorkflowEntry.dateTracking, sw.dateTracking);
        values.put(ServiceWorkflowContract.ServiceWorkflowEntry.latitude, sw.latitude);
        values.put(ServiceWorkflowContract.ServiceWorkflowEntry.longitude, sw.longitude);

        return  sqLiteDatabase.insert(
                ServiceWorkflowContract.ServiceWorkflowEntry.TABLE_NAME,null,values
        );
    }

    public boolean deleteServiceWorkflow(int idService, int idStatus)
    {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        return sqLiteDatabase.delete(ServiceWorkflowContract.ServiceWorkflowEntry.TABLE_NAME, "idService = " + idService + " AND idStatus = " + idStatus, null) > 0;
    }

    public Cursor getAllServiceWorkflow()
    {
        String[] campos = new String[] {"idService","idStatus", "dateTracking", "latitude", "longitude"};
        String[] args = new String[] {"usu1"};

        return getReadableDatabase().query("ServiceWorkflow", campos, null, null, null, null, null);

    }
}
