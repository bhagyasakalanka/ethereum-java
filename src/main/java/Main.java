import javaethereum.contracts.generated.DocumentRegistry;
import javaethereum.contracts.generated.MainContract;
import org.bouncycastle.asn1.DEROctetString;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bytes;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.AbiTypes;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.gas.StaticGasProvider;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import javax.print.Doc;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Main {
    private static String DEFAULT_PORT = "http://localhost:8545";
    public static void main(String[] args) throws Exception {
        System.out.println("Connecting to Ethereum ...");
        Web3j web3 = connect();
        System.out.println("Successfuly connected to Ethereum");
        //test(web3);
        //generateNewAccount3();
        //createWallet();
        //sendTransaction(web3, "0xa5ac43684a1111af7a0676a0786f90a003be14ac", 1);
        //deploy(web3);
        //deployMain(web3);
//        DocumentRegistry documentRegistry = getWrapperForContractor(web3, "0xe8df6cf15939ea37ba459d25df846118a1185eb6");
        MainContract mainContract = getWrapperForMainContractor(web3, "0x04fdf6c35704ed08797f0281316a20eb9f411eac");
        //addIssuer(mainContract,web3);
        getDoctorDetails(mainContract);
        //issueClaim(mainContract);
        //registerDoctor(mainContract);
//        notarizeDocument(documentRegistry);
//        isNotarized(documentRegistry);
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
        String walletPath = "C:/Users/User/Desktop/etheruem_server/data/keystore/UTC--2020-07-16T18-58-02.011591600Z--02e3d423bd01cfbed9a06d19ca9ff14abaf6a570";

        Credentials credentials = WalletUtils.loadCredentials(walletPass, walletPath);

        BigInteger GasPrice =Convert.toWei("3", Convert.Unit.GWEI).toBigInteger();
        BigInteger GasLimit = Convert.toWei("1800000", Convert.Unit.WEI).toBigInteger();


        MainContract mainContract = MainContract.deploy(web3, credentials, new StaticGasProvider(GasPrice, GasLimit)).send();
        String contractAddress = mainContract.getContractAddress();

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

    private static MainContract getWrapperForMainContractor(Web3j web3, String contractorAddress) throws IOException, CipherException {
        String walletPass = "123456";
        String walletPath = "C:/Users/User/Desktop/etheruem_server/data/keystore/UTC--2020-07-16T18-58-42.172712700Z--eabff46d129e738cec10daf70242b96574c7fb20";
        BigInteger GasPrice =Convert.toWei("20", Convert.Unit.GWEI).toBigInteger();
        BigInteger GasLimit = Convert.toWei("3000000", Convert.Unit.WEI).toBigInteger();

        Credentials credentials = WalletUtils.loadCredentials(walletPass, walletPath);
        MainContract mainContract = MainContract.load(contractorAddress,web3, credentials, new StaticGasProvider(GasPrice, GasLimit));
        return mainContract;
    }

    private static void notarizeDocument(DocumentRegistry documentRegistry) throws Exception {

        String hex = "QmXoypizjW3WknFiJnKLwHCnL72vedxjQkDDP1mXWo6u";
        byte[] doc = Numeric.hexStringToByteArray(hex);
        TransactionReceipt receipt = documentRegistry.notarizeDocument(hex).send();
        String txHash = receipt.getTransactionHash();
        System.out.println(txHash);
    }

    private static void isNotarized(DocumentRegistry documentRegistry) throws Exception {
        RemoteCall<Boolean> receipt = documentRegistry.isNotarized("QmXoypizjW3WknFiJnKLwHCnL72vedxjQkDDP1mXWo6u");
        Boolean txHash = receipt.send();

        System.out.println(txHash);
    }

    private static void registerDoctor(MainContract mainContract) throws Exception {
        String profileData = "im a doctor";
        String link = "www.link.com";
        TransactionReceipt receipt = mainContract.registerDoctor(link, profileData).send();
        String txHash = receipt.getTransactionHash();
        System.out.println(txHash);
    }

    private static void addIssuer(MainContract mainContract, Web3j web3j) throws IOException {
        try {
            TransactionReceipt receipt = mainContract.addClaimIssuers("0xeABFF46d129e738CEC10DAf70242b96574c7fb20").send();
            
            String txHash = receipt.getTransactionHash();
            System.out.println(txHash);
        } catch (Exception e){
            final org.web3j.abi.datatypes.Function voteFunction = new org.web3j.abi.datatypes.Function(
                    "addClaimIssuers",
                    Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String("0xeABFF46d129e738CEC10DAf70242b96574c7fb20")),  // this must match all the method input types you have in your function, make it an empty list otherwise
                    Arrays.<TypeReference<?>>asList(new TypeReference<org.web3j.abi.datatypes.Bool>() {})); //this must match your function return types. For example if your function returns a uint8 use: `Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));`


            String encodedFunction = FunctionEncoder.encode(voteFunction);
            EthCall ethCall = web3j.ethCall(
                        org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction(
                                "0xeABFF46d129e738CEC10DAf70242b96574c7fb20", // this is your wallet's address. Use `credentials.getAddress();` if you do not know what yours is
                                "0x04fdf6c35704ed08797f0281316a20eb9f411eac", // this should be the same as what is in the load function above
                                encodedFunction
                        ),
                        DefaultBlockParameterName.LATEST
                ).send();


            Optional<String> revertReason = getRevertReason(ethCall); // this is the same function from the blog post mentioned
            System.out.println(revertReason.get()); // outputs: 'Already voted.'
        }


    }
    public static Optional<String> getRevertReason(EthCall ethCall) {
        String errorMethodId = "0x08c379d0"; // Numeric.toHexString(Hash.sha3("Error(string)".getBytes())).substring(0, 10)
        List<TypeReference<Type>> revertReasonTypes = Collections.singletonList(TypeReference.create((Class<Type>) AbiTypes.getType("string")));

        if (!ethCall.hasError() && ethCall.getValue() != null && ethCall.getValue().startsWith(errorMethodId)) {
            System.out.println("hrereer");
            String encodedRevertReason = ethCall.getValue();
            List<Type> decoded = FunctionReturnDecoder.decode(encodedRevertReason, revertReasonTypes);
            Utf8String decodedRevertReason = (Utf8String) decoded.get(0);
            return Optional.of(decodedRevertReason.getValue());
        }
        return Optional.empty();
    }

    private static void issueClaim(MainContract mainContract) throws Exception {
        String link = "www.verify.lk";
        String hash = "verify hash";
        TransactionReceipt receipt = mainContract.setOrUpdateVerifiableClaimDetails(link, hash, "0x09E140b30ebB2054D2B302678BD776F0c9d30FEf").send();
        String txHash = receipt.getTransactionHash();
        System.out.println(txHash);
    }
    private static void getDoctorDetails(MainContract mainContract) throws Exception {
        Tuple2<String, String> receipt = mainContract.getDoctorDetails("0x09E140b30ebB2054D2B302678BD776F0c9d30FEf").send();
        String txHash = receipt.getValue1();
        String txHash1 = receipt.getValue2();
        System.out.println(txHash);
        System.out.println(txHash1);
    }



}
