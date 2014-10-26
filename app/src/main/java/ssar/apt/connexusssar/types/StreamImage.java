package ssar.apt.connexusssar.types;

/**
 * Created by Amy on 10/25/2014.
 */
public class StreamImage {
    private String imageid;
    private String imagefilename;
    private String comments;
    private String imagefileurl;
    private String imagecreationdate;
    private String imagestreamname;
    private Float imagelatitude;
    private Float imagelongitude;

    public String getImageId() {
        return imageid;
    }

    public void setImageid(String imageid) {
        this.imageid = imageid;
    }

    public String getImageFilename() {
        return imagefilename;
    }

    public void setImageFilename(String imagefilename) {
        this.imagefilename = imagefilename;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getImageFileUrl() {
        return imagefileurl;
    }

    public void setImageFileUrl(String imagefileurl) {
        this.imagefileurl = imagefileurl;
    }

    public String getImageCreationDate() {
        return imagecreationdate;
    }

    public void setImageCreationDate(String imagecreationdate) {
        this.imagecreationdate = imagecreationdate;
    }

    public String getImageStreamName() {
        return imagestreamname;
    }

    public void setImageStreamName(String imagestreamname) {
        this.imagestreamname = imagestreamname;
    }

    public Float getImageLatitude() {
        return imagelatitude;
    }

    public void setImageLatitude(Float imagelatitude) {
        this.imagelatitude = imagelatitude;
    }

    public Float getImageLongitude() {
        return imagelongitude;
    }

    public void setImageLongitude(Float imagelongitude) {
        this.imagelongitude = imagelongitude;
    }

    public String toString() {
        StringBuilder streamImageString = new StringBuilder();
        private String imagestreamname;
        private Float imagelatitude;
        private Float imagelongitude;
        streamImageString.append("Image id: " + imageid + ", Image Filename: " + imagefilename + ", Comments: " + comments  + ", Image File Url: " + imagefileurl + ", Image Creation Date: " + imagecreationdate + ", Image Stream Name: " + imagestreamname + ", Image Latitude: " + Float.toString(imagelatitude) + ", Image Longitude: " + Float.toString(imagelongitude));
        return(streamImageString.toString());
    }

}
