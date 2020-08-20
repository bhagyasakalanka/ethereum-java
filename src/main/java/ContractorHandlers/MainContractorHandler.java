package ContractorHandlers;

import crypto.KeyHandler;
import javaethereum.contracts.generated.MainContract;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tx.gas.StaticGasProvider;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Optional;

import static utilities.Functions.getRevertReason;

public class MainContractorHandler {
    private static final BigInteger GasPrice =Convert.toWei("20", Convert.Unit.GWEI).toBigInteger();
    private static final BigInteger GasLimit = Convert.toWei("3000000", Convert.Unit.WEI).toBigInteger();
    private static MainContractorHandler instance;
    private MainContractorHandler(){}
    public static MainContractorHandler getInstance(){
        if(instance == null){
            synchronized (MainContractorHandler.class){
                if(instance == null){
                    instance = new MainContractorHandler();
                }
            }
        }
        return instance;
    }
    public void deployMain(Web3j web3) throws Exception {
        String walletPass = "123456";
        String walletPath = "C:/Users/User/Desktop/etheruem_server/data/keystore/UTC--2020-07-19T16-36-50.572265900Z--d09b4fad07c80d4e721b1e7c4affb39e08396845";

        Credentials credentials = WalletUtils.loadCredentials(walletPass, walletPath);

        BigInteger GasPrice = Convert.toWei("3", Convert.Unit.GWEI).toBigInteger();
        BigInteger GasLimit = Convert.toWei("1800000", Convert.Unit.WEI).toBigInteger();


        MainContract mainContract = MainContract.deploy(web3, credentials, new StaticGasProvider(GasPrice, GasLimit)).send();
        String contractAddress = mainContract.getContractAddress();

        System.out.println(contractAddress);
    }

    public MainContract getWrapperForMainContractor(Web3j web3, String contractorAddress, Credentials CREDENTIALS) throws IOException, CipherException {

        MainContract mainContract = MainContract.load(contractorAddress,web3, CREDENTIALS, new StaticGasProvider(GasPrice, GasLimit));
        return mainContract;
    }

    private void registerDoctor(MainContract mainContract, Web3j web3j, String profileDataLink, String profileHash, Credentials CREDENTIALS) throws Exception {

        try {
            TransactionReceipt receipt = mainContract.registerDoctor(profileDataLink, profileHash).send();
            String a = mainContract.getRegisterDoctorEventEvents(receipt).get(0).hash;
            String b = mainContract.getUpdateClaimDetailsEvents(receipt).get(0).link;
            String c = mainContract.getUpdateClaimDetailsEvents(receipt).get(0).doctor;
            System.out.println(a + " " + b + " " + c);
            String txHash = receipt.getTransactionHash();
            System.out.println(txHash);
        }catch (Exception e){
            final Function _function = new Function(
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

    private  void addIssuer(MainContract mainContract, Web3j web3j, String issuerAddress, Credentials CREDENTIALS) throws IOException {
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

    public  void issueClaim(MainContract mainContract, Web3j web3j, String verifiableClaimLink, String verifiableClaimHash, String doctorAddress, Credentials CREDENTIALS) throws Exception {
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
    public  void getDoctorDetails(MainContract mainContract, String doctorAddress) throws Exception {
        Tuple2<String, String> receipt = mainContract.getDoctorDetails(doctorAddress).send();
        String txHash = receipt.getValue1();
        String txHash1 = receipt.getValue2();
        System.out.println(txHash);
        System.out.println(txHash1);
    }

}
