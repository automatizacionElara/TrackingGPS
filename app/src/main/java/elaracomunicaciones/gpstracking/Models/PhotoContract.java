package elaracomunicaciones.gpstracking.Models;

/**
 * Created by luis aranda on 11/10/2016.
 */
import android.provider.BaseColumns;

public class PhotoContract
{
    public static abstract class PhotoEntry implements BaseColumns
    {
        public static final String TABLE_NAME = "Elara_Service_Photos";

        public static final String IdService = "IdService";
        public static final String IdType = "IdType";
        public static final String PhotoDescription = "PhotoDescription";
        public static final String StringPhoto = "StringPhoto";
        public static final String Status = "Status";
        public static final String PhotoDate = "PhotoDate";
    }
}
