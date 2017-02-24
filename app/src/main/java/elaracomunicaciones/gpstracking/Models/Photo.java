package elaracomunicaciones.gpstracking.Models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by luis aranda on 11/10/2016.
 */

public class Photo
{
    public int IdService;
    public int IdType;
    public String PhotoDescription;
    public String StringPhoto;
    public int Status;
    public String PhotoDate;

    public Photo(int idservice, int idtype, String photodescription, String photo, int status)
    {
        this.IdService = idservice;
        this.IdType = idtype;
        this.PhotoDescription = photodescription;
        this.StringPhoto = photo;
        this.Status = status;

        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        PhotoDate = df.format(Calendar.getInstance().getTime());
    }

    public int getIdService() { return  IdService; }
    public int getIdType() { return  IdType; }
    public String getPhotoDescription() { return  PhotoDescription; }
    public String StringPhoto() { return  StringPhoto; }
    public int Status() { return Status; }
    public String PhotoDate() { return PhotoDate; }
}
