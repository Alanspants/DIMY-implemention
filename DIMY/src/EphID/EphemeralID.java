package EphID;

import Shamir.SecretShare;
import Shamir.Shamir;
import UDP.UDPBroadcast;
import Helper.Helper;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.security.*;
import java.util.Base64;
import java.util.UUID;
import java.security.SecureRandom;

public class EphemeralID extends Thread {
    private String pubKey;
    private String priKey;
    private volatile boolean cancelled;

    final int CERTAINTY = 256;
    final SecureRandom random = new SecureRandom();

    private BigInteger secret;
    private BigInteger prime;
    public SecretShare[] shares;

    KeyPairGenerator kpg;
    KeyPair kp;

    public EphemeralID() throws NoSuchAlgorithmException {
        pubKey = "";
        priKey = "";
        cancelled = false;
        kpg = KeyPairGenerator.getInstance("EC");
        kpg.initialize(128);
    }

    public String getPubKey() {
        return pubKey;
    }

    public SecretShare[] getShares() {
        return shares;
    }

    public BigInteger getPrime() { return prime; }

    private void generator() {

        kp = kpg.generateKeyPair();

        System.out.println("----------------------");
        System.out.println("new generator running ...");
//        id = UUID.randomUUID().toString().replace("-","");
//        System.out.println("ephID: " + id);
        PublicKey pub = kp.getPublic();
        byte[] pubBytes = pub.getEncoded();
        pubKey = Base64.getEncoder().encodeToString(pubBytes);

        PrivateKey pri = kp.getPrivate();
        byte[] priBytes = pri.getEncoded();
        priKey = Base64.getEncoder().encodeToString(priBytes);

        secret = new BigInteger(pubKey.getBytes());
        prime = new BigInteger(secret.bitLength() + 1, CERTAINTY, random);
        shares = Shamir.split(secret, 3, 5, prime, random);
        System.out.println("secret: " + secret);
        System.out.println("----------------------");
    }

    public void cancel() {
        cancelled = true;
    }

    public void broadcastShares(int index) throws IOException {
        String msg = secret.hashCode() + " " + prime + " " + shares[index].broadcastStr();
        System.out.println("Broadcast ing... ");
        System.out.println("    [secret hash code]: " + secret.hashCode());
        System.out.println("    [prime]: " + prime);
        System.out.println("    [share]: " + shares[index].broadcastStr());
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
        }
    }
}
