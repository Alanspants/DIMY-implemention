import helper.EphemeralID;

import static java.lang.Thread.sleep;

public class Dimy {
    public static void main(String[] args) throws InterruptedException {
        EphemeralID ephemeralID = new EphemeralID();
        ephemeralID.start();
        while(true) {
            System.out.println(ephemeralID.getID());
            sleep(1000);
        }
    }
}
