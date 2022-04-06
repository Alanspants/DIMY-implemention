import EphID.EphemeralID;
import Helper.Helper;
import Shamir.SecretShare;

import java.math.BigInteger;

import static java.lang.Thread.sleep;

public class Dimy {
    public static void main(String[] args) throws InterruptedException {
        EphemeralID ephemeralID = new EphemeralID();
        ephemeralID.start();
        while(true) {
            sleep(1000);

            // Get ephID
            String ephID = ephemeralID.getID();
            System.out.println("ephID:" + ephID);

            // ephID reconstruct test
            SecretShare[] shares = ephemeralID.getShares();
            String share0 = shares[2].broadcastStr();
            String share1 = shares[3].broadcastStr();
            String share2 = shares[4].broadcastStr();
            SecretShare[] sharesRCV = SecretShare.createSecretShareArray(share0, share1, share2);
//            boolean recoverCheck = ephemeralID.sharesRecover(sharesRCV);
//            if (recoverCheck) System.out.println("Shares recover: success");
//            else System.out.println("Shares recover: failed");
            String prime_str = ephemeralID.getPrime().toString();
            System.out.println("bigId:" + ephemeralID.getBigId().hashCode());
            System.out.println("recover: " + Helper.sharesRecover(sharesRCV, new BigInteger(prime_str)).hashCode());
            System.out.println("prime:" + prime_str);
            System.out.println("------------");
        }
    }
}
