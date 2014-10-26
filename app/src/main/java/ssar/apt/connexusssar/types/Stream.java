package ssar.apt.connexusssar.types;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

/**
 * Created by ssyed on 10/15/14.
 */
public class Stream {
    private String streamname;
    @SerializedName("date")
    private Date creationdate;
    //private List<ViewDate> viewDateList;
    private String owner;
    private String submessage;
    private List<String> subscribers;
    private List<String> taglist;
    private String coverurl;
    private List<String> commentlist;
    private List<String> imageurllist;
    private List<StreamImage> imagelist;

    public String getStreamname() {
        return streamname;
    }

    public void setStreamname(String streamName) {
        this.streamname = streamName;
    }

    public String getCoverurl() {
        return coverurl;
    }

    public void setCoverurl(String coverurl) {
        this.coverurl = coverurl;
    }

    public Date getCreationdate() {
        return creationdate;
    }

    public String getSubmessage() {
        return submessage;
    }

    public void setSubmessage(String submessage) {
        this.submessage = submessage;
    }

    public void setCreationdate(Date creationdate) {
        this.creationdate = creationdate;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public List<String> getCommentlist() {
        return commentlist;
    }

    public void setCommentlist(List<String> commentlist) {
        this.commentlist = commentlist;
    }

    public List<String> getTaglist() {
        return taglist;
    }

    public void setTaglist(List<String> taglist) {
        this.taglist = taglist;
    }

    public void setImageUrllist(List<String> imageurllist) {
        this.imageurllist = imageurllist;
    }

    public List<String> getImageUrllist() { return imageurllist;}

    public List<String> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(List<String> subscribers) {
        this.subscribers = subscribers;
    }

    public String toString() {
        StringBuilder streamString = new StringBuilder();
        streamString.append("Stream Name: " + streamname + ", Creation Date: " + creationdate + ", Owner: " + owner  + ", SubMessage: " + submessage + ", Cover URL: " + coverurl);
        if (subscribers != null) {
            StringBuilder subscriberString = new StringBuilder();
            subscriberString.append("{");
            for (String subscriber : subscribers) {
                subscriberString.append(subscriber).append(", ");
            }
            subscriberString.append("}");
            streamString.append(", Subscribers: " + subscriberString.toString());
        }
        if(taglist != null) {
            StringBuilder tagListString = new StringBuilder();
            tagListString.append("{");
            for (String tag : taglist) {
                tagListString.append(tag).append(", ");
            }
            tagListString.append("}");
            streamString.append(", Tags: " + tagListString.toString());
        }
        if(commentlist != null) {
            StringBuilder commentListString = new StringBuilder();
            commentListString.append("{");
            for (String comment : commentlist) {
                commentListString.append(comment).append(", ");
            }
            commentListString.append("}");
            streamString.append(", Comments List: " + commentListString.toString());
        }
        if(imageurllist != null) {
            StringBuilder imageUrllistString = new StringBuilder();
            imageUrllistString.append("{");
            for (String image : imageurllist) {
                imageUrllistString.append(image).append(", ");
            }
            imageUrllistString.append("}");
            streamString.append(", Image Url List: " + imageUrllistString.toString());
        }
        return(streamString.toString());
    }
}
