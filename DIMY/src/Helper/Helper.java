package Helper;

import java.util.Random;

public class Helper {

    public static boolean msgDrop() {
        Random rand = new Random();
        int value = rand.nextInt(10);
        if (value < 5) {
            return true;
        } else {
            return false;
        }
    }

}
