package android.trithe.real.model;

public class Browse {
    public  String name;
    public String url;
    public String image;

    public Browse() {

    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
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

    public Browse(String name, String url, String image) {

        this.name = name;
        this.image = image;
        this.url=url;
    }
}
