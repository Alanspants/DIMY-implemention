package EphID;

import Shamir.SecretShare;
import Shamir.Shamir;
import UDP.UDPBroadcast;
import Helper.Helper;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.util.UUID;


public class EphemeralID extends Thread {

    private Shamir shamir;
    private BigInteger prime;
    private SecretShare[] shares;
    private String id;
    private BigInteger bigId;
    private volatile boolean cancelled;

    public EphemeralID() {
        id = "";
        shares = new SecretShare[3];
        shamir = new Shamir(3, 5);
        cancelled = false;
    }

    public String getID() {
        return id;
    }

    public SecretShare[] getShares() {
        return shares;
    }

    public BigInteger getBigId() {
        return bigId;
    }

    public BigInteger getPrime() {
        return prime;
    }

    public boolean sharesRecover(SecretShare[] shares) {
        BigInteger result = shamir.combine(shares, prime);
        if (result.equals(bigId)) {
            return true;
        } else {
            return false;
        }
    }

    private void generator() {
        System.out.println("new generator running ...");
        id = UUID.randomUUID().toString().replace("-","");
        bigId = new BigInteger(id, 16);
        shares = shamir.split(bigId);
        prime = shamir.getPrime();
        System.out.println("ephID: " + id);
    }

    public void cancel() {
        cancelled = true;
    }

    public void broadcastShares(int index) throws IOException {
        String msg = bigId.hashCode() + " " + prime + " " + shares[index].broadcastStr();
        System.out.println("Broadcast ing... " + msg);
        UDPBroadcast.broadcast(msg, InetAddress.getByName("255.255.255.255"));
    }

    public void run() {
        while (!cancelled) {
            generator();
            for (int index = 0; index < 5; index++) {
                try {
                    if (!Helper.msgDrop()) {
                        broadcastShares(index);
                    }
                    sleep(3000);
                } catch (IOException|InterruptedException e) {
                    e.printStackTrace();
                }
            }
//            try {
//                sleep(15000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
    }
}
