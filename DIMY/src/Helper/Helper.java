package Helper;

import Shamir.SecretShare;
import Shamir.Shamir;

import java.math.BigInteger;
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

    public static BigInteger sharesRecover(SecretShare[] shares, BigInteger prime) {
        Shamir shamir = new Shamir(3, 5);
        BigInteger result = shamir.combine(shares, prime);
        return result;
    }
}
