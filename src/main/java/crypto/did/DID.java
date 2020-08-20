package crypto.did;

import crypto.PublicPrivateKeyPairGenerator;
import org.bitcoinj.core.Base58;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Arrays;

public class DID {

    private static DID instance;
    private String DIDMethod = "medico";
    private DID() {

    }
    public static DID getInstance(){
        if(instance == null){
            synchronized (DID.class){
                if(instance == null){
                    instance = new DID();
                }
            }
        }
        return instance;
    }

    public String generateDID() throws NoSuchAlgorithmException {
        PublicPrivateKeyPairGenerator publicPrivateKeyPairGenerator = PublicPrivateKeyPairGenerator.getInstance();
        PublicKey publicKey = publicPrivateKeyPairGenerator.generateRSAKeyPair().getPublic();
        MessageDigest sha3 = SHA3.Digest256.getInstance("SHA3-256");
        byte[] pub_hash = sha3.digest(Hex.encode(publicKey.getEncoded()));
        byte[] pub_hash_20bytes = Arrays.copyOfRange(pub_hash, 0, 20);
        String DIDString = Hex.toHexString(pub_hash_20bytes);

        String encodingTypes = "0C01";
        DIDString = encodingTypes + DIDString;

        byte[] extendedHash = Arrays.copyOfRange(sha3.digest(Hex.encode(DIDString.getBytes())),0,4);
        String extendedHashString = Hex.toHexString(extendedHash);
        DIDString += extendedHashString;
        DIDString = "z"+Base58.encode(Hex.decode(DIDString));
        String did = "did:"+DIDMethod+":"+DIDString;

        return did;
    }
}
