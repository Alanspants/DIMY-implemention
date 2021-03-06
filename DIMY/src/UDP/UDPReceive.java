package UDP;

import BF.DBF;
import EphID.EphemeralID;
import Helper.Helper;
import Shamir.SecretShare;
import Shamir.Shamir;

import javax.crypto.KeyAgreement;
import javax.xml.bind.SchemaOutputResolver;
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
            System.out.println("    [order]: 1");
        } else {
            String[] shareArray = shares.get(ephIDHash);
            if (!(shareArray[0].equals("") || shareArray[1].equals("") || shareArray[2].equals(""))) {
                return false;
            }
            if (shareArray[1].equals("")) System.out.println("    [order]: 2");
            else if (shareArray[2].equals("")) System.out.println("    [order]: 3");
            else System.out.println("    [order]: Already reconstruct, discard.");
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
                System.out.println("    [recover ephID]: " + recoverResult);
                System.out.println("    [recover ephID Hash]: " + recoverResult.hashCode());
                System.out.println("    [actual ephID Hash]: " + ephIDHash);
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
        System.out.println("    [encID]: " + printHexBinary(sharedSecret));
        dbf.insert(printHexBinary(sharedSecret));
        sharedSecret = null;
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
                        // ???????????????????????????????????????ephID???reconstruct
                        // ?????????????????????????????????public key????????????
                        // ?????????????????????public key???hash????????????????????????????????????DH public key??????????????????
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
                // ????????????DH msg??????hash????????????pubKey???hash??????????????????msg????????????????????????
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
