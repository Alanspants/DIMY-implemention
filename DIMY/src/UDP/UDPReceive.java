package UDP;

import Helper.Helper;
import Shamir.SecretShare;
import Shamir.Shamir;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;

import static Shamir.SecretShare.getSecretShareByStr;

public class UDPReceive extends Thread {
    HashMap<String, String[]> shares = new HashMap<>();
    int port;

    public UDPReceive(int port) {
        this.port = port;
    }

    public void verify(String content) {
        String bigIdHash = content.split(" ")[0];
        String prime = content.split(" ")[1];
        String share = content.split(" ")[2];

        if (!shares.containsKey(bigIdHash)){
            String[] shareArray = new String[]{share, "", ""};
            shares.put(bigIdHash, shareArray);
        } else {
            String[] shareArray = shares.get(bigIdHash);
            int i = 0;
            for(i = 0; i < 3; i++) {
                if (shareArray[i].equals("")) {
                    shareArray[i] = share;
                    break;
                }
            }
            shares.put(bigIdHash, shareArray);
            if (i >= 2) {
//                SecretShare[] sharesRCV = SecretShare.createSecretShareArray(shareArray[0], shareArray[1], shareArray[2]);
                SecretShare[] secretShare = {
                        getSecretShareByStr(shareArray[0]),
                        getSecretShareByStr(shareArray[1]),
                        getSecretShareByStr(shareArray[2]),
                };
//                BigInteger bigIdRecover = Helper.sharesRecover(sharesRCV, new BigInteger(prime));
                BigInteger recoverResult = Shamir.combine(secretShare, new BigInteger(prime));
                System.out.println("----------------------");
                System.out.println("recovering...");
                System.out.println("    [bigIdHash]: " + bigIdHash);
                System.out.println("    [bigIdRecover]: " + recoverResult.hashCode());
                if (Integer.parseInt(bigIdHash) == recoverResult.hashCode()) {
                    System.out.println("    [recover result]: YES!!");
                } else {
                    System.out.println("    [recover result]: NO!!");
                }
                System.out.println("----------------------");
            }
        }
    }

    public void run() {
        DatagramSocket ds = null;
        try {
            ds = new DatagramSocket(this.port);
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
            System.out.println("content: " + content);
            verify(content);
        }
    }

    public static void main(String[] args) {
        UDPReceive UDPRcv = new UDPReceive(5001);
        UDPRcv.start();
    }

}
