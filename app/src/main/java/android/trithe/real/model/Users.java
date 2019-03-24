package android.trithe.real.model;

public class Users extends UserId{
    String name,image,status;

    public Users() {
    }

    public String getName() {

        return name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Users(String name, String image, String status) {
        this.name = name;
        this.image = image;
        this.status = status;
    }

    @Override
    public String toString() {
        return getName();
    }
}
