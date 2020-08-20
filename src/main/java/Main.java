import ContractorHandlers.DoctorContractorHandler;
import ContractorHandlers.MainContractorHandler;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import crypto.AsymmetricEncDec;
import crypto.KeyHandler;
import crypto.did.DID;
import crypto.PublicPrivateKeyPairGenerator;

import crypto.did.DIDDocument;
import crypto.did.DIDDocumentGenerator;
import javaethereum.contracts.generated.DoctorContract;
import javaethereum.contracts.generated.MainContract;
import org.bitcoinj.core.Base58;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;

import static utilities.EthFunctions.connect;


public class Main {
    private static String DEFAULT_PORT = "http://localhost:8545";
    private static String doctor = "C:/Users/User/Desktop/etheruem_server/data/keystore/UTC--2020-07-19T16-37-27.237405200Z--0993bb88376afbce65514cb4a691b2930a24c499";
    private static String issuer = "C:/Users/User/Desktop/etheruem_server/data/keystore/UTC--2020-07-19T16-37-13.951490200Z--76a26ce5813d2ebc3abfe440e28a8be2d71c4643";
    private static String owner = "C:/Users/User/Desktop/etheruem_server/data/keystore/UTC--2020-07-19T16-36-50.572265900Z--d09b4fad07c80d4e721b1e7c4affb39e08396845";

    private static String walletPass = "123456";

    private static final BigInteger GasPrice =Convert.toWei("20", Convert.Unit.GWEI).toBigInteger();
    private static final BigInteger GasLimit = Convert.toWei("3000000", Convert.Unit.WEI).toBigInteger();

    private static Credentials CREDENTIALS1;
    private static Credentials CREDENTIALS2;


    static {
        try {
            CREDENTIALS1 = WalletUtils.loadCredentials(walletPass, doctor);
            CREDENTIALS2 = WalletUtils.loadCredentials(walletPass, issuer);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (CipherException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws Exception {
//        DIDDocumentGenerator didDocumentGenerator = DIDDocumentGenerator.getInstance();
//        DIDDocument didDocument = didDocumentGenerator.generateDIDDocument(DID.getInstance().generateDID(), "my public key");
//        Gson gson = new Gson();
//        JsonElement json  = gson.toJsonTree(didDocument);
//        String context =  "v1_did";
//        json.getAsJsonObject().addProperty("@context", context);
//        String jsonStr = gson.toJson(json);
//        System.out.println(jsonStr);
//
//        DIDDocument newDidDocument = gson.fromJson(jsonStr, DIDDocument.class);
//        System.out.println(newDidDocument);
//        json  = gson.toJsonTree(newDidDocument);
//        json.getAsJsonObject().addProperty("@context", context);
//        jsonStr = gson.toJson(json);
//        System.out.println(jsonStr);

//        System.out.println(json);
//        FileOutputStream out = new FileOutputStream("./didDocument");
//        out.write(jsonStr.getBytes());
//        out.close();
        System.out.println("Connecting to Ethereum ...");
        Web3j web3 = connect();
        System.out.println("Successfully connected to Ethereum");

//        KeyPair keyPairDoctor = RSAKeyGenerator.generateKeyPair();
//        KeyPair keyPairPatient = RSAKeyGenerator.generateKeyPair();

//        KeyHandler.writePlainKeyPair(keyPairDoctor, "./privateDoctor","./publicDoctor");
//        KeyHandler.writePlainKeyPair(keyPairPatient, "./privatePatient","./publicPatient");
        KeyHandler keyHandler = KeyHandler.getInstance();
        PrivateKey privateKeyDoctor = keyHandler.loadRSAPrivateFromPlainText("./privateDoctorpt.key");
        PublicKey publicKeyDoctor = keyHandler.loadRSAPublicFromPlainText("./publicDoctorpb.key");
        PrivateKey privateKeyPatient = keyHandler.loadRSAPrivateFromPlainText("./privatePatientpt.key");
        PublicKey publicKeyPatient = keyHandler.loadRSAPublicFromPlainText("./publicPatientpb.key");


        //generateECKeyPair(new KeyPair(publicKeyDoctor, privateKeyDoctor));
        //deployMain(web3);
//        deployDoctorContractor(web3);
        MainContractorHandler mainContractorHandler = MainContractorHandler.getInstance();
        MainContract mainContractDoctor = mainContractorHandler.getWrapperForMainContractor(web3, "0xc7406431a6f68968d1846b5a6a2728ea8f0841b2",CREDENTIALS1);
        MainContract mainContractPatient = mainContractorHandler.getWrapperForMainContractor(web3, "0xc7406431a6f68968d1846b5a6a2728ea8f0841b2",CREDENTIALS2);

        DoctorContractorHandler doctorContractorHandler = DoctorContractorHandler.getInstance();
        //doctorContractorHandler.deployDoctorContractor(web3);
        DoctorContract doctorContractDoctor = doctorContractorHandler.getWrapperForDoctorContractor(web3, "0xf037ab499dbfc4e00aa717d06afd92fc94816aea",CREDENTIALS1);
        DoctorContract doctorContractPatient = doctorContractorHandler.getWrapperForDoctorContractor(web3, "0xf037ab499dbfc4e00aa717d06afd92fc94816aea",CREDENTIALS2);

        //BigInteger i = doctorContractDoctor.getLatestRecordNumber().send();
        //addIssuer(mainContract,web3,"0x76A26Ce5813d2Ebc3abfE440E28A8be2d71c4643");
        //getDoctorDetails(mainContractDoctor,"0x0993bb88376afBcE65514cb4a691b2930A24c499");
        AsymmetricEncDec asymmetricEncDec = AsymmetricEncDec.getInstance();
        String challenge = asymmetricEncDec.encryptString("hello world" ,publicKeyDoctor);
        BigInteger timeStamp = BigInteger.valueOf((new Date()).getTime());
        System.out.println("start");
        BigInteger i = doctorContractorHandler.createRecord(doctorContractPatient,challenge, web3, CREDENTIALS2);
        System.out.println("record created");
        String encryptedChallenge = doctorContractorHandler.readChallenge(doctorContractDoctor,i, web3, CREDENTIALS1);
        if(encryptedChallenge.equals("")) throw new RuntimeException("no permission for read challenge");
        System.out.println("challenge read");
        String decryptedChallenge = asymmetricEncDec.decryptString(encryptedChallenge, privateKeyDoctor);
        String response = asymmetricEncDec.encryptString(decryptedChallenge,publicKeyPatient);
        boolean res = doctorContractorHandler.sendResponse(doctorContractDoctor, response, i, web3, CREDENTIALS1);
        if(!res) throw new RuntimeException("no permission for send response");
        System.out.println("response sent");

        String encryptedResponse = doctorContractorHandler.readResponse(doctorContractPatient, i, web3, CREDENTIALS2);
        if(encryptedResponse.equals("")) throw new RuntimeException("no permission to read response");

        String decryptedResponse = asymmetricEncDec.decryptString(encryptedResponse, privateKeyPatient);
        if(decryptedResponse.equals("hello world")){
            System.out.println("true");
        }else{
            System.out.println(false);
            System.out.println(decryptedResponse+"___"+"hello world");
        }
        System.out.println(i);
        System.out.println(decryptedResponse);
        boolean isRecordCloseByDoctor = doctorContractorHandler.closeRecordByDoctor(doctorContractDoctor, i, web3, CREDENTIALS1);
        if(isRecordCloseByDoctor){
            System.out.println("ok close by doctor");
        }else {
            System.out.println("record close failed");
        }

        //registerDoctor(mainContract,web3,"www.profile.com","profile hash");
        //issueClaim(mainContract, web3, "www.claim.com","my claim","0xD09B4FAd07C80d4E721b1e7c4aFfb39e08396845");

        //getDoctorDetails(mainContract);

    }

    private static void sendTransaction(Web3j web3, String to, int amount) throws IOException, CipherException, InterruptedException {
        String walletPass = "123456";
        String walletPath = "C:/Users/User/Desktop/etheruem_server/data/keystore/UTC--2020-07-09T16-30-26.864791800Z--2dd2c7506993b78d70b26e653ac9875a42eacd32";

        try {
            // Decrypt and open the wallet into a Credential object
            Credentials credentials = WalletUtils.loadCredentials(walletPass, walletPath);
            System.out.println("Account address: " + credentials.getAddress());
            System.out.println("Balance: " + Convert.fromWei(web3.ethGetBalance(credentials.getAddress(), DefaultBlockParameterName.LATEST).send().getBalance().toString(), Convert.Unit.ETHER));

            // Get the latest nonce
            EthGetTransactionCount ethGetTransactionCount = web3.ethGetTransactionCount(credentials.getAddress(), DefaultBlockParameterName.LATEST).send();
            BigInteger nonce =  ethGetTransactionCount.getTransactionCount();
            System.out.println("nonce: "+ nonce);
            System.out.println(to);
            // Value to transfer (in wei)
            BigInteger value = Convert.toWei("1000000", Convert.Unit.WEI).toBigInteger();
            System.out.println("send value: "+ value);
            // Gas Parameters
            BigInteger gasLimit = BigInteger.valueOf(21000);
            System.out.println("gasLimit: "+gasLimit);

            BigInteger gasPrice = Convert.toWei("10", Convert.Unit.WEI).toBigInteger();
            System.out.println("gasPrice: "+gasPrice);
            // Prepare the rawTransaction
            RawTransaction rawTransaction  = RawTransaction.createEtherTransaction(
                    nonce,
                    gasPrice,
                    gasLimit,
                    to,
                    value);

            // Sign the transaction
            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
            String hexValue = Numeric.toHexString(signedMessage);
            System.out.println("hexValue: "+hexValue);
            // Send transaction
            EthSendTransaction ethSendTransaction = web3.ethSendRawTransaction(hexValue).send();
            String transactionHash = ethSendTransaction.getTransactionHash();
            System.out.println("transactionHash: " + transactionHash);

            // Wait for transaction to be mined
            Optional<TransactionReceipt> transactionReceipt = null;
            do {
                System.out.println("checking if transaction " + transactionHash + " is mined....");
                EthGetTransactionReceipt ethGetTransactionReceiptResp = web3.ethGetTransactionReceipt(transactionHash).send();
                transactionReceipt = ethGetTransactionReceiptResp.getTransactionReceipt();
                Thread.sleep(3000); // Wait 3 sec
            } while(!transactionReceipt.isPresent());

            System.out.println("Transaction " + transactionHash + " was mined in block # " + transactionReceipt.get().getBlockNumber());
            System.out.println("Balance: " + Convert.fromWei(web3.ethGetBalance(credentials.getAddress(), DefaultBlockParameterName.LATEST).send().getBalance().toString(), Convert.Unit.ETHER));


        } catch (IOException | InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }



}
