package helper;

import java.util.UUID;

public class EphemeralID extends Thread {

    private String id;
    private volatile boolean cancelled;

    public EphemeralID() {
        id = "";
        cancelled = false;
    }

    public String getID() {
        return id;
    }

    private void generator() {
        id = UUID.randomUUID().toString();
        System.out.println("new generator running ...");
    }

    public void cancel() {
        cancelled = true;
    }

    public void run() {
        while (!cancelled) {
            generator();
            try {
                sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
