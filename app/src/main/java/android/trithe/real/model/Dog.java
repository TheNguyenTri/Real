package android.trithe.real.model;

public class Dog {
    private String dogid;
    private String name;
    private int age;
    private float price;
    private byte[] image;

    public Dog() {

    }

    public String getDogid() {
        return dogid;
    }

    public void setDogid(String dogid) {
        this.dogid = dogid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public int getAge() {
        return age;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public Dog(String dogid, String name, int age, float price, byte[] image) {
        this.dogid = dogid;
        this.name = name;
        this.age = age;
        this.price = price;
        this.image = image;
    }

    public void setAge(int age) {
        this.age = age;
    }


    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
