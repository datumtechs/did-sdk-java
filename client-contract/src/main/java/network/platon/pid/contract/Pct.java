package network.platon.pid.contract;

import com.platon.abi.solidity.EventEncoder;
import com.platon.abi.solidity.TypeReference;
import com.platon.abi.solidity.datatypes.Address;
import com.platon.abi.solidity.datatypes.DynamicBytes;
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
import com.platon.tuples.generated.Tuple3;
import com.platon.tx.Contract;
import com.platon.tx.TransactionManager;
import com.platon.tx.gas.GasProvider;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
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
public class Pct extends Contract {
    private static final String BINARY = "608060405234801561001057600080fd5b50610c75806100206000396000f3fe608060405234801561001057600080fd5b506004361061007d5760003560e01c80638f795e221161005b5780638f795e22146100cd57806395220d00146100d5578063c4d66de8146100f7578063f2fde38b1461010a57600080fd5b80630f47e21d14610082578063715018a6146100a85780638da5cb5b146100b2575b600080fd5b6100956100903660046108dd565b61011d565b6040519081526020015b60405180910390f35b6100b06102c3565b005b6033546040516001600160a01b03909116815260200161009f565b606654610095565b6100e86100e336600461096c565b610329565b60405161009f939291906109e1565b6100b0610105366004610a2c565b610479565b6100b0610118366004610a2c565b610508565b6065546000908190610137906001600160a01b03166105d3565b8051909150600090815b8181101561019657336001600160a01b031684828151811061016557610165610a50565b60200260200101516001600160a01b0316036101845760019250610196565b8061018e81610a7c565b915050610141565b50816101de5760405162461bcd60e51b815260206004820152601260248201527134b73b30b634b21036b9b39739b2b73232b960711b60448201526064015b60405180910390fd5b60408051606081018252338152602080820189815282840189905260665460009081526067835293909320825181546001600160a01b0319166001600160a01b039091161781559251805192939261023c92600185019201906107a5565b50604082015180516102589160028401916020909101906107a5565b50506066546040513392507f455459674d2e900484971e2223af5549736bd88a214c9d38a17a0c2957569e9390610292908a908a90610a95565b60405180910390a36066546102a8906001610aba565b60668190556102b990600190610ad2565b9695505050505050565b6033546001600160a01b0316331461031d5760405162461bcd60e51b815260206004820181905260248201527f4f776e61626c653a2063616c6c6572206973206e6f7420746865206f776e657260448201526064016101d5565b61032760006106cb565b565b6000818152606760205260408120805460018201805460609384936001600160a01b03169291600290910190829061036090610ae9565b80601f016020809104026020016040519081016040528092919081815260200182805461038c90610ae9565b80156103d95780601f106103ae576101008083540402835291602001916103d9565b820191906000526020600020905b8154815290600101906020018083116103bc57829003601f168201915b505050505091508080546103ec90610ae9565b80601f016020809104026020016040519081016040528092919081815260200182805461041890610ae9565b80156104655780601f1061043a57610100808354040283529160200191610465565b820191906000526020600020905b81548152906001019060200180831161044857829003601f168201915b505050505090509250925092509193909250565b6000610485600161071d565b9050801561049d576000805461ff0019166101001790555b606580546001600160a01b0319166001600160a01b0384161790556103e86066558015610504576000805461ff0019169055604051600181527f7f26b83ff96e1f2b6a682f133852f6798a09c465da95921460cefb38474024989060200160405180910390a15b5050565b6033546001600160a01b031633146105625760405162461bcd60e51b815260206004820181905260248201527f4f776e61626c653a2063616c6c6572206973206e6f7420746865206f776e657260448201526064016101d5565b6001600160a01b0381166105c75760405162461bcd60e51b815260206004820152602660248201527f4f776e61626c653a206e6577206f776e657220697320746865207a65726f206160448201526564647265737360d01b60648201526084016101d5565b6105d0816106cb565b50565b60408051600481526024810182526020810180516001600160e01b03166311ddc2b160e31b179052905160609160009182916001600160a01b0386169161061a9190610b23565b600060405180830381855afa9150503d8060008114610655576040519150601f19603f3d011682016040523d82523d6000602084013e61065a565b606091505b5091509150816106ac5760405162461bcd60e51b815260206004820152601b60248201527f73746174696363616c6c20616c6c6f77616e6365206661696c6564000000000060448201526064016101d5565b6000818060200190518101906106c29190610b3f565b95945050505050565b603380546001600160a01b038381166001600160a01b0319831681179093556040519116919082907f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e090600090a35050565b60008054610100900460ff1615610764578160ff1660011480156107405750303b155b61075c5760405162461bcd60e51b81526004016101d590610bf1565b506000919050565b60005460ff80841691161061078b5760405162461bcd60e51b81526004016101d590610bf1565b506000805460ff191660ff92909216919091179055600190565b8280546107b190610ae9565b90600052602060002090601f0160209004810192826107d35760008555610819565b82601f106107ec57805160ff1916838001178555610819565b82800160010185558215610819579182015b828111156108195782518255916020019190600101906107fe565b50610825929150610829565b5090565b5b80821115610825576000815560010161082a565b634e487b7160e01b600052604160045260246000fd5b604051601f8201601f1916810167ffffffffffffffff8111828210171561087d5761087d61083e565b604052919050565b600067ffffffffffffffff83111561089f5761089f61083e565b6108b2601f8401601f1916602001610854565b90508281528383830111156108c657600080fd5b828260208301376000602084830101529392505050565b600080604083850312156108f057600080fd5b823567ffffffffffffffff8082111561090857600080fd5b818501915085601f83011261091c57600080fd5b61092b86833560208501610885565b9350602085013591508082111561094157600080fd5b508301601f8101851361095357600080fd5b61096285823560208401610885565b9150509250929050565b60006020828403121561097e57600080fd5b5035919050565b60005b838110156109a0578181015183820152602001610988565b838111156109af576000848401525b50505050565b600081518084526109cd816020860160208601610985565b601f01601f19169290920160200192915050565b6001600160a01b0384168152606060208201819052600090610a05908301856109b5565b82810360408401526102b981856109b5565b6001600160a01b03811681146105d057600080fd5b600060208284031215610a3e57600080fd5b8135610a4981610a17565b9392505050565b634e487b7160e01b600052603260045260246000fd5b634e487b7160e01b600052601160045260246000fd5b600060018201610a8e57610a8e610a66565b5060010190565b604081526000610aa860408301856109b5565b82810360208401526106c281856109b5565b60008219821115610acd57610acd610a66565b500190565b600082821015610ae457610ae4610a66565b500390565b600181811c90821680610afd57607f821691505b602082108103610b1d57634e487b7160e01b600052602260045260246000fd5b50919050565b60008251610b35818460208701610985565b9190910192915050565b60006020808385031215610b5257600080fd5b825167ffffffffffffffff80821115610b6a57600080fd5b818501915085601f830112610b7e57600080fd5b815181811115610b9057610b9061083e565b8060051b9150610ba1848301610854565b8181529183018401918481019088841115610bbb57600080fd5b938501935b83851015610be55784519250610bd583610a17565b8282529385019390850190610bc0565b98975050505050505050565b6020808252602e908201527f496e697469616c697a61626c653a20636f6e747261637420697320616c72656160408201526d191e481a5b9a5d1a585b1a5e995960921b60608201526080019056fea264697066735822122095d040b2fc2fb392de04049c6d1475d203c6dd952e86c9e2af9ac721b6c7469564736f6c634300080d0033";

    public static final String FUNC_GETNEXTPCTID = "getNextPctId";

    public static final String FUNC_GETPCTINFO = "getPctInfo";

    public static final String FUNC_INITIALIZE = "initialize";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_REGISTERPCT = "registerPct";

    public static final String FUNC_RENOUNCEOWNERSHIP = "renounceOwnership";

    public static final String FUNC_TRANSFEROWNERSHIP = "transferOwnership";

    public static final Event INITIALIZED_EVENT = new Event("Initialized", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
    ;

    public static final Event OWNERSHIPTRANSFERRED_EVENT = new Event("OwnershipTransferred", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}));
    ;

    public static final Event REGISTERPCT_EVENT = new Event("RegisterPct", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Utf8String>() {}, new TypeReference<DynamicBytes>() {}));
    ;

    protected Pct(String contractAddress, Web3j web3j, Credentials credentials, GasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    protected Pct(String contractAddress, Web3j web3j, TransactionManager transactionManager, GasProvider contractGasProvider) {
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

    public List<RegisterPctEventResponse> getRegisterPctEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(REGISTERPCT_EVENT, transactionReceipt);
        ArrayList<RegisterPctEventResponse> responses = new ArrayList<RegisterPctEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            RegisterPctEventResponse typedResponse = new RegisterPctEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.pctId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.issuer = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.jsonSchema = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.extra = (byte[]) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<RegisterPctEventResponse> registerPctEventObservable(PlatonFilter filter) {
        return web3j.platonLogObservable(filter).map(new Func1<Log, RegisterPctEventResponse>() {
            @Override
            public RegisterPctEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(REGISTERPCT_EVENT, log);
                RegisterPctEventResponse typedResponse = new RegisterPctEventResponse();
                typedResponse.log = log;
                typedResponse.pctId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.issuer = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.jsonSchema = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.extra = (byte[]) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<RegisterPctEventResponse> registerPctEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        PlatonFilter filter = new PlatonFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(REGISTERPCT_EVENT));
        return registerPctEventObservable(filter);
    }

    public RemoteCall<BigInteger> getNextPctId() {
        final Function function = new Function(FUNC_GETNEXTPCTID, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<Tuple3<String, String, byte[]>> getPctInfo(BigInteger pctId) {
        final Function function = new Function(FUNC_GETPCTINFO, 
                Arrays.<Type>asList(new com.platon.abi.solidity.datatypes.generated.Uint256(pctId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Utf8String>() {}, new TypeReference<DynamicBytes>() {}));
        return new RemoteCall<Tuple3<String, String, byte[]>>(
                new Callable<Tuple3<String, String, byte[]>>() {
                    @Override
                    public Tuple3<String, String, byte[]> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple3<String, String, byte[]>(
                                (String) results.get(0).getValue(), 
                                (String) results.get(1).getValue(), 
                                (byte[]) results.get(2).getValue());
                    }
                });
    }

    public RemoteCall<TransactionReceipt> initialize(String voteAddress) {
        final Function function = new Function(
                FUNC_INITIALIZE, 
                Arrays.<Type>asList(new com.platon.abi.solidity.datatypes.Address(voteAddress)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> owner() {
        final Function function = new Function(FUNC_OWNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> registerPct(String jsonSchema, byte[] extra) {
        final Function function = new Function(
                FUNC_REGISTERPCT, 
                Arrays.<Type>asList(new com.platon.abi.solidity.datatypes.Utf8String(jsonSchema), 
                new com.platon.abi.solidity.datatypes.DynamicBytes(extra)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
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

    public static RemoteCall<Pct> deploy(Web3j web3j, Credentials credentials, GasProvider contractGasProvider) {
        return deployRemoteCall(Pct.class, web3j, credentials, contractGasProvider, BINARY,  "");
    }

    public static RemoteCall<Pct> deploy(Web3j web3j, TransactionManager transactionManager, GasProvider contractGasProvider) {
        return deployRemoteCall(Pct.class, web3j, transactionManager, contractGasProvider, BINARY,  "");
    }

    public static Pct load(String contractAddress, Web3j web3j, Credentials credentials, GasProvider contractGasProvider) {
        return new Pct(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static Pct load(String contractAddress, Web3j web3j, TransactionManager transactionManager, GasProvider contractGasProvider) {
        return new Pct(contractAddress, web3j, transactionManager, contractGasProvider);
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

    public static class RegisterPctEventResponse {
        public Log log;

        public BigInteger pctId;

        public String issuer;

        public String jsonSchema;

        public byte[] extra;
    }
}
