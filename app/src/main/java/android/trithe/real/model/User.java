package android.trithe.real.model;

public class User {
    private String userName;
    private String name;
    private int age;
    private String phone;
    private String gmail;
    private byte[] image;

    public User() {
    }

    public User(String userName, String name, int age, String phone, String gmail, byte[] image) {
        this.userName = userName;
        this.name = name;
        this.age = age;
        this.phone = phone;
        this.gmail = gmail;
        this.image = image;
    }

    public int getAge() {
        return age;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGmail() {
        return gmail;
    }

    public void setGmail(String gmail) {
        this.gmail = gmail;
    }
}
