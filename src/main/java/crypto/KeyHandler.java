package crypto;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public  class KeyHandler {
    private static KeyHandler instance;
    private KeyHandler(){

    }
    public static KeyHandler getInstance(){
        if(instance == null){
            synchronized (KeyHandler.class){
                if(instance == null){
                    instance = new KeyHandler();
                }
            }
        }
        return instance;
    }
    public void EncodedKeyPairWriter(KeyPair keyPair, String privateKeyLocation, String publicKeyLocation) throws IOException {
        FileOutputStream pri_out = new FileOutputStream(privateKeyLocation+".key");
        pri_out.write(keyPair.getPrivate().getEncoded());
        pri_out.close();
        FileOutputStream pub_out = new FileOutputStream(publicKeyLocation+".key");
        pub_out.write(keyPair.getPublic().getEncoded());
        pub_out.close();
    }
    public PublicKey loadEncodedRSAPublicKey(String publicKeyLocation) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        Path path = Paths.get(publicKeyLocation);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        byte[] bytes = Files.readAllBytes(path);
        X509EncodedKeySpec ks = new X509EncodedKeySpec(bytes);
        PublicKey publicKey = kf.generatePublic(ks);
        return publicKey;
    }
    public PrivateKey loadEncodedRSAPrivateKey(String privateKeyLocation) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        //private
        Path path = Paths.get(privateKeyLocation);
        byte[] bytes = Files.readAllBytes(path);
        PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(bytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = kf.generatePrivate(ks);
        return  privateKey;
    }

    public void writePlainKeyPair(KeyPair keyPair, String privateKeyLocation, String publicKeyLocation) throws IOException {
        Base64.Encoder encoder = Base64.getEncoder();
        Writer out = new FileWriter(privateKeyLocation+"pt.key");
        out.write("-----BEGIN RSA PRIVATE KEY-----\n");
        out.write(encoder.encodeToString(keyPair.getPrivate().getEncoded()));
        out.write("\n------END RSA PRIVATE KEY-----\n");
        out.close();

        out = new FileWriter(publicKeyLocation+"pb.key");
        out.write("-----BEGIN RSA PUBLIC KEY-----\n");
        out.write(encoder.encodeToString(keyPair.getPublic().getEncoded()));
        out.write("\n------END RSA PUBLIC KEY-----\n");
        out.close();
    }
    public PublicKey loadRSAPublicFromPlainText(String publicKeyLocation) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        File file = new File(publicKeyLocation);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String publicKeyText = "";
        String st;
        while((st = br.readLine()) != null){
            publicKeyText += st;
        }
        int s_index = publicKeyText.indexOf("-----BEGIN RSA PUBLIC KEY-----");
        int e_index = publicKeyText.indexOf("------END RSA PUBLIC KEY-----");
        publicKeyText = publicKeyText.substring(s_index+ 30, e_index);
        byte[] bytesArr = Base64.getDecoder().decode(publicKeyText);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(bytesArr);
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
        return publicKey;
    }
    public PrivateKey loadRSAPrivateFromPlainText(String privateKeyLocation) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        File file = new File(privateKeyLocation);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String privateKeyText = "";
        String st;
        while((st = br.readLine()) != null){
            privateKeyText += st;
        }

        int s_index = privateKeyText.indexOf("-----BEGIN RSA PRIVATE KEY-----");
        int e_index = privateKeyText.indexOf("------END RSA PRIVATE KEY-----");

        privateKeyText = privateKeyText.substring(s_index+ 31, e_index);
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] bytesArr = decoder.decode(privateKeyText);
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(bytesArr);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        return privateKey;
    }

}
