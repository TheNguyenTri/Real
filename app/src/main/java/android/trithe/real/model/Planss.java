package android.trithe.real.model;
import java.util.Date;
import java.util.Timer;

public class Planss {
    private String id;
    private String name;
    private String idpet;
    private Date day;
    private String time;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Planss(String id, String name, String idpet, Date day, String time) {
        this.id = id;
        this.name = name;
        this.idpet = idpet;
        this.day = day;
        this.time=time;

    }

    public Planss() {

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

    public String getIdpet() {
        return idpet;
    }

    public void setIdpet(String idpet) {
        this.idpet = idpet;
    }

    public Date getDay() {
        return day;
    }

    public void setDay(Date day) {
        this.day = day;
    }
}
