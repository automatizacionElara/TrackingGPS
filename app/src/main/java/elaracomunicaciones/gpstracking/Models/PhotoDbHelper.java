package elaracomunicaciones.gpstracking.Models;

/**
 * Created by luis aranda on 11/10/2016.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PhotoDbHelper extends SQLiteOpenHelper
{
    public static final int DATABASE_VERSION = 5;
    public static final String DATABASE_NAME = "Elara_Service_Photos";
    String sqlCreate = "CREATE TABLE Elara_Service_Photos (IdPhoto INTEGER PRIMARY KEY AUTOINCREMENT," +
            " IdService INTEGER, IdType INTEGER, PhotoDescription VARCHAR(100), StringPhoto BLOB, Status INTEGER, PhotoDate VARCHAR(100))";

    public PhotoDbHelper(Context context)
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
        db.execSQL("DROP TABLE IF EXISTS Elara_Service_Photos");
        db.execSQL(sqlCreate);
        //onCreate(db);
    }

    public long savePhoto(Photo photo)
    {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PhotoContract.PhotoEntry.IdService, photo .IdService);
        values.put(PhotoContract.PhotoEntry.IdType, photo.IdType);
        values.put(PhotoContract.PhotoEntry.PhotoDescription, photo.PhotoDescription);
        values.put(PhotoContract.PhotoEntry.StringPhoto, photo.StringPhoto);
        values.put(PhotoContract.PhotoEntry.PhotoDate, photo.PhotoDate);

        return  sqLiteDatabase.insert(
                PhotoContract.PhotoEntry.TABLE_NAME,null,values
        );
    }

    public boolean deletePhoto(String IdPhoto)
    {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        return sqLiteDatabase.delete(PhotoContract.PhotoEntry.TABLE_NAME, "IdPhoto =" + IdPhoto, null) > 0;
    }

    public boolean updatePhoto(String IdPhoto)
    {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        //return sqLiteDatabase.update(PhotoContract.PhotoEntry.TABLE_NAME,"IdPhoto=" + IdPhoto,null) > 0;
        sqLiteDatabase.execSQL("UPDATE Elara_Service_Photos SET Status = 0 WHERE IdPhoto = " + IdPhoto);
        String[] args = new String[]{IdPhoto};
        ContentValues valores = new ContentValues();
        valores.put("Status", "0");
        return sqLiteDatabase.update("Elara_Service_Photos", valores, "IdPhoto=?",args) > 0;
    }

    public boolean updateAlreadyPhoto(String IdService, String PhotoDescription, String Photo)
    {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String[] args = new String[]{IdService, PhotoDescription};
        ContentValues valores = new ContentValues();
        valores.put("StringPhoto", Photo);
        return  sqLiteDatabase.update("Elara_Service_Photos", valores, "IdService=? AND PhotoDescription=?", args) > 0;
    }

    public Cursor getPhotoByDescription(String IdService, String PhotoDescription)
    {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        String[] campos = new String[] {"IdPhoto","IdService", "IdType", "PhotoDescription", "StringPhoto", "Status", "PhotoDate"};
        String[] args = new String[] {IdService, PhotoDescription};

        return sqLiteDatabase.query("Elara_Service_Photos", campos, PhotoContract.PhotoEntry.IdService + "=? AND " + PhotoContract.PhotoEntry.PhotoDescription + "=?", args, null, null, null);
    }

    public Cursor getAllPhotos()
    {
        //return getReadableDatabase().query(TrackingContract.TrackingEntry.TABLE_NAME,null, null, null,null,null,null,null);
        String[] campos = new String[] {"IdPhoto","IdService", "IdType", "StringPhoto", "Status", "PhotoDate"};
        String[] args = new String[] {"usu1"};

        return getReadableDatabase().query("Elara_Service_Photos", campos, null, null, null, null, null);

    }

    public Cursor getAllActivePhotos()
    {
        //return getReadableDatabase().query(TrackingContract.TrackingEntry.TABLE_NAME,null, null, null,null,null,null,null);
        String[] campos = new String[] {"IdPhoto","IdService", "IdType", "StringPhoto", "Status", "PhotoDate"};
        String[] args = new String[] {"usu1"};

        return getReadableDatabase().query("Elara_Service_Photos", campos, PhotoContract.PhotoEntry.Status + "=?", new String[]{"1"}, null, null, null);

    }

    public Cursor getPhotoById(String photoid)
    {
        Cursor c = getReadableDatabase().query(
                PhotoContract.PhotoEntry.TABLE_NAME,
                null,
                PhotoContract.PhotoEntry._ID + " LIKE ?",
                new String[]{photoid},
                null,
                null,
                null);
        return c;
    }

    public Cursor getPhotoService(String idService)
    {
        String[] campos = new String[] {"IdPhoto","IdService", "IdType", "StringPhoto", "PhotoDate"};
        Cursor c = getReadableDatabase().query(PhotoContract.PhotoEntry.TABLE_NAME, campos, PhotoContract.PhotoEntry.IdService + "=?",new String[]{idService}, null, null,null );
        return c;
    }
}
