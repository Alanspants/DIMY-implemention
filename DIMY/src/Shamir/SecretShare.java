package Shamir;

import java.math.BigInteger;

public class SecretShare
{
    public SecretShare(final int number, final BigInteger share)
    {
        this.number = number;
        this.share = share;
    }

    public int getNumber()
    {
        return number;
    }

    public BigInteger getShare()
    {
        return share;
    }

    @Override
    public String toString()
    {
        return "SecretShare [num=" + number + ", share=" + share + "]";
    }

    public String broadcastStr() {
        return number + ":" + share;
    }

    public static SecretShare getSecretShareByStr(String str) {
        int n = Integer.parseInt(str.split(":")[0]);
        BigInteger s = new BigInteger(str.split(":")[1]);
        return new SecretShare(n, s);
    }

    private final int number;
    private final BigInteger share;
}