package UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;

public class UDPReceive extends Thread {
    HashMap<String, String[]> shares = new HashMap<>();
    int port;

    public UDPReceive(int port) {
        this.port = port;
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

            String[] contents = content.toString().split(" ");
            String ephIDHash = contents[0];
            String share = contents[1];

            System.out.println("ephIDHash: " + ephIDHash);
            System.out.println("share: " + share);
            System.out.println("------------------------");
        }
    }

    public static void main(String[] args) {
        UDPReceive UDPRcv = new UDPReceive(5001);
        UDPRcv.start();
    }

}
