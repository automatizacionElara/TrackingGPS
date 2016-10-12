package elaracomunicaciones.gpstracking;

/**
 * Created by luis aranda on 11/10/2016.
 */
import android.provider.BaseColumns;

public class TrackingContract
{
    public static abstract class TrackingEntry implements BaseColumns
    {
        public static final String TABLE_NAME = "Elara_Tracking";

        public static final String IdService = "IdService";
        public static final String DateTracking = "DateTracking";
        public static final String Latitude = "Latitude";
        public static final String Longitude = "Longitude";
    }
}
