package TCP;

import com.google.common.hash.BloomFilter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPObjSend {
    public static void sendQBF(BloomFilter qbf) throws IOException {
        Socket socket = new Socket("localhost", 5002);
        socket.setReuseAddress(true);
        ObjectOutputStream ObjOS = new ObjectOutputStream(socket.getOutputStream());
        ObjOS.writeObject(qbf);
        System.out.println("\n------\nQBF send\nQBF:" + qbf + "\n------\n");
        socket.close();
    }
}
