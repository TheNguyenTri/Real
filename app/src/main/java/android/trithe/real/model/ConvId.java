package android.trithe.real.model;

import android.support.annotation.NonNull;

public class ConvId {
    public String userId;

    public <T extends ConvId> T withId(@NonNull final String id) {
        this.userId = id;
        return (T) this;
    }
}
