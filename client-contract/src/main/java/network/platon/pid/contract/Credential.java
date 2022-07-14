package network.platon.pid.contract;

import com.platon.abi.solidity.EventEncoder;
import com.platon.abi.solidity.TypeReference;
import com.platon.abi.solidity.datatypes.Address;
import com.platon.abi.solidity.datatypes.Bool;
import com.platon.abi.solidity.datatypes.Event;
import com.platon.abi.solidity.datatypes.Function;
import com.platon.abi.solidity.datatypes.Type;
import com.platon.abi.solidity.datatypes.Utf8String;
import com.platon.abi.solidity.datatypes.generated.Bytes32;
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
public class Credential extends Contract {
    private static final String BINARY = "608060405234801561001057600080fd5b50610cb1806100206000396000f3fe608060405234801561001057600080fd5b50600436106100935760003560e01c8063af5a144c11610066578063af5a144c1461010a578063b2de79fd1461011d578063bfae430214610130578063c4d66de814610151578063f2fde38b1461016457600080fd5b80635de28ae014610098578063715018a6146100c25780638a2d099c146100cc5780638da5cb5b146100ef575b600080fd5b6100ab6100a63660046108bb565b610177565b60405160ff90911681526020015b60405180910390f35b6100ca6101e4565b005b6100df6100da3660046108bb565b61024f565b60405190151581526020016100b9565b6033546040516001600160a01b0390911681526020016100b9565b6100ca6101183660046108d4565b6102a0565b6100df61012b3660046109c1565b610364565b61014361013e3660046108bb565b61051e565b6040519081526020016100b9565b6100ca61015f366004610a68565b610595565b6100ca610172366004610a68565b61061e565b6066546000908190815b818110156101db57846066828154811061019d5761019d610a8c565b9060005260206000200154036101c95760008581526065602052604090206001015460ff1692506101db565b806101d381610aa2565b915050610181565b50909392505050565b6033546001600160a01b031633146102435760405162461bcd60e51b815260206004820181905260248201527f4f776e61626c653a2063616c6c6572206973206e6f7420746865206f776e657260448201526064015b60405180910390fd5b61024d60006106e9565b565b6066546000908190815b818110156101db57846066828154811061027557610275610a8c565b90600052602060002001540361028e57600192506101db565b8061029881610aa2565b915050610259565b606654600090815b818110156102ef5784606682815481106102c4576102c4610a8c565b9060005260206000200154036102dd57600192506102ef565b806102e781610aa2565b9150506102a8565b508161033d5760405162461bcd60e51b815260206004820152601760248201527f646f63756d656e7420646f6573206e6f74206578697374000000000000000000604482015260640161023a565b5050600091825260656020526040909120600101805460ff191660ff909216919091179055565b606754600090819061037e906001600160a01b031661073b565b8051909150600090815b818110156103dd57336001600160a01b03168482815181106103ac576103ac610a8c565b60200260200101516001600160a01b0316036103cb57600192506103dd565b806103d581610aa2565b915050610388565b50816104205760405162461bcd60e51b815260206004820152601260248201527134b73b30b634b21036b9b39739b2b73232b960711b604482015260640161023a565b7fbda6aff1adc27399496f953e769dd5eaea248b63011f5b641aae2d9531bbd3eb88600089600089604051610459959493929190610b25565b60405180910390a17fbda6aff1adc27399496f953e769dd5eaea248b63011f5b641aae2d9531bbd3eb8860018860008960405161049a959493929190610b25565b60405180910390a15050604080518082018252438152600060208083018281528a835260659091529281209151825591516001918201805460ff191660ff90921691909117905560668054808301825592527f46501879b8ca8525e8c2fd519e2fbfcfa2ebea26501294aa02cbfcfb12e94354909101969096555093949350505050565b6066546000908190815b8181101561056f57846066828154811061054457610544610a8c565b90600052602060002001540361055d576001925061056f565b8061056781610aa2565b915050610528565b50811561058b5750505060009081526065602052604090205490565b5060009392505050565b60006105a16001610833565b905080156105b9576000805461ff0019166101001790555b606780546001600160a01b0319166001600160a01b038416179055801561061a576000805461ff0019169055604051600181527f7f26b83ff96e1f2b6a682f133852f6798a09c465da95921460cefb38474024989060200160405180910390a15b5050565b6033546001600160a01b031633146106785760405162461bcd60e51b815260206004820181905260248201527f4f776e61626c653a2063616c6c6572206973206e6f7420746865206f776e6572604482015260640161023a565b6001600160a01b0381166106dd5760405162461bcd60e51b815260206004820152602660248201527f4f776e61626c653a206e6577206f776e657220697320746865207a65726f206160448201526564647265737360d01b606482015260840161023a565b6106e6816106e9565b50565b603380546001600160a01b038381166001600160a01b0319831681179093556040519116919082907f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e090600090a35050565b60408051600481526024810182526020810180516001600160e01b03166311ddc2b160e31b179052905160609160009182916001600160a01b038616916107829190610b6b565b600060405180830381855afa9150503d80600081146107bd576040519150601f19603f3d011682016040523d82523d6000602084013e6107c2565b606091505b5091509150816108145760405162461bcd60e51b815260206004820152601b60248201527f73746174696363616c6c20616c6c6f77616e6365206661696c65640000000000604482015260640161023a565b60008180602001905181019061082a9190610b87565b95945050505050565b60008054610100900460ff161561087a578160ff1660011480156108565750303b155b6108725760405162461bcd60e51b815260040161023a90610c2d565b506000919050565b60005460ff8084169116106108a15760405162461bcd60e51b815260040161023a90610c2d565b506000805460ff191660ff92909216919091179055600190565b6000602082840312156108cd57600080fd5b5035919050565b600080604083850312156108e757600080fd5b82359150602083013560ff811681146108ff57600080fd5b809150509250929050565b634e487b7160e01b600052604160045260246000fd5b604051601f8201601f1916810167ffffffffffffffff811182821017156109495761094961090a565b604052919050565b600082601f83011261096257600080fd5b813567ffffffffffffffff81111561097c5761097c61090a565b61098f601f8201601f1916602001610920565b8181528460208386010111156109a457600080fd5b816020850160208301376000918101602001919091529392505050565b600080600080608085870312156109d757600080fd5b84359350602085013567ffffffffffffffff808211156109f657600080fd5b610a0288838901610951565b94506040870135915080821115610a1857600080fd5b610a2488838901610951565b93506060870135915080821115610a3a57600080fd5b50610a4787828801610951565b91505092959194509250565b6001600160a01b03811681146106e657600080fd5b600060208284031215610a7a57600080fd5b8135610a8581610a53565b9392505050565b634e487b7160e01b600052603260045260246000fd5b600060018201610ac257634e487b7160e01b600052601160045260246000fd5b5060010190565b60005b83811015610ae4578181015183820152602001610acc565b83811115610af3576000848401525b50505050565b60008151808452610b11816020860160208601610ac9565b601f01601f19169290920160200192915050565b85815260ff8516602082015260a060408201526000610b4760a0830186610af9565b8460608401528281036080840152610b5f8185610af9565b98975050505050505050565b60008251610b7d818460208701610ac9565b9190910192915050565b60006020808385031215610b9a57600080fd5b825167ffffffffffffffff80821115610bb257600080fd5b818501915085601f830112610bc657600080fd5b815181811115610bd857610bd861090a565b8060051b9150610be9848301610920565b8181529183018401918481019088841115610c0357600080fd5b938501935b83851015610b5f5784519250610c1d83610a53565b8282529385019390850190610c08565b6020808252602e908201527f496e697469616c697a61626c653a20636f6e747261637420697320616c72656160408201526d191e481a5b9a5d1a585b1a5e995960921b60608201526080019056fea2646970667358221220ce32633102405ebaff85bac6a14f11630c621df0dc46a0e7d90fda3f80cb2d0564736f6c634300080d0033";

    public static final String FUNC_CHANGESTATUS = "changeStatus";

    public static final String FUNC_CREATECREDENTIAL = "createCredential";

    public static final String FUNC_GETLATESTBLOCK = "getLatestBlock";

    public static final String FUNC_GETSTATUS = "getStatus";

    public static final String FUNC_INITIALIZE = "initialize";

    public static final String FUNC_ISHASHEXIST = "isHashExist";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_RENOUNCEOWNERSHIP = "renounceOwnership";

    public static final String FUNC_TRANSFEROWNERSHIP = "transferOwnership";

    public static final Event CREDENTIALATTRIBUTECHANGE_EVENT = new Event("CredentialAttributeChange", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Uint8>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Utf8String>() {}));
    ;

    public static final Event INITIALIZED_EVENT = new Event("Initialized", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
    ;

    public static final Event OWNERSHIPTRANSFERRED_EVENT = new Event("OwnershipTransferred", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}));
    ;

    protected Credential(String contractAddress, Web3j web3j, Credentials credentials, GasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    protected Credential(String contractAddress, Web3j web3j, TransactionManager transactionManager, GasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public List<CredentialAttributeChangeEventResponse> getCredentialAttributeChangeEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(CREDENTIALATTRIBUTECHANGE_EVENT, transactionReceipt);
        ArrayList<CredentialAttributeChangeEventResponse> responses = new ArrayList<CredentialAttributeChangeEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            CredentialAttributeChangeEventResponse typedResponse = new CredentialAttributeChangeEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.credentialHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.fieldKey = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.fieldValue = (String) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.blockNumber = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
            typedResponse.updateTime = (String) eventValues.getNonIndexedValues().get(4).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<CredentialAttributeChangeEventResponse> credentialAttributeChangeEventObservable(PlatonFilter filter) {
        return web3j.platonLogObservable(filter).map(new Func1<Log, CredentialAttributeChangeEventResponse>() {
            @Override
            public CredentialAttributeChangeEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(CREDENTIALATTRIBUTECHANGE_EVENT, log);
                CredentialAttributeChangeEventResponse typedResponse = new CredentialAttributeChangeEventResponse();
                typedResponse.log = log;
                typedResponse.credentialHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.fieldKey = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.fieldValue = (String) eventValues.getNonIndexedValues().get(2).getValue();
                typedResponse.blockNumber = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
                typedResponse.updateTime = (String) eventValues.getNonIndexedValues().get(4).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<CredentialAttributeChangeEventResponse> credentialAttributeChangeEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        PlatonFilter filter = new PlatonFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(CREDENTIALATTRIBUTECHANGE_EVENT));
        return credentialAttributeChangeEventObservable(filter);
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

    public RemoteCall<TransactionReceipt> changeStatus(byte[] credentialHash, BigInteger status) {
        final Function function = new Function(
                FUNC_CHANGESTATUS, 
                Arrays.<Type>asList(new com.platon.abi.solidity.datatypes.generated.Bytes32(credentialHash), 
                new com.platon.abi.solidity.datatypes.generated.Uint8(status)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> createCredential(byte[] credentialHash, String signer, String signatureData, String updateTime) {
        final Function function = new Function(
                FUNC_CREATECREDENTIAL, 
                Arrays.<Type>asList(new com.platon.abi.solidity.datatypes.generated.Bytes32(credentialHash), 
                new com.platon.abi.solidity.datatypes.Utf8String(signer), 
                new com.platon.abi.solidity.datatypes.Utf8String(signatureData), 
                new com.platon.abi.solidity.datatypes.Utf8String(updateTime)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> getLatestBlock(byte[] credentialHash) {
        final Function function = new Function(FUNC_GETLATESTBLOCK, 
                Arrays.<Type>asList(new com.platon.abi.solidity.datatypes.generated.Bytes32(credentialHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> getStatus(byte[] credentialHash) {
        final Function function = new Function(FUNC_GETSTATUS, 
                Arrays.<Type>asList(new com.platon.abi.solidity.datatypes.generated.Bytes32(credentialHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> initialize(String voteAddress) {
        final Function function = new Function(
                FUNC_INITIALIZE, 
                Arrays.<Type>asList(new com.platon.abi.solidity.datatypes.Address(voteAddress)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Boolean> isHashExist(byte[] credentialHash) {
        final Function function = new Function(FUNC_ISHASHEXIST, 
                Arrays.<Type>asList(new com.platon.abi.solidity.datatypes.generated.Bytes32(credentialHash)), 
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

    public RemoteCall<TransactionReceipt> transferOwnership(String newOwner) {
        final Function function = new Function(
                FUNC_TRANSFEROWNERSHIP, 
                Arrays.<Type>asList(new com.platon.abi.solidity.datatypes.Address(newOwner)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static RemoteCall<Credential> deploy(Web3j web3j, Credentials credentials, GasProvider contractGasProvider) {
        return deployRemoteCall(Credential.class, web3j, credentials, contractGasProvider, BINARY,  "");
    }

    public static RemoteCall<Credential> deploy(Web3j web3j, TransactionManager transactionManager, GasProvider contractGasProvider) {
        return deployRemoteCall(Credential.class, web3j, transactionManager, contractGasProvider, BINARY,  "");
    }

    public static Credential load(String contractAddress, Web3j web3j, Credentials credentials, GasProvider contractGasProvider) {
        return new Credential(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static Credential load(String contractAddress, Web3j web3j, TransactionManager transactionManager, GasProvider contractGasProvider) {
        return new Credential(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static class CredentialAttributeChangeEventResponse {
        public Log log;

        public byte[] credentialHash;

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
