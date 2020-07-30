package crypto;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.util.Base64;

public class AsymmetricEncDec {
    private AsymmetricEncDec() {

    }
    public static String encryptString(String text, Key key) throws InvalidKeyException, UnsupportedEncodingException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException {
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(128);
        SecretKey secretKey = generator.generateKey();
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.ENCRYPT_MODE, secretKey);
        String cipherText = Base64.getEncoder().encodeToString(aesCipher.doFinal(text.getBytes("UTF-8")));
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.PUBLIC_KEY, key);
        String cipherSecret =  Base64.getEncoder().encodeToString(cipher.doFinal(secretKey.getEncoded()));
        System.out.println("encrypted: "+cipherSecret+"_"+cipherText);
        return cipherSecret+"_"+cipherText;
    }
    public static String decryptString(String cipherText, Key key) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException {
        String[] msg = cipherText.split("_");
        System.out.println("cipherText: "+cipherText);
        String cipherT = msg[1];
        String cipherSecret = msg[0];

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.PRIVATE_KEY, key);
        byte[] secretKey = cipher.doFinal(Base64.getDecoder().decode(cipherSecret));
        SecretKey originalKey = new SecretKeySpec(secretKey, 0, secretKey.length,"AES");
        Cipher cipherSec = Cipher.getInstance("AES");

        cipherSec.init(Cipher.DECRYPT_MODE, originalKey);
        String response = new String(cipherSec.doFinal(Base64.getDecoder().decode(cipherT)));

        return response;
    }
}
