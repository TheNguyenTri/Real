package android.trithe.real.model;

import java.util.Date;

public class NotificationsModel {
    private String blog_id;
    private String body;
    private String from;
    private long timestamp;

    public NotificationsModel(String blog_id, String body, String from, long timestamp) {
        this.blog_id = blog_id;
        this.body = body;
        this.from = from;
        this.timestamp = timestamp;
    }

    public String getBlog_id() {
        return blog_id;
    }

    public void setBlog_id(String blog_id) {
        this.blog_id = blog_id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public NotificationsModel() {
    }

}

