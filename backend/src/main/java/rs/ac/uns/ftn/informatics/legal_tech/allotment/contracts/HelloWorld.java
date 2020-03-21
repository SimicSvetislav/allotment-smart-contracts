package rs.ac.uns.ftn.informatics.legal_tech.allotment.contracts;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.5.15.
 */
@SuppressWarnings("rawtypes")
public class HelloWorld extends Contract {
    public static final String BINARY = "608060405234801561001057600080fd5b506040516105363803806105368339818101604052604081101561003357600080fd5b810190808051604051939291908464010000000082111561005357600080fd5b90830190602082018581111561006857600080fd5b825164010000000081118282018810171561008257600080fd5b82525081516020918201929091019080838360005b838110156100af578181015183820152602001610097565b50505050905090810190601f1680156100dc5780820380516001836020036101000a031916815260200191505b5060405260209081015184519093506100fb9250600091850190610105565b50600155506101a0565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061014657805160ff1916838001178555610173565b82800160010185558215610173579182015b82811115610173578251825591602001919060010190610158565b5061017f929150610183565b5090565b61019d91905b8082111561017f5760008155600101610189565b90565b610387806101af6000396000f3fe608060405234801561001057600080fd5b50600436106100625760003560e01c8063368b877214610067578063371303c01461010f5780633fb5c1cb146101295780637cf5dab0146101465780638381f58a14610163578063ce6d41de1461016b575b600080fd5b61010d6004803603602081101561007d57600080fd5b81019060208101813564010000000081111561009857600080fd5b8201836020820111156100aa57600080fd5b803590602001918460018302840111640100000000831117156100cc57600080fd5b91908080601f0160208091040260200160405190810160405280939291908181526020018383808284376000920191909152509295506101e8945050505050565b005b6101176101ff565b60408051918252519081900360200190f35b61010d6004803603602081101561013f57600080fd5b5035610212565b6101176004803603602081101561015c57600080fd5b5035610217565b61011761021d565b610173610223565b6040805160208082528351818301528351919283929083019185019080838360005b838110156101ad578181015183820152602001610195565b50505050905090810190601f1680156101da5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b80516101fb9060009060208401906102b9565b5050565b600061020c600154610217565b90505b90565b600155565b60010190565b60015481565b60008054604080516020601f60026000196101006001881615020190951694909404938401819004810282018101909252828152606093909290918301828280156102af5780601f10610284576101008083540402835291602001916102af565b820191906000526020600020905b81548152906001019060200180831161029257829003601f168201915b5050505050905090565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106102fa57805160ff1916838001178555610327565b82800160010185558215610327579182015b8281111561032757825182559160200191906001019061030c565b50610333929150610337565b5090565b61020f91905b80821115610333576000815560010161033d56fea26469706673582212204cc2e8aabb4a452fc574e50047405e5a9a658e1e22f4ec640a0e16f5bfd32bf864736f6c63430006030033";

    public static final String FUNC_GETMESSAGE = "getMessage";

    public static final String FUNC_INC = "inc";

    public static final String FUNC_INCREMENT = "increment";

    public static final String FUNC_NUMBER = "number";

    public static final String FUNC_SETMESSAGE = "setMessage";

    public static final String FUNC_SETNUMBER = "setNumber";

    @Deprecated
    protected HelloWorld(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected HelloWorld(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected HelloWorld(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected HelloWorld(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<String> getMessage() {
        final Function function = new Function(FUNC_GETMESSAGE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<BigInteger> inc() {
        final Function function = new Function(FUNC_INC, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> increment(BigInteger num) {
        final Function function = new Function(FUNC_INCREMENT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(num)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> number() {
        final Function function = new Function(FUNC_NUMBER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> setMessage(String _message) {
        final Function function = new Function(
                FUNC_SETMESSAGE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_message)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> setNumber(BigInteger num) {
        final Function function = new Function(
                FUNC_SETNUMBER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(num)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static HelloWorld load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new HelloWorld(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static HelloWorld load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new HelloWorld(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static HelloWorld load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new HelloWorld(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static HelloWorld load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new HelloWorld(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<HelloWorld> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider, String mssg, BigInteger num) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(mssg), 
                new org.web3j.abi.datatypes.generated.Uint256(num)));
        return deployRemoteCall(HelloWorld.class, web3j, credentials, contractGasProvider, BINARY, encodedConstructor);
    }

    public static RemoteCall<HelloWorld> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider, String mssg, BigInteger num) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(mssg), 
                new org.web3j.abi.datatypes.generated.Uint256(num)));
        return deployRemoteCall(HelloWorld.class, web3j, transactionManager, contractGasProvider, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<HelloWorld> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String mssg, BigInteger num) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(mssg), 
                new org.web3j.abi.datatypes.generated.Uint256(num)));
        return deployRemoteCall(HelloWorld.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<HelloWorld> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String mssg, BigInteger num) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(mssg), 
                new org.web3j.abi.datatypes.generated.Uint256(num)));
        return deployRemoteCall(HelloWorld.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }
}
