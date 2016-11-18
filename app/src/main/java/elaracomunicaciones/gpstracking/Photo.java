package elaracomunicaciones.gpstracking;

/**
 * Created by luis aranda on 11/10/2016.
 */

public class Photo
{
    public int IdService;
    public int IdType;
    public String StringPhoto;

    public Photo(int idservice, int idtype, String photo)
    {
        this.IdService = idservice;
        this.IdType = idtype;
        this.StringPhoto = photo;
    }

    public int getIdService() { return  IdService; }
    public int getIdType() { return  IdType; }
    public String StringPhoto() { return  StringPhoto; }
}
