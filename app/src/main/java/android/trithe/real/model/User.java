package android.trithe.real.model;

public class User {
    private String userName;
    private String password;
    private String name;
    private String phone;
    private byte[] image;

    public User() {

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public User(String userName, String password, String name, byte[] image, String phone) {

        this.userName = userName;
        this.password = password;
        this.name = name;
        this.image = image;
        this.phone = phone;
    }
}
