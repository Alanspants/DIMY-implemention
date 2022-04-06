import EphID.EphemeralID;

import static java.lang.Thread.sleep;

public class Dimy {
    public static void main(String[] args) throws InterruptedException {
        EphemeralID ephemeralID = new EphemeralID();
        ephemeralID.start();
//        while(true) {
//            sleep(1000);
//
//            // Get ephID
//            String ephID = ephemeralID.getID();
//            System.out.println("ephID:" + ephID);
//
//            // ephID reconstruct test
//            SecretShare[] shares = ephemeralID.getShares();
//            String share0 = shares[0].broadcastStr();
//            String share1 = shares[1].broadcastStr();
//            String share2 = shares[2].broadcastStr();
//            SecretShare[] sharesRCV = SecretShare.createSecretShareArray(share0, share1, share2);
//            boolean recoverCheck = ephemeralID.sharesRecover(sharesRCV);
//            if (recoverCheck) System.out.println("Shares recover: success");
//            else System.out.println("Shares recover: failed");
//        }
    }
}
