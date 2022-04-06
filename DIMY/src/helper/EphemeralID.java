package helper;

import java.math.BigInteger;
import java.util.Objects;
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
    }

    public void cancel() {
        cancelled = true;
    }

    public void run() {
        while (!cancelled) {
            generator();
            try {
                sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
