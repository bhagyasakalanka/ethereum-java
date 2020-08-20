package utilities;

import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class EthFunctions {
    private static final String DEFAULT_PORT = "http://localhost:8545";
    public static Web3j connect(){
        return Web3j.build(new HttpService(DEFAULT_PORT));
    }

    public static void generateNewAccount1(final String walletPassword, final String walletDirectory, final String walletName) throws IOException, CipherException {
        //load the json encrypted wallet
        Credentials credentials = WalletUtils.loadCredentials(walletPassword, walletDirectory+"/"+walletName);

        String accountAddress = credentials.getAddress();

        String privateKey = credentials.getEcKeyPair().getPrivateKey().toString(16);
        String publicKey = credentials.getEcKeyPair().getPublicKey().toString(16);
        System.out.println("privateKey:"+privateKey);
        System.out.println("publicKey:"+ publicKey);
        System.out.println("address: "+accountAddress);
    }

    public static void generateNewAccount2(final String password, final String mnemonic){
        //String mnemonic = "candy maple cake suger pudding cream honey rich smooth crumble sweet treat";
        Credentials credentials = WalletUtils.loadBip39Credentials(password, mnemonic);
        String privateKey = credentials.getEcKeyPair().getPrivateKey().toString(16);
        String publicKey = credentials.getEcKeyPair().getPublicKey().toString(16);
        String walletAddresss = credentials.getAddress();
        System.out.println("privateKey:"+privateKey);
        System.out.println("publicKey:"+ publicKey);
        System.out.println("wallet Address: "+ walletAddresss);
    }

    public static void generateNewAccount3(final String password, final String mnemonic){
        //String mnemonic = "candy maple cake suger pudding cream honey rich smooth crumble sweet treat";
        //Derivation path wanted: //m/44'60'/0'/0
        int[] derivationPath = {44 | Bip32ECKeyPair.HARDENED_BIT, 60 | Bip32ECKeyPair.HARDENED_BIT,0, Bip32ECKeyPair.HARDENED_BIT,0 ,0};

        //Generate a BIP32 master keypair from the mnemonic phrase
        Bip32ECKeyPair masterKeyPair = Bip32ECKeyPair.generateKeyPair(MnemonicUtils.generateSeed(mnemonic, password));

        //Derived the key using the derivation path
        Bip32ECKeyPair derivedKeyPair = Bip32ECKeyPair.deriveKeyPair(masterKeyPair, derivationPath);

        //Load the wallet for the derived key
        Credentials credentials = Credentials.create(derivedKeyPair);

        String privateKey = credentials.getEcKeyPair().getPrivateKey().toString(16);
        String publicKey = credentials.getEcKeyPair().getPublicKey().toString(16);
        String walletAddress = credentials.getAddress();
        System.out.println("privateKey:"+privateKey);
        System.out.println("publicKey:"+ publicKey);
        System.out.println("Wallet Address: "+ walletAddress);
    }

    public static void createWallet(final String walletPassword, final String walletDirectory)
            throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException,
            CipherException, IOException {
        String walletName  = WalletUtils.generateNewWalletFile(walletPassword,new File(walletDirectory));
        System.out.println("wallet location: "+walletDirectory+ "/"+ walletName);

        Credentials credentials  = WalletUtils.loadCredentials(walletPassword, walletDirectory+"/"+walletName);
        String privateKey = credentials.getEcKeyPair().getPrivateKey().toString(16);
        String publicKey = credentials.getEcKeyPair().getPublicKey().toString(16);
        String walletAddress = credentials.getAddress();
        System.out.println("privateKey:"+privateKey);
        System.out.println("publicKey:"+ publicKey);
        System.out.println("Wallet Address: "+ walletAddress);
    }

}
