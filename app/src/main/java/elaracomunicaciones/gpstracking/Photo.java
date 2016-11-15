package elaracomunicaciones.gpstracking;

/**
 * Created by luis aranda on 11/10/2016.
 */

public class Photo
{
    public int IdService;
    public String DatePhoto;
    public String StringPhoto;

    public Photo(int idservice, String datephoto, String photo)
    {
        this.IdService = idservice;
        this.DatePhoto = datephoto;
        this.StringPhoto = photo;
    }

    public int getIdService() { return  IdService; }
    public String getDatePhoto() { return  DatePhoto; }
    public String StringPhoto() { return  StringPhoto; }
}
