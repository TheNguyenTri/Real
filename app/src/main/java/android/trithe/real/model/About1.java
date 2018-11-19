package android.trithe.real.model;

public class About1 {
    private String name;
    private String subname;

// --Commented out by Inspection START (17/11/2018 2:47 CH):
//    public About1() {
//
//    }
// --Commented out by Inspection STOP (17/11/2018 2:47 CH)

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubname() {
        return subname;
    }


    public About1(String name, String subname) {

        this.name = name;
        this.subname = subname;
    }
}
