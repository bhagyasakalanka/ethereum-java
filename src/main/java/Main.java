import crypto.AsymmetricEncDec;
import crypto.KeyHandler;
import crypto.RSAKeyGenerator;
import javaethereum.contracts.generated.DoctorContract;
import javaethereum.contracts.generated.DocumentRegistry;
import javaethereum.contracts.generated.MainContract;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;

import org.web3j.abi.datatypes.generated.AbiTypes;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tx.gas.StaticGasProvider;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.swing.plaf.metal.MetalTheme;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.*;
import java.util.*;


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
        System.out.println("Connecting to Ethereum ...");
        Web3j web3 = connect();
        System.out.println("Successfully connected to Ethereum");

//        KeyPair keyPairDoctor = RSAKeyGenerator.generateKeyPair();
//        KeyPair keyPairPatient = RSAKeyGenerator.generateKeyPair();

//        KeyHandler.writePlainKeyPair(keyPairDoctor, "./privateDoctor","./publicDoctor");
//        KeyHandler.writePlainKeyPair(keyPairPatient, "./privatePatient","./publicPatient");

        PrivateKey privateKeyDoctor = KeyHandler.loadRSAPrivateFromPlainText("./privateDoctorpt.key");
        PublicKey publicKeyDoctor = KeyHandler.loadRSAPublicFromPlainText("./publicDoctorpb.key");
        PrivateKey privateKeyPatient = KeyHandler.loadRSAPrivateFromPlainText("./privatePatientpt.key");
        PublicKey publicKeyPatient = KeyHandler.loadRSAPublicFromPlainText("./publicPatientpb.key");


        //generateECKeyPair(new KeyPair(publicKeyDoctor, privateKeyDoctor));
        //deployMain(web3);
        //deployDoctorContractor(web3);
        MainContract mainContractDoctor = getWrapperForMainContractor(web3, "0xc7406431a6f68968d1846b5a6a2728ea8f0841b2",CREDENTIALS1);
        MainContract mainContractPatient = getWrapperForMainContractor(web3, "0xc7406431a6f68968d1846b5a6a2728ea8f0841b2",CREDENTIALS2);

        DoctorContract doctorContractDoctor = getWrapperForDoctorContractor(web3, "0xfbe324406eef963ed4eb5b9d8c79fb3a60ebf464",CREDENTIALS1);
        DoctorContract doctorContractPatient = getWrapperForDoctorContractor(web3, "0xfbe324406eef963ed4eb5b9d8c79fb3a60ebf464",CREDENTIALS2);

        //BigInteger i = doctorContractDoctor.getLatestRecordNumber().send();
        //addIssuer(mainContract,web3,"0x76A26Ce5813d2Ebc3abfE440E28A8be2d71c4643");
        //getDoctorDetails(mainContractDoctor,"0x0993bb88376afBcE65514cb4a691b2930A24c499");
        String challenge = AsymmetricEncDec.encryptString("hi there bro" ,publicKeyDoctor);
        BigInteger timeStamp = BigInteger.valueOf((new Date()).getTime());
        BigInteger i = createRecord(doctorContractPatient,challenge, web3, CREDENTIALS2);
        String encryptedChallenge = readChallenge(doctorContractDoctor,i, web3, CREDENTIALS1);
        if(encryptedChallenge.equals("")) throw new RuntimeException("no permission for read challenge");

        String decryptedChallenge = AsymmetricEncDec.decryptString(encryptedChallenge, privateKeyDoctor);
        String response = AsymmetricEncDec.encryptString(decryptedChallenge,publicKeyPatient);
        boolean res = sendResponse(doctorContractDoctor, response, i, web3, CREDENTIALS1);
        if(!res) throw new RuntimeException("no permission for send response");

        String encryptedResponse = readResponse(doctorContractPatient, i, web3, CREDENTIALS2);
        if(encryptedResponse.equals("")) throw new RuntimeException("no permission to read response");

        String decryptedResponse = AsymmetricEncDec.decryptString(encryptedResponse, privateKeyPatient);
        if(decryptedResponse.equals("hi there bro")){
            System.out.println("true");
        }else{
            System.out.println(false);
            System.out.println(decryptedResponse+"___"+"hi there bro");
        }
        System.out.println(i);
        System.out.println(decryptedResponse);
        boolean isRecordCloseByDoctor = closeRecordByDoctor(doctorContractDoctor, i, web3, CREDENTIALS1);
        if(isRecordCloseByDoctor){
            System.out.println("ok close by doctor");
        }else {
            System.out.println("record close failed");
        }

        //registerDoctor(mainContract,web3,"www.profile.com","profile hash");
        //issueClaim(mainContract, web3, "www.claim.com","my claim","0xD09B4FAd07C80d4E721b1e7c4aFfb39e08396845");

        //getDoctorDetails(mainContract);

    }

    private static void test(Web3j web3){
        try {
            // web3_clientVersion returns the current client version.
            Web3ClientVersion clientVersion = web3.web3ClientVersion().send();

            // eth_blockNumber returns the number of most recent block.
            EthBlockNumber blockNumber = web3.ethBlockNumber().send();
            EthAccounts ethAccounts = web3.ethAccounts().send();

            // eth_gasPrice, returns the current price per gas in wei.
            EthGasPrice gasPrice = web3.ethGasPrice().send();
            EthGetBalance balance = web3.ethGetBalance(ethAccounts.getAccounts().get(0), DefaultBlockParameterName.LATEST).send();
            BigDecimal balanceInEther = Convert.fromWei(balance.getBalance().toString(), Convert.Unit.GWEI);
            System.out.println("balance in ether: " + balanceInEther);
            // Print result
            System.out.println("Client version: " + clientVersion.getWeb3ClientVersion());
            System.out.println("Block number: " + blockNumber.getBlockNumber());
            System.out.println("Gas price: " + Convert.fromWei(gasPrice.getGasPrice().toString(),Convert.Unit.GWEI));

        } catch (IOException ex) {
            throw new RuntimeException("Error whilst sending json-rpc requests", ex);
        }
    }

    private static Web3j connect(){
        return Web3j.build(new HttpService(DEFAULT_PORT));
    }

    private static void generateNewAccount1() throws IOException, CipherException {
        String walletPassword = "123456";
        String walletDirectory = "./wallet";
        String walletName = "UTC--2019-06-20T08-55-56.200000000Z--fd7d68e16ef61868f3e325fafdf2fc1ec0b77649.json";

        //load the json encrypted wallet
        Credentials credentials = WalletUtils.loadCredentials(walletPassword, walletDirectory+"/"+walletName);

        //get the account address
        String accountAddress = credentials.getAddress();

        String privateKey = credentials.getEcKeyPair().getPrivateKey().toString(16);
        String publicKey = credentials.getEcKeyPair().getPublicKey().toString(16);
        System.out.println("privateKey:"+privateKey);
        System.out.println("publicKey:"+ publicKey);
    }

    private static void generateNewAccount2(){
        String password = null;
        String mnemonic = "candy maple cake suger pudding cream honey rich smooth crumble sweet treat";

        Credentials credentials = WalletUtils.loadBip39Credentials(password, mnemonic);
        String privateKey = credentials.getEcKeyPair().getPrivateKey().toString(16);
        String publicKey = credentials.getEcKeyPair().getPublicKey().toString(16);
        System.out.println("privateKey:"+privateKey);
        System.out.println("publicKey:"+ publicKey);
    }

    private static void generateNewAccount3(){
        String password = null;
        String mnemonic = "candy maple cake suger pudding cream honey rich smooth crumble sweet treat";

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
        System.out.println("privateKey:"+privateKey);
        System.out.println("publicKey:"+ publicKey);
    }

    private static void createWallet() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, CipherException, IOException {
        String walletPassword = "123456";
        String walletDirectory = "./wallet/";
        String walletName  = WalletUtils.generateNewWalletFile(walletPassword,new File(walletDirectory));
        System.out.println("wallet location: "+walletDirectory+ "/"+ walletName);

        Credentials credentials  = WalletUtils.loadCredentials(walletPassword, walletDirectory+"/"+walletName);
        String privateKey = credentials.getEcKeyPair().getPrivateKey().toString(16);
        String publicKey = credentials.getEcKeyPair().getPublicKey().toString(16);
        System.out.println("privateKey:"+privateKey);
        System.out.println("publicKey:"+ publicKey);
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
    private static void deploy(Web3j web3) throws Exception {
        String walletPass = "123456";
        String walletPath = "C:/Users/User/Desktop/etheruem_server/data/keystore/UTC--2020-07-09T16-30-26.864791800Z--2dd2c7506993b78d70b26e653ac9875a42eacd32";

        Credentials credentials = WalletUtils.loadCredentials(walletPass, walletPath);

        BigInteger GasPrice =Convert.toWei("5", Convert.Unit.GWEI).toBigInteger();
        BigInteger GasLimit = Convert.toWei("2000000", Convert.Unit.WEI).toBigInteger();


        DocumentRegistry registry = DocumentRegistry.deploy(web3, credentials, new StaticGasProvider(GasPrice, GasLimit)).send();
        String contractAddress = registry.getContractAddress();

        System.out.println(contractAddress);
    }

    private static void deployMain(Web3j web3) throws Exception {
        String walletPass = "123456";
        String walletPath = "C:/Users/User/Desktop/etheruem_server/data/keystore/UTC--2020-07-19T16-36-50.572265900Z--d09b4fad07c80d4e721b1e7c4affb39e08396845";

        Credentials credentials = WalletUtils.loadCredentials(walletPass, walletPath);

        BigInteger GasPrice =Convert.toWei("3", Convert.Unit.GWEI).toBigInteger();
        BigInteger GasLimit = Convert.toWei("1800000", Convert.Unit.WEI).toBigInteger();


        MainContract mainContract = MainContract.deploy(web3, credentials, new StaticGasProvider(GasPrice, GasLimit)).send();
        String contractAddress = mainContract.getContractAddress();

        System.out.println(contractAddress);
    }

    private static void deployDoctorContractor(Web3j web3) throws Exception {
        String walletPass = "123456";

        Credentials credentials = WalletUtils.loadCredentials(walletPass, doctor);

        BigInteger GasPrice =Convert.toWei("3", Convert.Unit.GWEI).toBigInteger();
        BigInteger GasLimit = Convert.toWei("1800000", Convert.Unit.WEI).toBigInteger();


        DoctorContract doctorContract = DoctorContract.deploy(web3, credentials, new StaticGasProvider(GasPrice, GasLimit)).send();
        String contractAddress = doctorContract.getContractAddress();

        System.out.println(contractAddress);
    }

    private static DocumentRegistry getWrapperForContractor(Web3j web3, String contractorAddress) throws IOException, CipherException {
        String walletPass = "123456";
        String walletPath = "C:/Users/User/Desktop/etheruem_server/data/keystore/UTC--2020-07-09T16-30-44.733634900Z--820f4b83964a3f8d353b48fae6ac3a1e71983453";
        BigInteger GasPrice =Convert.toWei("20", Convert.Unit.GWEI).toBigInteger();
        BigInteger GasLimit = Convert.toWei("3000000", Convert.Unit.WEI).toBigInteger();

        Credentials credentials = WalletUtils.loadCredentials(walletPass, walletPath);
        DocumentRegistry documentRegistry = DocumentRegistry.load(contractorAddress,web3, credentials, new StaticGasProvider(GasPrice, GasLimit));
        return documentRegistry;
    }

    private static MainContract getWrapperForMainContractor(Web3j web3, String contractorAddress, Credentials CREDENTIALS) throws IOException, CipherException {

        MainContract mainContract = MainContract.load(contractorAddress,web3, CREDENTIALS, new StaticGasProvider(GasPrice, GasLimit));
        return mainContract;
    }
    private static DoctorContract getWrapperForDoctorContractor(Web3j web3, String contractorAddress, Credentials CREDENTIALS) throws IOException, CipherException {

        DoctorContract doctorContract = DoctorContract.load(contractorAddress,web3, CREDENTIALS, new StaticGasProvider(GasPrice, GasLimit));
        return doctorContract;
    }


    private static void registerDoctor(MainContract mainContract, Web3j web3j, String profileDataLink, String profileHash, Credentials CREDENTIALS) throws Exception {

        try {
            TransactionReceipt receipt = mainContract.registerDoctor(profileDataLink, profileHash).send();
            String a = mainContract.getRegisterDoctorEventEvents(receipt).get(0).hash;
            String b = mainContract.getUpdateClaimDetailsEvents(receipt).get(0).link;
            String c = mainContract.getUpdateClaimDetailsEvents(receipt).get(0).doctor;
            System.out.println(a + " " + b + " " + c);
            String txHash = receipt.getTransactionHash();
            System.out.println(txHash);
        }catch (Exception e){
            final  Function _function = new Function(
                    "registerDoctor",
                    Arrays.asList(new Utf8String(profileDataLink), new Utf8String(profileHash)),
                    Arrays.asList(new TypeReference<Utf8String>() {}));

            String encodedFunction = FunctionEncoder.encode(_function);
            EthCall ethCall = web3j.ethCall(
                    Transaction.createEthCallTransaction(
                            CREDENTIALS.getAddress(),
                            mainContract.getContractAddress(),
                            encodedFunction),
                    DefaultBlockParameterName.LATEST
            ).send();

            Optional<String> revertReason = getRevertReason(ethCall);
            System.out.println(revertReason.get());
        }
    }

    private static void addIssuer(MainContract mainContract, Web3j web3j, String issuerAddress, Credentials CREDENTIALS) throws IOException {
        try {
            TransactionReceipt receipt = mainContract.addClaimIssuers(issuerAddress).send();
            String a = mainContract.getAddIssuerEventEvents(receipt).get(0).newIssuer;

            System.out.println("new issuer: "+a);
            String txHash = receipt.getTransactionHash();
            System.out.println(txHash);
        } catch (Exception e){
            final Function _function = new Function(
                    "addClaimIssuers",
                    Arrays.asList(new Address(issuerAddress)),  // this must match all the method input types you have in your function, make it an empty list otherwise
                    Arrays.asList(new TypeReference<Utf8String>() {})); //this must match your function return types. For example if your function returns a uint8 use: `Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));`

            String encodedFunction = FunctionEncoder.encode(_function);
            EthCall ethCall = web3j.ethCall(
                        Transaction.createEthCallTransaction(
                                CREDENTIALS.getAddress(), // this is your wallet's address. Use `credentials.getAddress();` if you do not know what yours is
                                mainContract.getContractAddress(), // this should be the same as what is in the load function above
                                encodedFunction
                        ),
                        DefaultBlockParameterName.LATEST
                ).send();


            Optional<String> revertReason = getRevertReason(ethCall); // this is the same function from the blog post mentioned
            System.out.println(revertReason.get()); // outputs: 'Already voted.'
        }


    }
    public static Optional<String> getRevertReason(EthCall ethCall) {
        String errorMethodId = Numeric.toHexString(Hash.sha3("Error(string)".getBytes())).substring(0, 10);
        System.out.println(errorMethodId);
        List<TypeReference<Type>> revertReasonTypes = Collections.singletonList(TypeReference.create((Class<Type>) AbiTypes.getType("string")));

        if (!ethCall.hasError() && ethCall.getValue() != null && ethCall.getValue().startsWith(errorMethodId)) {
            System.out.println("hrereer");
            String encodedRevertReason = ethCall.getValue().substring(10);
            List<Type> decoded = FunctionReturnDecoder.decode(encodedRevertReason, revertReasonTypes);
            Utf8String decodedRevertReason = (Utf8String) decoded.get(0);
            System.out.println("decodedRevertReason"+decoded);
            return Optional.of(decodedRevertReason.getValue());
        }
        System.out.println("hrereer666");
        return Optional.empty();
    }

    private static void issueClaim(MainContract mainContract, Web3j web3j, String verifiableClaimLink, String verifiableClaimHash, String doctorAddress, Credentials CREDENTIALS) throws Exception {
        try {
            TransactionReceipt receipt = mainContract.setOrUpdateVerifiableClaimDetails(verifiableClaimLink, verifiableClaimHash, doctorAddress).send();
            String txHash = receipt.getTransactionHash();
            String a = mainContract.getUpdateClaimDetailsEvents(receipt).get(0).hash;
            String b = mainContract.getUpdateClaimDetailsEvents(receipt).get(0).link;
            String c = mainContract.getUpdateClaimDetailsEvents(receipt).get(0).doctor;
            System.out.println(a + " " + b + " " + c);
            System.out.println(txHash);
        } catch (Exception e) {
            final Function _function = new Function(
                    "setOrUpdateVerifiableClaimDetails",
                    Arrays.asList(new Utf8String(verifiableClaimLink), new Utf8String(verifiableClaimHash), new Address(doctorAddress)),
                    Arrays.asList(new TypeReference<Utf8String>() {
                    }));

            String encodedFunction = FunctionEncoder.encode(_function);
            EthCall ethCall = web3j.ethCall(
                    Transaction.createEthCallTransaction(
                            CREDENTIALS.getAddress(),
                            mainContract.getContractAddress(),
                            encodedFunction),
                    DefaultBlockParameterName.LATEST
            ).send();

            Optional<String> revertReason = getRevertReason(ethCall);
            System.out.println(revertReason.get());
        }
    }
    private static void getDoctorDetails(MainContract mainContract, String doctorAddress) throws Exception {
        Tuple2<String, String> receipt = mainContract.getDoctorDetails(doctorAddress).send();
        String txHash = receipt.getValue1();
        String txHash1 = receipt.getValue2();
        System.out.println(txHash);
        System.out.println(txHash1);
    }


    private static void generateECKeyPair(KeyPair keyPair) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, CipherException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException, UnsupportedEncodingException, NoSuchPaddingException {
        String text = "hi there bro";
        String cipher = AsymmetricEncDec.encryptString(text, keyPair.getPublic());
        String msg = AsymmetricEncDec.decryptString(cipher, keyPair.getPrivate());
        System.out.println("cipher: "+cipher);
        System.out.println("msg: "+msg);

    }

    private static BigInteger createRecord(DoctorContract doctorContract,String challenge, Web3j web3j, Credentials CREDENTIALS) throws IOException {


        BigInteger timeStamp = BigInteger.valueOf((new Date()).getTime());
        try {
            TransactionReceipt receipt = doctorContract.createNewRecord(challenge, timeStamp).send();
            BigInteger recordNumber = doctorContract.getRecordCreatedEvents(receipt).get(0).recordNumber;

            System.out.println("record number: "+recordNumber);
            String txHash = receipt.getTransactionHash();
            System.out.println(txHash);
            return recordNumber;
        } catch (Exception e){
            final Function _function = new Function(
                    "createNewRecord",
                    Arrays.asList( new Utf8String(challenge),new Uint(timeStamp)),  // this must match all the method input types you have in your function, make it an empty list otherwise
                    Arrays.asList(new TypeReference<Utf8String>() {})); //this must match your function return types. For example if your function returns a uint8 use: `Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));`

            String encodedFunction = FunctionEncoder.encode(_function);
            EthCall ethCall = web3j.ethCall(
                    Transaction.createEthCallTransaction(
                            CREDENTIALS.getAddress(), // this is your wallet's address. Use `credentials.getAddress();` if you do not know what yours is
                            doctorContract.getContractAddress(), // this should be the same as what is in the load function above
                            encodedFunction
                    ),
                    DefaultBlockParameterName.LATEST
            ).send();


            Optional<String> revertReason = getRevertReason(ethCall); // this is the same function from the blog post mentioned
            System.out.println(revertReason.get()); // outputs: 'Already voted.'
        }
        return BigInteger.valueOf(0);


    }

    private static String readChallenge(DoctorContract doctorContract,BigInteger recordNumber ,Web3j web3j, Credentials CREDENTIALS) throws Exception {
        return doctorContract.readChallenge(recordNumber).send();
    }

    private static boolean sendResponse(DoctorContract doctorContract,String response, BigInteger recordNumber, Web3j web3j, Credentials CREDENTIALS) throws IOException {
        try {
            TransactionReceipt receipt = doctorContract.sendResponse(response, recordNumber).send();
            BigInteger recordNum = doctorContract.getSendResponseEventEvents(receipt).get(0).recordNumber;

            System.out.println("record number: "+recordNumber);
            String txHash = receipt.getTransactionHash();
            System.out.println(txHash);
            return Integer.valueOf(recordNum.toString())> 0;


        } catch (Exception e){
            final Function _function = new Function(
                    "sendResponse",
                    Arrays.asList( new Utf8String(response),new Uint(recordNumber)),  // this must match all the method input types you have in your function, make it an empty list otherwise
                    Arrays.asList(new TypeReference<Utf8String>() {})); //this must match your function return types. For example if your function returns a uint8 use: `Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));`

            String encodedFunction = FunctionEncoder.encode(_function);
            EthCall ethCall = web3j.ethCall(
                    Transaction.createEthCallTransaction(
                            CREDENTIALS.getAddress(), // this is your wallet's address. Use `credentials.getAddress();` if you do not know what yours is
                            doctorContract.getContractAddress(), // this should be the same as what is in the load function above
                            encodedFunction
                    ),
                    DefaultBlockParameterName.LATEST
            ).send();


            Optional<String> revertReason = getRevertReason(ethCall); // this is the same function from the blog post mentioned
            System.out.println(revertReason.get()); // outputs: 'Already voted.'
        }
        return false;
    }

    private static String readResponse(DoctorContract doctorContract,BigInteger recordNumber ,Web3j web3j, Credentials CREDENTIALS) throws Exception {
        return doctorContract.readResponse(recordNumber).send();
    }

    private static boolean closeRecordByDoctor(DoctorContract doctorContract,BigInteger recordNumber, Web3j web3j, Credentials CREDENTIALS) throws IOException {
        try {
            TransactionReceipt receipt = doctorContract.closeRecordByDoctor(recordNumber).send();
            boolean response = doctorContract.getRecordCloseEventEvents(receipt).get(0).success;
            System.out.println("response: "+ response);
            return response;

        }catch (Exception e){
            final Function function = new Function(
                    "closeRecordByDoctor",
                    Arrays.asList(new Uint(recordNumber)),
                    Arrays.asList(new TypeReference<Type>() {})
            );
            String encodedFunction = FunctionEncoder.encode(function);
            EthCall ethCall = web3j.ethCall(Transaction.createEthCallTransaction(
                    CREDENTIALS.getAddress(),
                    doctorContract.getContractAddress(),
                    encodedFunction
                    ), DefaultBlockParameterName.LATEST
            ).send();
            Optional<String> revertReason = getRevertReason(ethCall);
            System.out.println(revertReason.get());
        }

        return false;
    }

    private static boolean closeRecordByPatient(DoctorContract doctorContract,BigInteger recordNumber, Web3j web3j, Credentials CREDENTIALS) throws IOException {
        try {
            TransactionReceipt receipt = doctorContract.closeRecordByPatient(recordNumber).send();
            boolean response = doctorContract.getRecordCloseEventEvents(receipt).get(0).success;
            System.out.println("response: "+ response);
            return response;

        }catch (Exception e){
            final Function function = new Function(
                    "closeRecordByPatient",
                    Arrays.asList(new Uint(recordNumber)),
                    Arrays.asList(new TypeReference<Type>() {})
            );
            String encodedFunction = FunctionEncoder.encode(function);
            EthCall ethCall = web3j.ethCall(Transaction.createEthCallTransaction(
                    CREDENTIALS.getAddress(),
                    doctorContract.getContractAddress(),
                    encodedFunction
                    ), DefaultBlockParameterName.LATEST
            ).send();
            Optional<String> revertReason = getRevertReason(ethCall);
            System.out.println(revertReason.get());
        }

        return false;
    }
    private static boolean isRecordActive(DoctorContract doctorContract, BigInteger recordNumber, Web3j web3j, Credentials CREDENTIALS) throws Exception {
        return doctorContract.isRecordActive(recordNumber).send();
    }
    private static String generateRandomChallenge() {
        return "hi there bro";
    }


}
