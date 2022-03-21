package helper;

public class EphemeralID {

    String id;

    public EphemeralID() {
        id = generator();
    }

    public String getID() {
        return id;
    }

    private String generator() {
        return "EphemeralID";
    }
}
