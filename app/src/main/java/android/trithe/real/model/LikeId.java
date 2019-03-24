package android.trithe.real.model;

import android.support.annotation.NonNull;

public class LikeId {
    public String likeUserId;

    public <T extends LikeId> T withId(@NonNull final String id) {
        this.likeUserId = id;
        return (T) this;
    }
}
