package android.trithe.real.model;

import android.support.annotation.NonNull;

public class UserId {
    public String userId;

    public <T extends UserId> T withId(@NonNull final String id) {
        this.userId = id;
        return (T) this;
    }
}
