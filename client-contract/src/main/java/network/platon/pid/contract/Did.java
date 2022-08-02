package network.platon.did.contract;

import com.platon.abi.solidity.EventEncoder;
import com.platon.abi.solidity.TypeReference;
import com.platon.abi.solidity.datatypes.Address;
import com.platon.abi.solidity.datatypes.Bool;
import com.platon.abi.solidity.datatypes.Event;
import com.platon.abi.solidity.datatypes.Function;
import com.platon.abi.solidity.datatypes.Type;
import com.platon.abi.solidity.datatypes.Utf8String;
import com.platon.abi.solidity.datatypes.generated.Int8;
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
public class Did extends Contract {
    private static final String BINARY = "608060405234801561001057600080fd5b50610a95806100206000396000f3fe608060405234801561001057600080fd5b50600436106100935760003560e01c8063520aeecd11610066578063520aeecd1461010e578063715018a6146101215780638da5cb5b14610129578063e4e9f5c014610144578063f2fde38b1461016557600080fd5b806301dbe85814610098578063170abf9c146100ad57806330ccebb5146100d55780633bcc8a87146100fb575b600080fd5b6100ab6100a63660046107d5565b610178565b005b6100c06100bb3660046107ff565b61024f565b60405190151581526020015b60405180910390f35b6100e86100e33660046107ff565b6102bc565b60405160009190910b81526020016100cc565b6100c06101093660046108cb565b61033e565b6100c061011c366004610947565b610455565b6100ab610621565b6033546040516001600160a01b0390911681526020016100cc565b6101576101523660046107ff565b610635565b6040519081526020016100cc565b6100ab6101733660046107ff565b6106b0565b606654600090815b818110156101da57336001600160a01b0316606682815481106101a5576101a5610996565b6000918252602090912001546001600160a01b0316036101c857600192506101da565b806101d2816109ac565b915050610180565b50816102275760405162461bcd60e51b8152602060048201526017602482015276191bd8dd5b595b9d08191bd95cc81b9bdd08195e1a5cdd604a1b60448201526064015b60405180910390fd5b5050336000908152606560205260409020600101805460ff191660ff92909216919091179055565b6066546000908190815b818110156102b357846001600160a01b03166066828154811061027e5761027e610996565b6000918252602090912001546001600160a01b0316036102a157600192506102b3565b806102ab816109ac565b915050610259565b50909392505050565b60665460009060001990825b818110156102b357846001600160a01b0316606682815481106102ed576102ed610996565b6000918252602090912001546001600160a01b03160361032c576001600160a01b038516600090815260656020526040812060010154900b92506102b3565b80610336816109ac565b9150506102c8565b6066546000908190815b818110156103a257336001600160a01b03166066828154811061036d5761036d610996565b6000918252602090912001546001600160a01b03160361039057600192506103a2565b8061039a816109ac565b915050610348565b50816103ea5760405162461bcd60e51b8152602060048201526017602482015276191bd8dd5b595b9d08191bd95cc81b9bdd08195e1a5cdd604a1b604482015260640161021e565b33600081815260656020526040908190205490517f4bdf02929c49ebabe416224f8f1ec84797a40af54baab393a859cdc00805a1899161042f918a918a918a90610a20565b60405180910390a250503360009081526065602052604090204390555060019392505050565b6066546000908190815b818110156104b957336001600160a01b03166066828154811061048457610484610996565b6000918252602090912001546001600160a01b0316036104a757600192506104b9565b806104b1816109ac565b91505061045f565b5081156105085760405162461bcd60e51b815260206004820152601760248201527f646f63756d656e7420616c726561647920657869737473000000000000000000604482015260640161021e565b336001600160a01b03167f4bdf02929c49ebabe416224f8f1ec84797a40af54baab393a859cdc00805a1896000886000886040516105499493929190610a20565b60405180910390a2336001600160a01b03167f4bdf02929c49ebabe416224f8f1ec84797a40af54baab393a859cdc00805a1896001876000886040516105929493929190610a20565b60405180910390a25050604080518082018252438152600060208083018281523380845260659092529382209251835592516001928301805460ff191660ff90921691909117905560668054808401825591527f46501879b8ca8525e8c2fd519e2fbfcfa2ebea26501294aa02cbfcfb12e943540180546001600160a01b031916909217909155949350505050565b610629610729565b6106336000610783565b565b6066546000908190815b818110156102b357846001600160a01b03166066828154811061066457610664610996565b6000918252602090912001546001600160a01b03160361069e576001600160a01b03851660009081526065602052604090205492506102b3565b806106a8816109ac565b91505061063f565b6106b8610729565b6001600160a01b03811661071d5760405162461bcd60e51b815260206004820152602660248201527f4f776e61626c653a206e6577206f776e657220697320746865207a65726f206160448201526564647265737360d01b606482015260840161021e565b61072681610783565b50565b6033546001600160a01b031633146106335760405162461bcd60e51b815260206004820181905260248201527f4f776e61626c653a2063616c6c6572206973206e6f7420746865206f776e6572604482015260640161021e565b603380546001600160a01b038381166001600160a01b0319831681179093556040519116919082907f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e090600090a35050565b6000602082840312156107e757600080fd5b81358060000b81146107f857600080fd5b9392505050565b60006020828403121561081157600080fd5b81356001600160a01b03811681146107f857600080fd5b634e487b7160e01b600052604160045260246000fd5b600082601f83011261084f57600080fd5b813567ffffffffffffffff8082111561086a5761086a610828565b604051601f8301601f19908116603f0116810190828211818310171561089257610892610828565b816040528381528660208588010111156108ab57600080fd5b836020870160208301376000602085830101528094505050505092915050565b6000806000606084860312156108e057600080fd5b833560ff811681146108f157600080fd5b9250602084013567ffffffffffffffff8082111561090e57600080fd5b61091a8783880161083e565b9350604086013591508082111561093057600080fd5b5061093d8682870161083e565b9150509250925092565b60008060006060848603121561095c57600080fd5b833567ffffffffffffffff8082111561097457600080fd5b6109808783880161083e565b9450602086013591508082111561090e57600080fd5b634e487b7160e01b600052603260045260246000fd5b6000600182016109cc57634e487b7160e01b600052601160045260246000fd5b5060010190565b6000815180845260005b818110156109f9576020818501810151868301820152016109dd565b81811115610a0b576000602083870101525b50601f01601f19169290920160200192915050565b60ff85168152608060208201526000610a3c60808301866109d3565b8460408401528281036060840152610a5481856109d3565b97965050505050505056fea26469706673582212204b988c689731844db202ee004ba947558aaab8bef54585dc57cf40e41da4c9a964736f6c634300080d0033";

    public static final String FUNC_CHANGESTATUS = "changeStatus";

    public static final String FUNC_CREATEDID = "createDid";

    public static final String FUNC_GETLATESTBLOCK = "getLatestBlock";

    public static final String FUNC_GETSTATUS = "getStatus";

    public static final String FUNC_ISIDENTITYEXIST = "isIdentityExist";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_RENOUNCEOWNERSHIP = "renounceOwnership";

    public static final String FUNC_SETATTRIBUTE = "setAttribute";

    public static final String FUNC_TRANSFEROWNERSHIP = "transferOwnership";

    public static final Event DIDATTRIBUTECHANGE_EVENT = new Event("DIDAttributeChange", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint8>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Utf8String>() {}));
    ;

    public static final Event INITIALIZED_EVENT = new Event("Initialized", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
    ;

    public static final Event OWNERSHIPTRANSFERRED_EVENT = new Event("OwnershipTransferred", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}));
    ;

    protected Did(String contractAddress, Web3j web3j, Credentials credentials, GasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    protected Did(String contractAddress, Web3j web3j, TransactionManager transactionManager, GasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public List<DIDAttributeChangeEventResponse> getDIDAttributeChangeEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(DIDATTRIBUTECHANGE_EVENT, transactionReceipt);
        ArrayList<DIDAttributeChangeEventResponse> responses = new ArrayList<DIDAttributeChangeEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            DIDAttributeChangeEventResponse typedResponse = new DIDAttributeChangeEventResponse();
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

    public Observable<DIDAttributeChangeEventResponse> dIDAttributeChangeEventObservable(PlatonFilter filter) {
        return web3j.platonLogObservable(filter).map(new Func1<Log, DIDAttributeChangeEventResponse>() {
            @Override
            public DIDAttributeChangeEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(DIDATTRIBUTECHANGE_EVENT, log);
                DIDAttributeChangeEventResponse typedResponse = new DIDAttributeChangeEventResponse();
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

    public Observable<DIDAttributeChangeEventResponse> dIDAttributeChangeEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        PlatonFilter filter = new PlatonFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(DIDATTRIBUTECHANGE_EVENT));
        return dIDAttributeChangeEventObservable(filter);
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

    public RemoteCall<TransactionReceipt> changeStatus(BigInteger status) {
        final Function function = new Function(
                FUNC_CHANGESTATUS, 
                Arrays.<Type>asList(new com.platon.abi.solidity.datatypes.generated.Int8(status)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> createDid(String createTime, String publicKey, String updateTime) {
        final Function function = new Function(
                FUNC_CREATEDID, 
                Arrays.<Type>asList(new com.platon.abi.solidity.datatypes.Utf8String(createTime), 
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
                Arrays.<TypeReference<?>>asList(new TypeReference<Int8>() {}));
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

    public static RemoteCall<Did> deploy(Web3j web3j, Credentials credentials, GasProvider contractGasProvider) {
        return deployRemoteCall(Did.class, web3j, credentials, contractGasProvider, BINARY,  "");
    }

    public static RemoteCall<Did> deploy(Web3j web3j, TransactionManager transactionManager, GasProvider contractGasProvider) {
        return deployRemoteCall(Did.class, web3j, transactionManager, contractGasProvider, BINARY,  "");
    }

    public static Did load(String contractAddress, Web3j web3j, Credentials credentials, GasProvider contractGasProvider) {
        return new Did(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static Did load(String contractAddress, Web3j web3j, TransactionManager transactionManager, GasProvider contractGasProvider) {
        return new Did(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static class DIDAttributeChangeEventResponse {
        public Log log;

        public String identity;

        public BigInteger fieldKey;

        public String fieldValue;

        public BigInteger blockNumber;

        public String updateTime;
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
}
