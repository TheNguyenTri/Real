package android.trithe.real.model;

import android.support.annotation.NonNull;

public class MessagesId {
    public String userId;

    public <T extends MessagesId> T withId(@NonNull final String id) {
        this.userId = id;
        return (T) this;
    }
}
