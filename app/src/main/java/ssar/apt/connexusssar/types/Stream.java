package ssar.apt.connexusssar.types;

/**
 * Created by ssyed on 10/15/14.
 */
public class Stream {
    private String streamName;
    //private Date creationDate;
    //private List<ViewDate> viewDateList;
    //private String owner;
    //private String subMessage;
    //private List<String> streamsSubscribers;
    //private List<String> tagList;
    private String coverURL;
   // private String comments;
   // private List<Images> imageList;

    public String getStreamName() {
        return streamName;
    }

    public void setStreamName(String streamName) {
        this.streamName = streamName;
    }

    public String getCoverURL() {
        return coverURL;
    }

    public void setCoverURL(String coverURL) {
        this.coverURL = coverURL;
    }

    public String toString() {
        return("Stream Name: " + streamName + ", Cover URL: " + coverURL);
    }
}
