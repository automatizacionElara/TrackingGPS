package elaracomunicaciones.gpstracking.Models;

/**
 * Created by luis aranda on 11/10/2016.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TrackingDbHelper extends SQLiteOpenHelper
{
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Elara_Tracking";
    String sqlCreate = "CREATE TABLE Elara_Tracking (IdTracking INTEGER PRIMARY KEY AUTOINCREMENT, IdService INTEGER, DateTracking DATETIME, Latitude DOUBLE, Longitude DOUBLE)";

    public TrackingDbHelper(Context context)
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
        db.execSQL("DROP TABLE IF EXISTS Elara_Tracking");

        onCreate(db);
    }

    public long saveTracking(Tracking tracking)
    {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TrackingContract.TrackingEntry.IdService, tracking.IdService);
        values.put(TrackingContract.TrackingEntry.DateTracking, tracking.DateTracking.toString());
        values.put(TrackingContract.TrackingEntry.Latitude, tracking.Latitude);
        values.put(TrackingContract.TrackingEntry.Longitude, tracking.Longitude);

        return  sqLiteDatabase.insert(
                TrackingContract.TrackingEntry.TABLE_NAME,null, values
        );
    }

    public boolean deleteTracking(String IdTracking)
    {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        return sqLiteDatabase.delete(TrackingContract.TrackingEntry.TABLE_NAME, "IdTracking =" + IdTracking, null) > 0;
    }

    public Cursor getAllTracking()
    {
        //return getReadableDatabase().query(TrackingContract.TrackingEntry.TABLE_NAME,null, null, null,null,null,null,null);
        String[] campos = new String[] {"IdTracking","IdService", "DateTracking", "Latitude", "Longitude"};
        String[] args = new String[] {"usu1"};

        return getReadableDatabase().query("Elara_Tracking", campos, null, null, null, null, null);

    }

    public Cursor getTrackingById(String trackingid)
    {
        Cursor c = getReadableDatabase().query(
                TrackingContract.TrackingEntry.TABLE_NAME,
                null,
                TrackingContract.TrackingEntry.IdService + " LIKE ?",
                new String[]{trackingid },
                null,
                null,
                null);
        return c;
    }
}
