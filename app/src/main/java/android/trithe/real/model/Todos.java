package android.trithe.real.model;

public class Todos {
    private String title;
    private String description;
    private String times;

    public Todos(String title, String description, String times) {
        this.title = title;
        this.description = description;
        this.times = times;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public Todos() {
    }
}
