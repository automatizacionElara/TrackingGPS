package elaracomunicaciones.gpstracking.Models;

/**
 * Created by sandro manzano on 27/02/2017.
 */

public class ServiceWorkflow
{
    public int idService;
    public int idStatus;
    public String dateTracking;
    public final double latitude;
    public final double longitude;

    public ServiceWorkflow(int idService, int idStatus, String dateTracking, double latitude, double longitude)
    {
        this.idService = idService;
        this.idStatus = idStatus;
        this.dateTracking = dateTracking;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public ServiceWorkflow(int idService, int idStatus, String dateTracking)
    {
        this.idService = idService;
        this.idStatus = idStatus;
        this.dateTracking = dateTracking;
        this.latitude = Double.parseDouble(null);
        this.longitude = Double.parseDouble(null);
    }
}
