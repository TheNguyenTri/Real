package android.trithe.real.model;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class InfoUserPostId {
    @Exclude
    public String InfoUserPostId;

    public <T extends InfoUserPostId> T withId(@NonNull final String id) {
        this.InfoUserPostId = id;
        return (T) this;
    }
}
