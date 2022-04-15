package UDP;

import java.io.IOException;
import java.net.*;

public class UDPBroadcast {
    private static DatagramSocket socket;

    public static void broadcast (String msg, InetAddress address) throws IOException {
        socket = new DatagramSocket();
        socket.setReuseAddress(true);
        socket.setBroadcast(true);

        byte[] buffer = msg.getBytes();

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 55001);
        socket.send(packet);
        socket.close();
    }
}
