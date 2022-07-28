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
    private static final String BINARY = "608060405234801561001057600080fd5b50610e7d806100206000396000f3fe608060405234801561001057600080fd5b50600436106100935760003560e01c8063af5a144c11610066578063af5a144c1461010a578063b2de79fd1461011d578063bfae430214610130578063c4d66de814610151578063f2fde38b1461016457600080fd5b80635de28ae014610098578063715018a6146100c25780638a2d099c146100cc5780638da5cb5b146100ef575b600080fd5b6100ab6100a6366004610945565b610177565b60405160ff90911681526020015b60405180910390f35b6100ca6101eb565b005b6100df6100da366004610945565b6101ff565b60405190151581526020016100b9565b6033546040516001600160a01b0390911681526020016100b9565b6100ca61011836600461095e565b610250565b6100df61012b366004610a59565b6103ab565b61014361013e366004610945565b610589565b6040519081526020016100b9565b6100ca61015f366004610b00565b610600565b6100ca610172366004610b00565b610725565b6066546000908190815b818110156101e257846066828154811061019d5761019d610b24565b9060005260206000200154036101d057600085815260656020526040902060010154600160a01b900460ff1692506101e2565b806101da81610b3a565b915050610181565b50909392505050565b6101f361079e565b6101fd60006107f8565b565b6066546000908190815b818110156101e257846066828154811061022557610225610b24565b90600052602060002001540361023e57600192506101e2565b8061024881610b3a565b915050610209565b60665460009081805b828110156102a357856066828154811061027557610275610b24565b90600052602060002001540361029157600193508091506102a3565b8061029b81610b3a565b915050610259565b50826102f65760405162461bcd60e51b815260206004820152601760248201527f646f63756d656e7420646f6573206e6f7420657869737400000000000000000060448201526064015b60405180910390fd5b6000858152606560205260409020600101546001600160a01b031633146103785760405162461bcd60e51b815260206004820152603060248201527f4f6e6c7920746865206973737565722063616e206368616e676520746865206360448201526f726564656e7469616c2073746174757360801b60648201526084016102ed565b505050600091825260656020526040909120600101805460ff909216600160a01b0260ff60a01b19909216919091179055565b60675460009081906103c5906001600160a01b031661084a565b8051909150600090815b8181101561042457336001600160a01b03168482815181106103f3576103f3610b24565b60200260200101516001600160a01b0316036104125760019250610424565b8061041c81610b3a565b9150506103cf565b50816104675760405162461bcd60e51b815260206004820152601260248201527134b73b30b634b21036b9b39739b2b73232b960711b60448201526064016102ed565b877fbda6aff1adc27399496f953e769dd5eaea248b63011f5b641aae2d9531bbd3eb60008960008960405161049f9493929190610bbd565b60405180910390a2877fbda6aff1adc27399496f953e769dd5eaea248b63011f5b641aae2d9531bbd3eb6001886000896040516104df9493929190610bbd565b60405180910390a250506040805160608101825243815233602080830191825260008385018181528b8252606590925293842092518355905160019283018054925160ff16600160a01b026001600160a81b03199093166001600160a01b03929092169190911791909117905560668054808301825592527f46501879b8ca8525e8c2fd519e2fbfcfa2ebea26501294aa02cbfcfb12e94354909101879055915050949350505050565b6066546000908190815b818110156105da5784606682815481106105af576105af610b24565b9060005260206000200154036105c857600192506105da565b806105d281610b3a565b915050610593565b5081156105f65750505060009081526065602052604090205490565b5060009392505050565b600054610100900460ff16158080156106205750600054600160ff909116105b8061063a5750303b15801561063a575060005460ff166001145b61069d5760405162461bcd60e51b815260206004820152602e60248201527f496e697469616c697a61626c653a20636f6e747261637420697320616c72656160448201526d191e481a5b9a5d1a585b1a5e995960921b60648201526084016102ed565b6000805460ff1916600117905580156106c0576000805461ff0019166101001790555b606780546001600160a01b0319166001600160a01b0384161790558015610721576000805461ff0019169055604051600181527f7f26b83ff96e1f2b6a682f133852f6798a09c465da95921460cefb38474024989060200160405180910390a15b5050565b61072d61079e565b6001600160a01b0381166107925760405162461bcd60e51b815260206004820152602660248201527f4f776e61626c653a206e6577206f776e657220697320746865207a65726f206160448201526564647265737360d01b60648201526084016102ed565b61079b816107f8565b50565b6033546001600160a01b031633146101fd5760405162461bcd60e51b815260206004820181905260248201527f4f776e61626c653a2063616c6c6572206973206e6f7420746865206f776e657260448201526064016102ed565b603380546001600160a01b038381166001600160a01b0319831681179093556040519116919082907f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e090600090a35050565b60408051600481526024810182526020810180516001600160e01b03166311ddc2b160e31b179052905160609160009182916001600160a01b038616916108919190610bfc565b600060405180830381855afa9150503d80600081146108cc576040519150601f19603f3d011682016040523d82523d6000602084013e6108d1565b606091505b5091509150816109235760405162461bcd60e51b815260206004820152601b60248201527f73746174696363616c6c20616c6c6f77616e6365206661696c6564000000000060448201526064016102ed565b6000818060200190518101906109399190610d5e565b50909695505050505050565b60006020828403121561095757600080fd5b5035919050565b6000806040838503121561097157600080fd5b82359150602083013560ff8116811461098957600080fd5b809150509250929050565b634e487b7160e01b600052604160045260246000fd5b604051601f8201601f1916810167ffffffffffffffff811182821017156109d3576109d3610994565b604052919050565b600067ffffffffffffffff8211156109f5576109f5610994565b50601f01601f191660200190565b600082601f830112610a1457600080fd5b8135610a27610a22826109db565b6109aa565b818152846020838601011115610a3c57600080fd5b816020850160208301376000918101602001919091529392505050565b60008060008060808587031215610a6f57600080fd5b84359350602085013567ffffffffffffffff80821115610a8e57600080fd5b610a9a88838901610a03565b94506040870135915080821115610ab057600080fd5b610abc88838901610a03565b93506060870135915080821115610ad257600080fd5b50610adf87828801610a03565b91505092959194509250565b6001600160a01b038116811461079b57600080fd5b600060208284031215610b1257600080fd5b8135610b1d81610aeb565b9392505050565b634e487b7160e01b600052603260045260246000fd5b600060018201610b5a57634e487b7160e01b600052601160045260246000fd5b5060010190565b60005b83811015610b7c578181015183820152602001610b64565b83811115610b8b576000848401525b50505050565b60008151808452610ba9816020860160208601610b61565b601f01601f19169290920160200192915050565b60ff85168152608060208201526000610bd96080830186610b91565b8460408401528281036060840152610bf18185610b91565b979650505050505050565b60008251610c0e818460208701610b61565b9190910192915050565b600067ffffffffffffffff821115610c3257610c32610994565b5060051b60200190565b600082601f830112610c4d57600080fd5b81516020610c5d610a2283610c18565b82815260059290921b84018101918181019086841115610c7c57600080fd5b8286015b84811015610cf857805167ffffffffffffffff811115610ca05760008081fd5b8701603f81018913610cb25760008081fd5b848101516040610cc4610a22836109db565b8281528b82848601011115610cd95760008081fd5b610ce883898301848701610b61565b8652505050918301918301610c80565b509695505050505050565b600082601f830112610d1457600080fd5b81516020610d24610a2283610c18565b82815260059290921b84018101918181019086841115610d4357600080fd5b8286015b84811015610cf85780518352918301918301610d47565b600080600060608486031215610d7357600080fd5b835167ffffffffffffffff80821115610d8b57600080fd5b818601915086601f830112610d9f57600080fd5b81516020610daf610a2283610c18565b82815260059290921b8401810191818101908a841115610dce57600080fd5b948201945b83861015610df5578551610de681610aeb565b82529482019490820190610dd3565b91890151919750909350505080821115610e0e57600080fd5b610e1a87838801610c3c565b93506040860151915080821115610e3057600080fd5b50610e3d86828701610d03565b915050925092509256fea2646970667358221220074f570872368640f28c7b98781897d77d049224a6a45aefb97720f6172332d264736f6c634300080d0033";

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
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>(true) {}, new TypeReference<Uint8>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Utf8String>() {}));
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
            typedResponse.credentialHash = (byte[]) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.fieldKey = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.fieldValue = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.blockNumber = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.updateTime = (String) eventValues.getNonIndexedValues().get(3).getValue();
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
                typedResponse.credentialHash = (byte[]) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.fieldKey = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.fieldValue = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.blockNumber = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
                typedResponse.updateTime = (String) eventValues.getNonIndexedValues().get(3).getValue();
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
