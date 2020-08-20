package ContractorHandlers;

import javaethereum.contracts.generated.DoctorContract;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Uint;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.StaticGasProvider;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import static utilities.Functions.getRevertReason;

public class DoctorContractorHandler {
    private static String doctor = "C:/Users/User/Desktop/etheruem_server/data/keystore/UTC--2020-07-19T16-37-27.237405200Z--0993bb88376afbce65514cb4a691b2930a24c499";
    private static final BigInteger GasPrice =Convert.toWei("20", Convert.Unit.GWEI).toBigInteger();
    private static final BigInteger GasLimit = Convert.toWei("3000000", Convert.Unit.WEI).toBigInteger();
    private static DoctorContractorHandler instance;
    private DoctorContractorHandler(){}
    public static DoctorContractorHandler getInstance(){
        if(instance == null){
            synchronized (DoctorContractorHandler.class){
                if(instance == null){
                    instance = new DoctorContractorHandler();
                }
            }
        }
        return instance;
    }
    public void deployDoctorContractor(Web3j web3) throws Exception {
        String walletPass = "123456";

        Credentials credentials = WalletUtils.loadCredentials(walletPass, doctor);

        BigInteger GasPrice = Convert.toWei("3", Convert.Unit.GWEI).toBigInteger();
        BigInteger GasLimit = Convert.toWei("1800000", Convert.Unit.WEI).toBigInteger();


        DoctorContract doctorContract = DoctorContract.deploy(web3, credentials, new StaticGasProvider(GasPrice, GasLimit)).send();
        String contractAddress = doctorContract.getContractAddress();

        System.out.println(contractAddress);
    }

    public DoctorContract getWrapperForDoctorContractor(Web3j web3, String contractorAddress, Credentials CREDENTIALS) throws IOException, CipherException {

        DoctorContract doctorContract = DoctorContract.load(contractorAddress,web3, CREDENTIALS, new StaticGasProvider(GasPrice, GasLimit));
        return doctorContract;
    }

    public BigInteger createRecord(DoctorContract doctorContract, String challenge, Web3j web3j, Credentials CREDENTIALS) throws IOException {


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

    public String readChallenge(DoctorContract doctorContract, BigInteger recordNumber, Web3j web3j, Credentials CREDENTIALS) throws Exception {
        return doctorContract.readChallenge(recordNumber).send();
    }

    public boolean sendResponse(DoctorContract doctorContract, String response, BigInteger recordNumber, Web3j web3j, Credentials CREDENTIALS) throws IOException {
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

    public String readResponse(DoctorContract doctorContract, BigInteger recordNumber, Web3j web3j, Credentials CREDENTIALS) throws Exception {
        return doctorContract.readResponse(recordNumber).send();
    }

    public boolean closeRecordByDoctor(DoctorContract doctorContract, BigInteger recordNumber, Web3j web3j, Credentials CREDENTIALS) throws IOException {
        boolean result = false;
        try {
            TransactionReceipt receipt = doctorContract.closeRecordByDoctor(recordNumber).send();
            boolean response = doctorContract.getRecordCloseEventEvents(receipt).get(0).success;
            System.out.println("response: " + response);
            result = response;

        } catch (Exception e) {
            final Function function = new Function(
                    "closeRecordByDoctor",
                    Arrays.asList(new Uint(recordNumber)),
                    Arrays.asList(new TypeReference<Utf8String>() {
                    })
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

        return result;
    }

    private boolean closeRecordByPatient(DoctorContract doctorContract,BigInteger recordNumber, Web3j web3j, Credentials CREDENTIALS) throws IOException {
        try {
            TransactionReceipt receipt = doctorContract.closeRecordByPatient(recordNumber).send();
            boolean response = doctorContract.getRecordCloseEventEvents(receipt).get(0).success;
            System.out.println("response: "+ response);
            return response;

        }catch (Exception e){
            final Function function = new Function(
                    "closeRecordByPatient",
                    Arrays.asList(new Uint(recordNumber)),
                    Arrays.asList(new TypeReference<?>[]{new TypeReference<Utf8String>() {
                    }})
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

    public boolean isRecordActive(DoctorContract doctorContract, BigInteger recordNumber, Web3j web3j, Credentials CREDENTIALS) throws Exception {
        return doctorContract.isRecordActive(recordNumber).send();
    }


}
