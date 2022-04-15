package UDP;

import BF.DBF;
import EphID.EphemeralID;
import Helper.Helper;
import Shamir.SecretShare;
import Shamir.Shamir;

import javax.crypto.KeyAgreement;
import java.io.IOException;
import java.math.BigInteger;
import java.net.*;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.HashMap;

import static Shamir.SecretShare.getSecretShareByStr;
import static javax.xml.bind.DatatypeConverter.printHexBinary;

public class UDPReceive extends Thread {
    HashMap<String, String[]> shares = new HashMap<>();
    int port;
    EphemeralID ephID;
    BigInteger recoverEphID;
    DBF dbf;

    public UDPReceive(int port, EphemeralID ephID, DBF dbf) {
        this.port = port;
        this.ephID = ephID;
        this.dbf = dbf;
    }

    public boolean verify(String content) {
        String ephIDHash = content.split(" ")[1];
        String prime = content.split(" ")[2];
        String share = content.split(" ")[3];

        if (!shares.containsKey(ephIDHash)){
            String[] shareArray = new String[]{share, "", ""};
            shares.put(ephIDHash, shareArray);
        } else {
            String[] shareArray = shares.get(ephIDHash);
            if (!(shareArray[0].equals("") || shareArray[1].equals("") || shareArray[2].equals(""))) {
                return false;
            }
            int i = 0;
            for(i = 0; i < 3; i++) {
                if (shareArray[i].equals("")) {
                    shareArray[i] = share;
                    break;
                }
            }
            shares.put(ephIDHash, shareArray);
            if (i >= 2) {
                SecretShare[] secretShare = {
                        getSecretShareByStr(shareArray[0]),
                        getSecretShareByStr(shareArray[1]),
                        getSecretShareByStr(shareArray[2]),
                };
                BigInteger recoverResult = Shamir.combine(secretShare, new BigInteger(prime));
                System.out.println("Recovering...");
                System.out.println("    [actual ephIDHash]: " + ephIDHash);
                System.out.println("    [recover ephIDHash]: " + recoverResult.hashCode());
                if (Integer.parseInt(ephIDHash) == recoverResult.hashCode()) {
                    System.out.println("    [recover result]: YES!!");
                    recoverEphID = recoverResult;
                    return true;
                } else {
                    System.out.println("    [recover result]: NO!!");
                    return false;
                }
            }
        }
        return false;
    }

    private void DH(BigInteger otherPKBI) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {
        byte[] otherPK = otherPKBI.toByteArray();
        KeyFactory kf = KeyFactory.getInstance("EC");
        X509EncodedKeySpec pkSpec = new X509EncodedKeySpec(otherPK);
        PublicKey otherPublicKey = kf.generatePublic(pkSpec);

        KeyAgreement ka = KeyAgreement.getInstance("ECDH");
        ka.init(ephID.kp.getPrivate());
        ka.doPhase(otherPublicKey, true);

        byte[] sharedSecret = ka.generateSecret();
        System.out.println("DHing...");
        System.out.println("    [Share key]: " + printHexBinary(sharedSecret));
        dbf.insert(printHexBinary(sharedSecret));
    }

    public void run() {
        DatagramSocket ds = null;
        try {
            ds = new DatagramSocket(null);
            ds.setReuseAddress(true);
            ds.bind(new InetSocketAddress(55001));
        } catch (SocketException e) {
            e.printStackTrace();
        }
        while (true) {
            byte[] data = new byte[1024];
            DatagramPacket dp = new DatagramPacket(data, data.length);
            try {
                ds.receive(dp);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String content = new String(data, 0, dp.getLength());
            if (content.split(" ")[0].equals("Shamir")) {
                if (Integer.parseInt(content.split(" ")[1]) != ephID.getPubKeyHash()) {
                    System.out.println("Receiving... ");
                    System.out.println("    [Shamir][content]: " + content);
                    if (verify(content)) {
                        // 如果接收方完成了对发送方的ephID的reconstruct
                        // 进行一个广播，将自己的public key广播出去
                        // 同时广播对方的public key的hash值，从而使得对方知道这个DH public key是发给自己的
                         try {
                            ephID.broadcastDHPubKey(ephID.getPubKey().toString(), String.valueOf(recoverEphID.hashCode()));
                            DH(recoverEphID);
                        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException e) {
                            e.printStackTrace();
                        }
                        System.out.println("----------------------");

                    }
                }
            } else if (content.split(" ")[0].equals("DH")) {
                String myPubKey = content.split(" ")[1];
                String otherPubKey = content.split(" ")[2];
                // 通过比对DH msg中的hash值和自己pubKey的hash值，判断这条msg是不是发给自己的
                if (myPubKey.equals(String.valueOf(ephID.getPubKey().hashCode()))) {
                    System.out.println("----------------------");
                    System.out.println("Diffle-Hellman receiving... ");
                    System.out.println("    [DH][content]: " + content);
                    try {
                        DH(new BigInteger(otherPubKey));
                    } catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException e) {
                        e.printStackTrace();
                    }
                    System.out.println("----------------------");
                }
            }
        }
    }

}
