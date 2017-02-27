package elaracomunicaciones.gpstracking.Models;

import android.provider.BaseColumns;

/**
 * Created by sandro manzano on 27/02/2017.
 */

public class ServiceWorkflowContract
{
    public static abstract class ServiceWorkflowEntry implements BaseColumns
    {
        public static final String TABLE_NAME = "ServiceWorkflow";

        public static final String idService = "idService";
        public static final String idStatus = "idStatus";
        public static final String dateTracking = "dateTracking";
        public static final String latitude = "latitude";
        public static final String longitude = "longitude";
    }
}
