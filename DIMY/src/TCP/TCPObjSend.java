package TCP;

import com.google.common.hash.BloomFilter;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class TCPObjSend {
    public static void sendQBF(BloomFilter qbf) throws IOException {
        Socket socket = new Socket("localhost", 5002);
        socket.setReuseAddress(true);

        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

        dataOutputStream.writeUTF("QBF");
        dataOutputStream.flush();

        objectOutputStream.writeObject(qbf);
        objectOutputStream.flush();

        System.out.println("\n------\nQBF send\nQBF:" + qbf + "\n------\n");

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(dataInputStream));
        String msg = bufferedReader.readLine();
//        System.out.println(msg);
        if (msg.equals("true")) {
            System.out.println("You are at risk of being in close contact with a patient");
        } else {
            System.out.println("You are safe");
        }

        socket.close();
    }

    public static void sendCBF(BloomFilter cbf) throws IOException {
        Socket socket = new Socket("localhost", 5002);
        socket.setReuseAddress(true);

        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

        dataOutputStream.writeUTF("CBF");
        dataOutputStream.flush();

        objectOutputStream.writeObject(cbf);
        objectOutputStream.flush();

        System.out.println("------\nCBF send\nCBF:" + cbf + "\n------");

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(dataInputStream));
        String msg = bufferedReader.readLine();
//        System.out.println(msg);
        if (msg.equals("true")) {
            System.out.println("Upload CBF successfully.");
        }

        socket.close();

    }
}
