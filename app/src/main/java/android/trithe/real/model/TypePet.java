package android.trithe.real.model;

import android.support.annotation.NonNull;

public class TypePet {
    private String id;
    private String name;

    public TypePet(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public TypePet() {

    }


    @NonNull
    @Override
    public String toString() {
        return getName();
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
