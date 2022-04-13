import TCP.TCPObjReceive;

public class DimyServer {
    public static void main(String[] args) {
        TCPObjReceive tcpObjReceive = new TCPObjReceive();
        tcpObjReceive.start();
    }
}
