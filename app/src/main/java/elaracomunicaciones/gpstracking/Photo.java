package elaracomunicaciones.gpstracking;

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

    public Photo(int idservice, int idtype, String photodescription, String photo, int status)
    {
        this.IdService = idservice;
        this.IdType = idtype;
        this.PhotoDescription = photodescription;
        this.StringPhoto = photo;
        this.Status = status;
    }

    public int getIdService() { return  IdService; }
    public int getIdType() { return  IdType; }
    public String getPhotoDescription() { return  PhotoDescription; }
    public String StringPhoto() { return  StringPhoto; }
    public int Status() { return Status; }
}
