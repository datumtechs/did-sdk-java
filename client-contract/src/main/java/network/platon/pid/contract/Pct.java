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
    private static final String BINARY = "608060405234801561001057600080fd5b50610bf1806100206000396000f3fe608060405234801561001057600080fd5b506004361061007d5760003560e01c80638f795e221161005b5780638f795e22146100cd57806395220d00146100d5578063c4d66de8146100f7578063f2fde38b1461010a57600080fd5b80630f47e21d14610082578063715018a6146100a85780638da5cb5b146100b2575b600080fd5b6100956100903660046108a7565b61011d565b6040519081526020015b60405180910390f35b6100b06102c3565b005b6033546040516001600160a01b03909116815260200161009f565b606654610095565b6100e86100e3366004610936565b6102d7565b60405161009f939291906109ab565b6100b06101053660046109f6565b610427565b6100b06101183660046109f6565b610552565b6065546000908190610137906001600160a01b03166105cb565b8051909150600090815b8181101561019657336001600160a01b031684828151811061016557610165610a1a565b60200260200101516001600160a01b0316036101845760019250610196565b8061018e81610a46565b915050610141565b50816101de5760405162461bcd60e51b815260206004820152601260248201527134b73b30b634b21036b9b39739b2b73232b960711b60448201526064015b60405180910390fd5b60408051606081018252338152602080820189815282840189905260665460009081526067835293909320825181546001600160a01b0319166001600160a01b039091161781559251805192939261023c926001850192019061076f565b506040820151805161025891600284019160209091019061076f565b50506066546040513392507f455459674d2e900484971e2223af5549736bd88a214c9d38a17a0c2957569e9390610292908a908a90610a5f565b60405180910390a36066546102a8906001610a84565b60668190556102b990600190610a9c565b9695505050505050565b6102cb6106c3565b6102d5600061071d565b565b6000818152606760205260408120805460018201805460609384936001600160a01b03169291600290910190829061030e90610ab3565b80601f016020809104026020016040519081016040528092919081815260200182805461033a90610ab3565b80156103875780601f1061035c57610100808354040283529160200191610387565b820191906000526020600020905b81548152906001019060200180831161036a57829003601f168201915b5050505050915080805461039a90610ab3565b80601f01602080910402602001604051908101604052809291908181526020018280546103c690610ab3565b80156104135780601f106103e857610100808354040283529160200191610413565b820191906000526020600020905b8154815290600101906020018083116103f657829003601f168201915b505050505090509250925092509193909250565b600054610100900460ff16158080156104475750600054600160ff909116105b806104615750303b158015610461575060005460ff166001145b6104c45760405162461bcd60e51b815260206004820152602e60248201527f496e697469616c697a61626c653a20636f6e747261637420697320616c72656160448201526d191e481a5b9a5d1a585b1a5e995960921b60648201526084016101d5565b6000805460ff1916600117905580156104e7576000805461ff0019166101001790555b606580546001600160a01b0319166001600160a01b0384161790556103e8606655801561054e576000805461ff0019169055604051600181527f7f26b83ff96e1f2b6a682f133852f6798a09c465da95921460cefb38474024989060200160405180910390a15b5050565b61055a6106c3565b6001600160a01b0381166105bf5760405162461bcd60e51b815260206004820152602660248201527f4f776e61626c653a206e6577206f776e657220697320746865207a65726f206160448201526564647265737360d01b60648201526084016101d5565b6105c88161071d565b50565b60408051600481526024810182526020810180516001600160e01b03166311ddc2b160e31b179052905160609160009182916001600160a01b038616916106129190610aed565b600060405180830381855afa9150503d806000811461064d576040519150601f19603f3d011682016040523d82523d6000602084013e610652565b606091505b5091509150816106a45760405162461bcd60e51b815260206004820152601b60248201527f73746174696363616c6c20616c6c6f77616e6365206661696c6564000000000060448201526064016101d5565b6000818060200190518101906106ba9190610b09565b95945050505050565b6033546001600160a01b031633146102d55760405162461bcd60e51b815260206004820181905260248201527f4f776e61626c653a2063616c6c6572206973206e6f7420746865206f776e657260448201526064016101d5565b603380546001600160a01b038381166001600160a01b0319831681179093556040519116919082907f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e090600090a35050565b82805461077b90610ab3565b90600052602060002090601f01602090048101928261079d57600085556107e3565b82601f106107b657805160ff19168380011785556107e3565b828001600101855582156107e3579182015b828111156107e35782518255916020019190600101906107c8565b506107ef9291506107f3565b5090565b5b808211156107ef57600081556001016107f4565b634e487b7160e01b600052604160045260246000fd5b604051601f8201601f1916810167ffffffffffffffff8111828210171561084757610847610808565b604052919050565b600067ffffffffffffffff83111561086957610869610808565b61087c601f8401601f191660200161081e565b905082815283838301111561089057600080fd5b828260208301376000602084830101529392505050565b600080604083850312156108ba57600080fd5b823567ffffffffffffffff808211156108d257600080fd5b818501915085601f8301126108e657600080fd5b6108f58683356020850161084f565b9350602085013591508082111561090b57600080fd5b508301601f8101851361091d57600080fd5b61092c8582356020840161084f565b9150509250929050565b60006020828403121561094857600080fd5b5035919050565b60005b8381101561096a578181015183820152602001610952565b83811115610979576000848401525b50505050565b6000815180845261099781602086016020860161094f565b601f01601f19169290920160200192915050565b6001600160a01b03841681526060602082018190526000906109cf9083018561097f565b82810360408401526102b9818561097f565b6001600160a01b03811681146105c857600080fd5b600060208284031215610a0857600080fd5b8135610a13816109e1565b9392505050565b634e487b7160e01b600052603260045260246000fd5b634e487b7160e01b600052601160045260246000fd5b600060018201610a5857610a58610a30565b5060010190565b604081526000610a72604083018561097f565b82810360208401526106ba818561097f565b60008219821115610a9757610a97610a30565b500190565b600082821015610aae57610aae610a30565b500390565b600181811c90821680610ac757607f821691505b602082108103610ae757634e487b7160e01b600052602260045260246000fd5b50919050565b60008251610aff81846020870161094f565b9190910192915050565b60006020808385031215610b1c57600080fd5b825167ffffffffffffffff80821115610b3457600080fd5b818501915085601f830112610b4857600080fd5b815181811115610b5a57610b5a610808565b8060051b9150610b6b84830161081e565b8181529183018401918481019088841115610b8557600080fd5b938501935b83851015610baf5784519250610b9f836109e1565b8282529385019390850190610b8a565b9897505050505050505056fea26469706673582212205662f25c6800ce1b2ce45ccf5a288d0058c1f1c334445929cc2db81315aa025f64736f6c634300080d0033";

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
