package TCP;

import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class TCPObjReceive extends Thread{

//    BloomFilter<String> QBFs;
    BloomFilter<String> CBFs;

    public TCPObjReceive () {
//        QBFs = BloomFilter.create(Funnels.stringFunnel(Charsets.UTF_8), 1000, 0.001);
        CBFs = BloomFilter.create(Funnels.stringFunnel(Charsets.UTF_8), 100, 0.001);
    }

//    public boolean closeContactCheck () {
//        if (CBFs.)
//    }

    public void run() {
        ServerSocket serverSocket = null;
        try {
            while (true) {
                serverSocket = new ServerSocket(55000);
                serverSocket.setReuseAddress(true);
                Socket socket = serverSocket.accept();

                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                ObjectOutputStream ObjOS = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream ObjIS = new ObjectInputStream(socket.getInputStream());

                String msg = dataInputStream.readUTF();
                if (msg.equals("QBF")) {
                    BloomFilter QBF = (BloomFilter) ObjIS.readObject();
                    System.out.println("\n------\nQBF receive\n[QBF]:" + QBF + "\n------");

                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"));
                    System.out.println("QBF-CBF matching...");
                    System.out.println("    [QBF]: " + QBF);
                    System.out.println("    [CBF]: " + CBFs);
                    if (Helper.Helper.intersectCheck(QBF, CBFs)) {
                        bufferedWriter.write("true");
                    } else {
                        bufferedWriter.write("false");
                    }
                    bufferedWriter.flush();
                    socket.close();
                } else if (msg.equals("CBF")) {
                    BloomFilter receiveBF = (BloomFilter) ObjIS.readObject();
                    System.out.println("\nSomebody is diagnosed positive\n------\nCBF receive\n[CBF]:" + receiveBF + "\n------");
                    CBFs.putAll(receiveBF);

                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"));
                    bufferedWriter.write("true");
                    bufferedWriter.flush();
                    socket.close();
                }
                serverSocket.close();
                socket.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
