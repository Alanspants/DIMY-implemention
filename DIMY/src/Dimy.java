import EphID.EphemeralID;
import UDP.UDPReceive;

import java.security.NoSuchAlgorithmException;

public class Dimy {
    public static void main(String[] args) throws InterruptedException, NoSuchAlgorithmException {
        EphemeralID ephemeralID = new EphemeralID();
        ephemeralID.start();

        UDPReceive UDPRcv = new UDPReceive(5001, ephemeralID);
        UDPRcv.start();
    }
}
