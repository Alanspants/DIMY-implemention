package helper;

import java.math.BigInteger;

public class SecretShare {
    public SecretShare(final int num, final BigInteger share) {
        this.num = num;
        this.share = share;
    }

    public int getNum() {
        return num;
    }

    public BigInteger getShare() {
        return share;
    }

    @Override
    public String toString() {
        return "SecretShare [num=" + num + ", share=" + share + "]";
    }

    public String broadcastStr() {
        return num + ":" + share;
    }

    public static SecretShare stringToSecretShare(String str) {
        int numValue = Integer.parseInt(str.split(":")[0]);
        BigInteger shareValue = new BigInteger(str.split(":")[1]);
        return new SecretShare(numValue, shareValue);
    }

    public static SecretShare[] createSecretShareArray(String str0, String str1, String str2) {
        SecretShare[] shares = new SecretShare[3];
        shares[0] = stringToSecretShare(str0);
        shares[1] = stringToSecretShare(str1);
        shares[2] = stringToSecretShare(str2);
        return shares;
    }

    private final int num;
    private final BigInteger share;
}
