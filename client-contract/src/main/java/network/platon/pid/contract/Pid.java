package network.platon.pid.contract;

import com.platon.abi.solidity.EventEncoder;
import com.platon.abi.solidity.TypeReference;
import com.platon.abi.solidity.datatypes.Address;
import com.platon.abi.solidity.datatypes.Bool;
import com.platon.abi.solidity.datatypes.Event;
import com.platon.abi.solidity.datatypes.Function;
import com.platon.abi.solidity.datatypes.Type;
import com.platon.abi.solidity.datatypes.Utf8String;
import com.platon.abi.solidity.datatypes.generated.Uint256;
import com.platon.abi.solidity.datatypes.generated.Uint8;
import com.platon.crypto.Credentials;
import com.platon.protocol.Web3j;
import com.platon.protocol.core.DefaultBlockParameter;
import com.platon.protocol.core.RemoteCall;
import com.platon.protocol.core.methods.request.PlatonFilter;
import com.platon.protocol.core.methods.response.Log;
import com.platon.protocol.core.methods.response.TransactionReceipt;
import com.platon.tx.Contract;
import com.platon.tx.TransactionManager;
import com.platon.tx.gas.GasProvider;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import rx.Observable;
import rx.functions.Func1;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://github.com/PlatONnetwork/client-sdk-java/releases">platon-web3j command line tools</a>,
 * or the com.platon.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/PlatONnetwork/client-sdk-java/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 1.1.0.0.
 */
public class Pid extends Contract {
    private static final String BINARY = "608060405234801561001057600080fd5b50610b63806100206000396000f3fe608060405234801561001057600080fd5b50600436106100935760003560e01c8063715018a611610066578063715018a61461010b5780638da5cb5b1461011557806395bc953814610130578063e4e9f5c014610143578063f2fde38b1461016457600080fd5b8063170abf9c1461009857806330ccebb5146100c05780633bcc8a87146100e55780636596dc56146100f8575b600080fd5b6100ab6100a636600461081f565b610177565b60405190151581526020015b60405180910390f35b6100d36100ce36600461081f565b6101e4565b60405160ff90911681526020016100b7565b6100ab6100f3366004610908565b610265565b6100ab61010636600461097c565b61036f565b61011361054f565b005b6033546040516001600160a01b0390911681526020016100b7565b61011361013e366004610a29565b6105b5565b61015661015136600461081f565b610687565b6040519081526020016100b7565b61011361017236600461081f565b610702565b6066546000908190815b818110156101db57846001600160a01b0316606682815481106101a6576101a6610a44565b6000918252602090912001546001600160a01b0316036101c957600192506101db565b806101d381610a5a565b915050610181565b50909392505050565b6066546000908190815b818110156101db57846001600160a01b03166066828154811061021357610213610a44565b6000918252602090912001546001600160a01b031603610253576001600160a01b03851660009081526065602052604090206001015460ff1692506101db565b8061025d81610a5a565b9150506101ee565b6066546000908190815b818110156102c957336001600160a01b03166066828154811061029457610294610a44565b6000918252602090912001546001600160a01b0316036102b757600192506102c9565b806102c181610a5a565b91505061026f565b50816103165760405162461bcd60e51b8152602060048201526017602482015276191bd8dd5b595b9d08191bd95cc81b9bdd08195e1a5cdd604a1b60448201526064015b60405180910390fd5b3360008181526065602052604090819020549051600080516020610b0e83398151915291610349918a918a918a90610ace565b60405180910390a250503360009081526065602052604090204390555060019392505050565b6066546000908190815b818110156103d357336001600160a01b03166066828154811061039e5761039e610a44565b6000918252602090912001546001600160a01b0316036103c157600192506103d3565b806103cb81610a5a565b915050610379565b5081156104225760405162461bcd60e51b815260206004820152601760248201527f646f63756d656e7420616c726561647920657869737473000000000000000000604482015260640161030d565b336001600160a01b0316600080516020610b0e8339815191526000896000886040516104519493929190610ace565b60405180910390a2336001600160a01b0316600080516020610b0e8339815191526001886000886040516104889493929190610ace565b60405180910390a2336001600160a01b0316600080516020610b0e8339815191526002876000886040516104bf9493929190610ace565b60405180910390a25050604080518082018252438152600060208083018281523380845260659092529382209251835592516001928301805460ff191660ff90921691909117905560668054808401825591527f46501879b8ca8525e8c2fd519e2fbfcfa2ebea26501294aa02cbfcfb12e943540180546001600160a01b03191690921790915595945050505050565b6033546001600160a01b031633146105a95760405162461bcd60e51b815260206004820181905260248201527f4f776e61626c653a2063616c6c6572206973206e6f7420746865206f776e6572604482015260640161030d565b6105b360006107cd565b565b606654600090815b8181101561061757336001600160a01b0316606682815481106105e2576105e2610a44565b6000918252602090912001546001600160a01b0316036106055760019250610617565b8061060f81610a5a565b9150506105bd565b508161065f5760405162461bcd60e51b8152602060048201526017602482015276191bd8dd5b595b9d08191bd95cc81b9bdd08195e1a5cdd604a1b604482015260640161030d565b5050336000908152606560205260409020600101805460ff191660ff92909216919091179055565b6066546000908190815b818110156101db57846001600160a01b0316606682815481106106b6576106b6610a44565b6000918252602090912001546001600160a01b0316036106f0576001600160a01b03851660009081526065602052604090205492506101db565b806106fa81610a5a565b915050610691565b6033546001600160a01b0316331461075c5760405162461bcd60e51b815260206004820181905260248201527f4f776e61626c653a2063616c6c6572206973206e6f7420746865206f776e6572604482015260640161030d565b6001600160a01b0381166107c15760405162461bcd60e51b815260206004820152602660248201527f4f776e61626c653a206e6577206f776e657220697320746865207a65726f206160448201526564647265737360d01b606482015260840161030d565b6107ca816107cd565b50565b603380546001600160a01b038381166001600160a01b0319831681179093556040519116919082907f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e090600090a35050565b60006020828403121561083157600080fd5b81356001600160a01b038116811461084857600080fd5b9392505050565b803560ff8116811461086057600080fd5b919050565b634e487b7160e01b600052604160045260246000fd5b600082601f83011261088c57600080fd5b813567ffffffffffffffff808211156108a7576108a7610865565b604051601f8301601f19908116603f011681019082821181831017156108cf576108cf610865565b816040528381528660208588010111156108e857600080fd5b836020870160208301376000602085830101528094505050505092915050565b60008060006060848603121561091d57600080fd5b6109268461084f565b9250602084013567ffffffffffffffff8082111561094357600080fd5b61094f8783880161087b565b9350604086013591508082111561096557600080fd5b506109728682870161087b565b9150509250925092565b6000806000806080858703121561099257600080fd5b843567ffffffffffffffff808211156109aa57600080fd5b6109b68883890161087b565b955060208701359150808211156109cc57600080fd5b6109d88883890161087b565b945060408701359150808211156109ee57600080fd5b6109fa8883890161087b565b93506060870135915080821115610a1057600080fd5b50610a1d8782880161087b565b91505092959194509250565b600060208284031215610a3b57600080fd5b6108488261084f565b634e487b7160e01b600052603260045260246000fd5b600060018201610a7a57634e487b7160e01b600052601160045260246000fd5b5060010190565b6000815180845260005b81811015610aa757602081850181015186830182015201610a8b565b81811115610ab9576000602083870101525b50601f01601f19169290920160200192915050565b60ff85168152608060208201526000610aea6080830186610a81565b8460408401528281036060840152610b028185610a81565b97965050505050505056fe78acd80016b76ce5977296fbdd38a0aac1a3ff9531865bd76469cd4cb590dadda264697066735822122010b45f56b7183e02ba44e94928a98f49d2221dcee0d4a0060fb1b64afdd0a70f64736f6c634300080d0033";

    public static final String FUNC_CHANGESTATUS = "changeStatus";

    public static final String FUNC_CREATEPID = "createPid";

    public static final String FUNC_GETLATESTBLOCK = "getLatestBlock";

    public static final String FUNC_GETSTATUS = "getStatus";

    public static final String FUNC_ISIDENTITYEXIST = "isIdentityExist";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_RENOUNCEOWNERSHIP = "renounceOwnership";

    public static final String FUNC_SETATTRIBUTE = "setAttribute";

    public static final String FUNC_TRANSFEROWNERSHIP = "transferOwnership";

    public static final Event INITIALIZED_EVENT = new Event("Initialized", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
    ;

    public static final Event OWNERSHIPTRANSFERRED_EVENT = new Event("OwnershipTransferred", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}));
    ;

    public static final Event PIDATTRIBUTECHANGE_EVENT = new Event("PIDAttributeChange", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint8>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Utf8String>() {}));
    ;

    protected Pid(String contractAddress, Web3j web3j, Credentials credentials, GasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    protected Pid(String contractAddress, Web3j web3j, TransactionManager transactionManager, GasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public List<InitializedEventResponse> getInitializedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(INITIALIZED_EVENT, transactionReceipt);
        ArrayList<InitializedEventResponse> responses = new ArrayList<InitializedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            InitializedEventResponse typedResponse = new InitializedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.version = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<InitializedEventResponse> initializedEventObservable(PlatonFilter filter) {
        return web3j.platonLogObservable(filter).map(new Func1<Log, InitializedEventResponse>() {
            @Override
            public InitializedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(INITIALIZED_EVENT, log);
                InitializedEventResponse typedResponse = new InitializedEventResponse();
                typedResponse.log = log;
                typedResponse.version = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<InitializedEventResponse> initializedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        PlatonFilter filter = new PlatonFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(INITIALIZED_EVENT));
        return initializedEventObservable(filter);
    }

    public List<OwnershipTransferredEventResponse> getOwnershipTransferredEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(OWNERSHIPTRANSFERRED_EVENT, transactionReceipt);
        ArrayList<OwnershipTransferredEventResponse> responses = new ArrayList<OwnershipTransferredEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            OwnershipTransferredEventResponse typedResponse = new OwnershipTransferredEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.previousOwner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.newOwner = (String) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<OwnershipTransferredEventResponse> ownershipTransferredEventObservable(PlatonFilter filter) {
        return web3j.platonLogObservable(filter).map(new Func1<Log, OwnershipTransferredEventResponse>() {
            @Override
            public OwnershipTransferredEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(OWNERSHIPTRANSFERRED_EVENT, log);
                OwnershipTransferredEventResponse typedResponse = new OwnershipTransferredEventResponse();
                typedResponse.log = log;
                typedResponse.previousOwner = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.newOwner = (String) eventValues.getIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<OwnershipTransferredEventResponse> ownershipTransferredEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        PlatonFilter filter = new PlatonFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(OWNERSHIPTRANSFERRED_EVENT));
        return ownershipTransferredEventObservable(filter);
    }

    public List<PIDAttributeChangeEventResponse> getPIDAttributeChangeEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(PIDATTRIBUTECHANGE_EVENT, transactionReceipt);
        ArrayList<PIDAttributeChangeEventResponse> responses = new ArrayList<PIDAttributeChangeEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            PIDAttributeChangeEventResponse typedResponse = new PIDAttributeChangeEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.identity = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.fieldKey = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.fieldValue = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.blockNumber = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.updateTime = (String) eventValues.getNonIndexedValues().get(3).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<PIDAttributeChangeEventResponse> pIDAttributeChangeEventObservable(PlatonFilter filter) {
        return web3j.platonLogObservable(filter).map(new Func1<Log, PIDAttributeChangeEventResponse>() {
            @Override
            public PIDAttributeChangeEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(PIDATTRIBUTECHANGE_EVENT, log);
                PIDAttributeChangeEventResponse typedResponse = new PIDAttributeChangeEventResponse();
                typedResponse.log = log;
                typedResponse.identity = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.fieldKey = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.fieldValue = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.blockNumber = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
                typedResponse.updateTime = (String) eventValues.getNonIndexedValues().get(3).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<PIDAttributeChangeEventResponse> pIDAttributeChangeEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        PlatonFilter filter = new PlatonFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(PIDATTRIBUTECHANGE_EVENT));
        return pIDAttributeChangeEventObservable(filter);
    }

    public RemoteCall<TransactionReceipt> changeStatus(BigInteger status) {
        final Function function = new Function(
                FUNC_CHANGESTATUS, 
                Arrays.<Type>asList(new com.platon.abi.solidity.datatypes.generated.Uint8(status)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> createPid(String createTime, String authentication, String publicKey, String updateTime) {
        final Function function = new Function(
                FUNC_CREATEPID, 
                Arrays.<Type>asList(new com.platon.abi.solidity.datatypes.Utf8String(createTime), 
                new com.platon.abi.solidity.datatypes.Utf8String(authentication), 
                new com.platon.abi.solidity.datatypes.Utf8String(publicKey), 
                new com.platon.abi.solidity.datatypes.Utf8String(updateTime)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> getLatestBlock(String identify) {
        final Function function = new Function(FUNC_GETLATESTBLOCK, 
                Arrays.<Type>asList(new com.platon.abi.solidity.datatypes.Address(identify)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> getStatus(String identify) {
        final Function function = new Function(FUNC_GETSTATUS, 
                Arrays.<Type>asList(new com.platon.abi.solidity.datatypes.Address(identify)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<Boolean> isIdentityExist(String identify) {
        final Function function = new Function(FUNC_ISIDENTITYEXIST, 
                Arrays.<Type>asList(new com.platon.abi.solidity.datatypes.Address(identify)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<String> owner() {
        final Function function = new Function(FUNC_OWNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> renounceOwnership() {
        final Function function = new Function(
                FUNC_RENOUNCEOWNERSHIP, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> setAttribute(BigInteger fieldKey, String fieldValue, String updateTime) {
        final Function function = new Function(
                FUNC_SETATTRIBUTE, 
                Arrays.<Type>asList(new com.platon.abi.solidity.datatypes.generated.Uint8(fieldKey), 
                new com.platon.abi.solidity.datatypes.Utf8String(fieldValue), 
                new com.platon.abi.solidity.datatypes.Utf8String(updateTime)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> transferOwnership(String newOwner) {
        final Function function = new Function(
                FUNC_TRANSFEROWNERSHIP, 
                Arrays.<Type>asList(new com.platon.abi.solidity.datatypes.Address(newOwner)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static RemoteCall<Pid> deploy(Web3j web3j, Credentials credentials, GasProvider contractGasProvider) {
        return deployRemoteCall(Pid.class, web3j, credentials, contractGasProvider, BINARY,  "");
    }

    public static RemoteCall<Pid> deploy(Web3j web3j, TransactionManager transactionManager, GasProvider contractGasProvider) {
        return deployRemoteCall(Pid.class, web3j, transactionManager, contractGasProvider, BINARY,  "");
    }

    public static Pid load(String contractAddress, Web3j web3j, Credentials credentials, GasProvider contractGasProvider) {
        return new Pid(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static Pid load(String contractAddress, Web3j web3j, TransactionManager transactionManager, GasProvider contractGasProvider) {
        return new Pid(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static class InitializedEventResponse {
        public Log log;

        public BigInteger version;
    }

    public static class OwnershipTransferredEventResponse {
        public Log log;

        public String previousOwner;

        public String newOwner;
    }

    public static class PIDAttributeChangeEventResponse {
        public Log log;

        public String identity;

        public BigInteger fieldKey;

        public String fieldValue;

        public BigInteger blockNumber;

        public String updateTime;
    }
}
