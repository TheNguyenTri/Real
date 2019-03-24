package android.trithe.real.model;



public class Logout {
    private String label;
    private int icon;

    public Logout(String label, int icon){
        this.label = label;
        this.icon = icon;
    }

    public Logout() {
    }

    public String getLabel(){
        return this.label;
    }

    public int getIcon(){
        return this.icon;
    }
}
