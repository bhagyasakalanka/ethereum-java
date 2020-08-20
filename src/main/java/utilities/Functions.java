package utilities;

import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.AbiTypes;
import org.web3j.crypto.Hash;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.utils.Numeric;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Functions {
    private Functions(){}
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

}
