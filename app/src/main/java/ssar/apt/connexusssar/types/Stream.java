package ssar.apt.connexusssar.types;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

/**
 * Created by ssyed on 10/15/14.
 */
public class Stream {
    private String streamName;
    @SerializedName("date")
    private Date creationDate;
    //private List<ViewDate> viewDateList;
    private String owner;
    private String subMessage;
    private List<String> subscribers;
    private List<String> tagList;
    private String coverURL;
    private List<String> commentList;
   //private List<Images> imageList;

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

    public Date getCreationDate() {
        return creationDate;
    }

    public String getSubMessage() {
        return subMessage;
    }

    public void setSubMessage(String subMessage) {
        this.subMessage = subMessage;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public List<String> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<String> commentList) {
        this.commentList = commentList;
    }

    public List<String> getTagList() {
        return tagList;
    }

    public void setTagList(List<String> tagList) {
        this.tagList = tagList;
    }

    public List<String> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(List<String> subscribers) {
        this.subscribers = subscribers;
    }

    public String toString() {
        StringBuilder streamString = new StringBuilder();
        streamString.append("Stream Name: " + streamName + ", Creation Date: " + creationDate + ", Owner: " + owner  + ", SubMessage: " + subMessage + ", Cover URL: " + coverURL);
        if (subscribers != null) {
            StringBuilder subscriberString = new StringBuilder();
            subscriberString.append("{");
            for (String subscriber : subscribers) {
                subscriberString.append(subscriber).append(", ");
            }
            subscriberString.append("}");
            streamString.append(", Subscribers: " + subscriberString.toString());
        }
        if(tagList != null) {
            StringBuilder tagListString = new StringBuilder();
            tagListString.append("{");
            for (String tag : tagList) {
                tagListString.append(tag).append(", ");
            }
            tagListString.append("}");
            streamString.append(", Tags: " + tagListString.toString());
        }
        if(commentList != null) {
            StringBuilder commentListString = new StringBuilder();
            commentListString.append("{");
            for (String comment : commentList) {
                commentListString.append(comment).append(", ");
            }
            commentListString.append("}");
            streamString.append(", Comments List: " + commentListString.toString());
        }
        return(streamString.toString());
    }
}
