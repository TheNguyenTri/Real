package android.trithe.real.model;

import android.support.annotation.NonNull;

public class Pet {
private String id;
private String name;
private String giongloai;
private int age;
private String health;
private float weight;
private String gender;
private byte[] image;

    public Pet() {

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

    public String getGiongloai() {
        return giongloai;
    }

    public void setGiongloai(String giongloai) {
        this.giongloai = giongloai;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getHealth() {
        return health;
    }

    public void setHealth(String health) {
        this.health = health;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Pet(String id, String name, String giongloai, int age, String health, float weight, String gender, byte[] image) {
        this.id = id;
        this.name = name;
        this.giongloai = giongloai;
        this.age = age;
        this.health = health;
        this.weight = weight;
        this.gender = gender;
        this.image = image;
    }

    @NonNull
    @Override
    public String toString() {
        return getName();
    }

    public byte[] getImage() {

        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
