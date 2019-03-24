package android.trithe.real.model;

public class Lich {
    private String ngay;
    private String thu;
    private String url;

    public String getNgay() {
        return ngay;
    }

    public Lich(String ngay, String thu, String url) {
        this.ngay = ngay;
        this.thu = thu;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setNgay(String ngay) {
        this.ngay = ngay;
    }

    public String getThu() {
        return thu;
    }

    public void setThu(String thu) {
        this.thu = thu;
    }

}
