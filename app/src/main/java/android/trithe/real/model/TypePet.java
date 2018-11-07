package android.trithe.real.model;

import android.support.annotation.NonNull;

public class TypePet {
    private String id;
    private String name;
    private int image;

    public TypePet(String id, String name, int image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }

    public TypePet() {

    }

    public int getImage() {

        return image;
    }

    @NonNull
    @Override
    public String toString() {
        return getName();
    }

    public void setImage(int image) {
        this.image = image;
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
