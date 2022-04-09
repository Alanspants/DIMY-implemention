import EphID.EphemeralID;

import java.security.NoSuchAlgorithmException;

public class Dimy {
    public static void main(String[] args) throws InterruptedException, NoSuchAlgorithmException {
        EphemeralID ephemeralID = new EphemeralID();
        ephemeralID.start();
    }
}
