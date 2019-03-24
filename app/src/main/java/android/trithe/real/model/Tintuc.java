package android.trithe.real.model;

public class Tintuc {
    private String name;
    private String author;
    private String image;
    private String url;

    public Tintuc(String name, String author, String image, String url) {
        this.name = name;
        this.author = author;
        this.image = image;
        this.url = url;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
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

}
