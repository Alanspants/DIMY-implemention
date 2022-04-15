import BF.DBF;
import EphID.EphemeralID;
import TCP.TCPObjSend;
import UDP.UDPReceive;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class Dimy {
    public static void main(String[] args) throws InterruptedException, NoSuchAlgorithmException {
        DBF dbf = new DBF();
        dbf.start();

        EphemeralID ephemeralID = new EphemeralID();
        ephemeralID.start();

        UDPReceive UDPRcv = new UDPReceive(5001, ephemeralID, dbf);
        UDPRcv.start();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    Thread.sleep(200);
                    System.out.println("\nDiagnosed Positive !!!");
                    System.out.println("CBF Creating and Uploading...");
                    //some cleaning up code...
                    TCPObjSend.sendCBF(dbf.newCBF());

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
