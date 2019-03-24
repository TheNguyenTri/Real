package android.trithe.real.model;

import java.util.Date;

public class Likes extends LikeId{
    private Date timestamp;

    public Likes(){

    }

    public Likes( Date timestamp) {
        this.timestamp = timestamp;
    }
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
