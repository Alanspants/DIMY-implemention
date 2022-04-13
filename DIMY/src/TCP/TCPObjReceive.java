package TCP;

import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPObjReceive extends Thread{

    BloomFilter<String> QBFs;

    public TCPObjReceive () {
        QBFs = BloomFilter.create(Funnels.stringFunnel(Charsets.UTF_8), 1000, 0.001);
    }

    public void run() {
        ServerSocket serverSocket = null;
        try {
            while (true) {
                serverSocket = new ServerSocket(5002);
                serverSocket.setReuseAddress(true);
                Socket socket = serverSocket.accept();

                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                String msg = dataInputStream.readUTF();
                if (msg.equals("QBF")) {
                    ObjectOutputStream ObjOS = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream ObjIS = new ObjectInputStream(socket.getInputStream());
                    BloomFilter receiveBF = (BloomFilter) ObjIS.readObject();
                    System.out.println("\n------\nQBF receive\nQBF:" + receiveBF + "\n------\n");
                    QBFs.putAll(receiveBF);
                }
                serverSocket.close();
                socket.close();

            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
