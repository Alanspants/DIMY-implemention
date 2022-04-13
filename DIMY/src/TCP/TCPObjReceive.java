package TCP;

import com.google.common.hash.BloomFilter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPObjReceive extends Thread{

    public TCPObjReceive () {

    }

    public void run() {
        ServerSocket serverSocket = null;
        try {
            while (true) {
                serverSocket = new ServerSocket(5002);
                serverSocket.setReuseAddress(true);
                Socket socket = serverSocket.accept();
                ObjectOutputStream ObjOS = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream ObjIS = new ObjectInputStream(socket.getInputStream());
                BloomFilter receiveBF = (BloomFilter) ObjIS.readObject();
                System.out.println("\n------\nQBF receive\nQBF:" + receiveBF + "\n------\n");
                serverSocket.close();
                socket.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
