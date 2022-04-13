import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class test {

    public static void main(String[] args) throws Exception {
        // Generate ephemeral ECDH keypair
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC");
        kpg.initialize(128);
        KeyPair kp = kpg.generateKeyPair();

        PublicKey pub = kp.getPublic();
        byte[] pubBytes = pub.getEncoded();
        String pubStr = Base64.getEncoder().encodeToString(pubBytes);
//        byte[] decode = Base64.getDecoder().decode(s);

        PrivateKey pri = kp.getPrivate();
        byte[] priBytes = pri.getEncoded();
        String priStr = Base64.getEncoder().encodeToString(priBytes);
//        byte[] decode = Base64.getDecoder().decode(s);

    }
}