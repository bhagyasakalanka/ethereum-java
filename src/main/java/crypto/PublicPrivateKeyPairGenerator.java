package crypto;

import org.bouncycastle.math.ec.custom.sec.SecP224K1Curve;
import org.bouncycastle.math.ec.rfc8032.Ed25519;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.*;
import java.security.spec.ECGenParameterSpec;

public class PublicPrivateKeyPairGenerator {
    private static PublicPrivateKeyPairGenerator instance;
    private PublicPrivateKeyPairGenerator(){

    }
    public static PublicPrivateKeyPairGenerator getInstance(){
        if(instance == null){
            synchronized (PublicPrivateKeyPairGenerator.class){
                if(instance == null){
                    instance = new PublicPrivateKeyPairGenerator();
                }
            }
        }
        return instance;
    }
    public KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        Key pub = keyPair.getPublic();
        Key pri = keyPair.getPrivate();
        return keyPair;
    }

}
