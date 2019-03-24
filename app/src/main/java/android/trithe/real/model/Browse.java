package android.trithe.real.model;

public class Browse {
    private String name;
    private final String url;
    private String timeline;

    public Browse(String name, String url, String timeline, String image) {
        this.name = name;
        this.url = url;
        this.timeline = timeline;
        this.image = image;
    }

    public String getTimeline() {
        return timeline;
    }

    public void setTimeline(String timeline) {
        this.timeline = timeline;
    }

    private String image;


    public String getUrl() {
        return url;
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
