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
    private static final String BINARY = "608060405234801561001057600080fd5b50610aa7806100206000396000f3fe608060405234801561001057600080fd5b506004361061007d5760003560e01c8063abf66aed1161005b578063abf66aed146100cf578063bfae4302146100e2578063c4d66de814610103578063f2fde38b1461011657600080fd5b8063715018a6146100825780638a2d099c1461008c5780638da5cb5b146100b4575b600080fd5b61008a610129565b005b61009f61009a366004610726565b610194565b60405190151581526020015b60405180910390f35b6033546040516001600160a01b0390911681526020016100ab565b61009f6100dd3660046107f6565b6101ee565b6100f56100f0366004610726565b610389565b6040519081526020016100ab565b61008a610111366004610878565b610400565b61008a610124366004610878565b610489565b6033546001600160a01b031633146101885760405162461bcd60e51b815260206004820181905260248201527f4f776e61626c653a2063616c6c6572206973206e6f7420746865206f776e657260448201526064015b60405180910390fd5b6101926000610554565b565b6066546000908190815b818110156101e55784606682815481106101ba576101ba61089c565b9060005260206000200154036101d357600192506101e5565b806101dd816108b2565b91505061019e565b50909392505050565b6067546000908190610208906001600160a01b03166105a6565b8051909150600090815b8181101561026757336001600160a01b03168482815181106102365761023661089c565b60200260200101516001600160a01b0316036102555760019250610267565b8061025f816108b2565b915050610212565b50816102aa5760405162461bcd60e51b815260206004820152601260248201527134b73b30b634b21036b9b39739b2b73232b960711b604482015260640161017f565b7fad1f4fc02828882c8dc0931fe80a0ffbe0bf083316996ccf319e8b4200b9b0cd8760008860006040516102e19493929190610909565b60405180910390a17fad1f4fc02828882c8dc0931fe80a0ffbe0bf083316996ccf319e8b4200b9b0cd8760018760006040516103209493929190610909565b60405180910390a15050604080516020808201835243825260008881526065909152918220905190556066805460018181018355919092527f46501879b8ca8525e8c2fd519e2fbfcfa2ebea26501294aa02cbfcfb12e943549091018690559150509392505050565b6066546000908190815b818110156103da5784606682815481106103af576103af61089c565b9060005260206000200154036103c857600192506103da565b806103d2816108b2565b915050610393565b5081156103f65750505060009081526065602052604090205490565b5060009392505050565b600061040c600161069e565b90508015610424576000805461ff0019166101001790555b606780546001600160a01b0319166001600160a01b0384161790558015610485576000805461ff0019169055604051600181527f7f26b83ff96e1f2b6a682f133852f6798a09c465da95921460cefb38474024989060200160405180910390a15b5050565b6033546001600160a01b031633146104e35760405162461bcd60e51b815260206004820181905260248201527f4f776e61626c653a2063616c6c6572206973206e6f7420746865206f776e6572604482015260640161017f565b6001600160a01b0381166105485760405162461bcd60e51b815260206004820152602660248201527f4f776e61626c653a206e6577206f776e657220697320746865207a65726f206160448201526564647265737360d01b606482015260840161017f565b61055181610554565b50565b603380546001600160a01b038381166001600160a01b0319831681179093556040519116919082907f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e090600090a35050565b60408051600481526024810182526020810180516001600160e01b03166311ddc2b160e31b179052905160609160009182916001600160a01b038616916105ed9190610955565b600060405180830381855afa9150503d8060008114610628576040519150601f19603f3d011682016040523d82523d6000602084013e61062d565b606091505b50915091508161067f5760405162461bcd60e51b815260206004820152601b60248201527f73746174696363616c6c20616c6c6f77616e6365206661696c65640000000000604482015260640161017f565b6000818060200190518101906106959190610971565b95945050505050565b60008054610100900460ff16156106e5578160ff1660011480156106c15750303b155b6106dd5760405162461bcd60e51b815260040161017f90610a23565b506000919050565b60005460ff80841691161061070c5760405162461bcd60e51b815260040161017f90610a23565b506000805460ff191660ff92909216919091179055600190565b60006020828403121561073857600080fd5b5035919050565b634e487b7160e01b600052604160045260246000fd5b604051601f8201601f1916810167ffffffffffffffff8111828210171561077e5761077e61073f565b604052919050565b600082601f83011261079757600080fd5b813567ffffffffffffffff8111156107b1576107b161073f565b6107c4601f8201601f1916602001610755565b8181528460208386010111156107d957600080fd5b816020850160208301376000918101602001919091529392505050565b60008060006060848603121561080b57600080fd5b83359250602084013567ffffffffffffffff8082111561082a57600080fd5b61083687838801610786565b9350604086013591508082111561084c57600080fd5b5061085986828701610786565b9150509250925092565b6001600160a01b038116811461055157600080fd5b60006020828403121561088a57600080fd5b813561089581610863565b9392505050565b634e487b7160e01b600052603260045260246000fd5b6000600182016108d257634e487b7160e01b600052601160045260246000fd5b5060010190565b60005b838110156108f45781810151838201526020016108dc565b83811115610903576000848401525b50505050565b84815260ff8416602082015260806040820152600083518060808401526109378160a08501602088016108d9565b606083019390935250601f91909101601f19160160a0019392505050565b600082516109678184602087016108d9565b9190910192915050565b6000602080838503121561098457600080fd5b825167ffffffffffffffff8082111561099c57600080fd5b818501915085601f8301126109b057600080fd5b8151818111156109c2576109c261073f565b8060051b91506109d3848301610755565b81815291830184019184810190888411156109ed57600080fd5b938501935b83851015610a175784519250610a0783610863565b82825293850193908501906109f2565b98975050505050505050565b6020808252602e908201527f496e697469616c697a61626c653a20636f6e747261637420697320616c72656160408201526d191e481a5b9a5d1a585b1a5e995960921b60608201526080019056fea2646970667358221220c079ce2dedae32bd3b7f7b38be14dbad5fe2cc94f289ac533ec860e0a169f9af64736f6c634300080d0033";

    public static final String FUNC_CREATECREDENTIAL = "createCredential";

    public static final String FUNC_GETLATESTBLOCK = "getLatestBlock";

    public static final String FUNC_INITIALIZE = "initialize";

    public static final String FUNC_ISHASHEXIST = "isHashExist";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_RENOUNCEOWNERSHIP = "renounceOwnership";

    public static final String FUNC_TRANSFEROWNERSHIP = "transferOwnership";

    public static final Event CREDENTIALATTRIBUTECHANGE_EVENT = new Event("CredentialAttributeChange", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Uint8>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}));
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

    public RemoteCall<TransactionReceipt> createCredential(byte[] credentialHash, String signer, String signatureData) {
        final Function function = new Function(
                FUNC_CREATECREDENTIAL, 
                Arrays.<Type>asList(new com.platon.abi.solidity.datatypes.generated.Bytes32(credentialHash), 
                new com.platon.abi.solidity.datatypes.Utf8String(signer), 
                new com.platon.abi.solidity.datatypes.Utf8String(signatureData)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> getLatestBlock(byte[] credentialHash) {
        final Function function = new Function(FUNC_GETLATESTBLOCK, 
                Arrays.<Type>asList(new com.platon.abi.solidity.datatypes.generated.Bytes32(credentialHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
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

    public static RemoteCall<Credential> deploy(Web3j web3j, Credentials credentials, GasProvider contractGasProvider, String string) {
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
