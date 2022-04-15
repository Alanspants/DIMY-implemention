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
import java.security.SecureRandom;

import static javax.xml.bind.DatatypeConverter.printHexBinary;

public class EphemeralID extends Thread {
    private BigInteger pubKey;
    private BigInteger priKey;
    private byte[] pubKeyByte;
    private byte[] priKeyByte;

    private volatile boolean cancelled;

    final int CERTAINTY = 256;
    final SecureRandom random = new SecureRandom();

    private BigInteger secret;
    private BigInteger prime;
    public SecretShare[] shares;

    KeyPairGenerator kpg;
    public KeyPair kp;

    public EphemeralID() throws NoSuchAlgorithmException {
        pubKey = new BigInteger("0");
        priKey = new BigInteger("0");
        cancelled = false;
        kpg = KeyPairGenerator.getInstance("EC");
        kpg.initialize(128);
    }

    public BigInteger getPubKey() {
        return pubKey;
    }

    public int getPubKeyHash() {
        return pubKey.hashCode();
    }

    public SecretShare[] getShares() {
        return shares;
    }

    public BigInteger getPrime() { return prime; }

    private void generator() {

        kp = kpg.generateKeyPair();

        System.out.println("\n----------------------");
        System.out.println("new generator running ...");

//        id = UUID.randomUUID().toString().replace("-","");

        PublicKey pub = kp.getPublic();
        pubKeyByte = pub.getEncoded();
        pubKey = new BigInteger(pubKeyByte);
        System.out.println("ephID (pubKey): " + pubKey);
        System.out.println("ephID hash value: " + pubKey.hashCode());

        PrivateKey pri = kp.getPrivate();
        priKeyByte = pri.getEncoded();
        priKey = new BigInteger(priKeyByte);

        prime = new BigInteger(pubKey.bitLength() + 1, CERTAINTY, random);
        shares = Shamir.split(pubKey, 3, 5, prime, random);
        System.out.println("----------------------\n");
    }

    public void cancel() {
        cancelled = true;
    }

    public void broadcastShares(int index) throws IOException {
        String msg = "Shamir " + pubKey.hashCode() + " " + prime + " " + shares[index].broadcastStr();
        System.out.println("Broadcasting... ");
        System.out.println("    [secret hash code]: " + pubKey.hashCode());
//        System.out.println("    [prime]: " + prime);
        System.out.println("    [share]: " + shares[index].broadcastStr());
        UDPBroadcast.broadcast(msg, InetAddress.getByName("255.255.255.255"));
    }

    public void broadcastDHPubKey(String myPubKey, String otherPubKey) throws IOException {
        System.out.println("----------------------");
        System.out.println("Diffle-Hellman broadcasting... ");
        System.out.println("    [other's pub key hash]: " + otherPubKey);
        System.out.println("    [my pub key]: " + myPubKey);
        String msg = "DH " +  otherPubKey + " " + myPubKey;
        System.out.println("    [DH][content]: " + msg);
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
