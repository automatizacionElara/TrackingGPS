package elaracomunicaciones.gpstracking;

/**
 * Created by luis aranda on 11/10/2016.
 */

import net.sourceforge.jtds.jdbc.DateTime;
import java.util.Date;

public class Tracking
{
    public int IdService;
    public String DateTracking;
    public final double Latitude;
    public final double Longitude;

    public Tracking(int idservice, String datetracking, double latitude, double longitude)
    {
        this.IdService = idservice;
        this.DateTracking = datetracking;
        this.Latitude = latitude;
        this.Longitude = longitude;
    }

    public int getIdService() { return  IdService; }
    public String getDateTracking() { return  DateTracking; }
    public double Latitude() { return  Latitude; }
    public double Longitude () { return  Longitude; }
}
