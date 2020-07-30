package crypto;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class RSAKeyGenerator {
    private RSAKeyGenerator(){

    }
    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        Key pub = keyPair.getPublic();
        Key pri = keyPair.getPrivate();
        System.out.println("public: "+pub.getFormat());
        System.out.println("private: "+pri.getFormat());
        return keyPair;
    }
}
