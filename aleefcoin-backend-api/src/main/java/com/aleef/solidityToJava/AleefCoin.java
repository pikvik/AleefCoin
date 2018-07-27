package com.aleef.solidityToJava;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.EventValues;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import rx.Observable;
import rx.functions.Func1;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.1.1.
 */
public final class AleefCoin extends Contract {
    private static final String BINARY = "60606040526003805460ff1916600417905560006006819055600755341561002657600080fd5b604051610f02380380610f028339810160405280805182019190602001805182019190602001805160008054600160a060020a03191633600160a060020a0316908117825560035460ff16600a0a8302600481905590825260086020526040909120559150600190508380516100a09291602001906100c3565b5060028280516100b49291602001906100c3565b505060006005555061015e9050565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061010457805160ff1916838001178555610131565b82800160010185558215610131579182015b82811115610131578251825591602001919060010190610116565b5061013d929150610141565b5090565b61015b91905b8082111561013d5760008155600101610147565b90565b610d958061016d6000396000f3006060604052600436106101115763ffffffff7c010000000000000000000000000000000000000000000000000000000060003504166306fdde038114610113578063095ea7b31461019d57806318160ddd146101d357806323b872dd146101f857806324bce60c14610220578063313ce567146102425780634123a0ac1461026b57806342966c681461027e5780635a2bcc18146102945780636769d1f9146102a757806370a08231146102ba57806379c65068146102d95780637b46b80b146102fb578063873550841461031d5780638da5cb5b1461033f57806395d89b411461036e578063a9059cbb14610381578063cd4217c1146103a3578063dd62ed3e146103c2578063f2fde38b146103e7575b005b341561011e57600080fd5b610126610406565b60405160208082528190810183818151815260200191508051906020019080838360005b8381101561016257808201518382015260200161014a565b50505050905090810190601f16801561018f5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34156101a857600080fd5b6101bf600160a060020a03600435166024356104a4565b604051901515815260200160405180910390f35b34156101de57600080fd5b6101e66104e3565b60405190815260200160405180910390f35b341561020357600080fd5b6101bf600160a060020a03600435811690602435166044356104e9565b341561022b57600080fd5b6101bf600160a060020a0360043516602435610684565b341561024d57600080fd5b610255610780565b60405160ff909116815260200160405180910390f35b341561027657600080fd5b6101e6610789565b341561028957600080fd5b6101bf60043561078f565b341561029f57600080fd5b6101e661085b565b34156102b257600080fd5b6101e6610861565b34156102c557600080fd5b6101e6600160a060020a0360043516610867565b34156102e457600080fd5b610111600160a060020a0360043516602435610879565b341561030657600080fd5b6101bf600160a060020a0360043516602435610923565b341561032857600080fd5b610111600160a060020a0360043516602435610a1f565b341561034a57600080fd5b610352610aee565b604051600160a060020a03909116815260200160405180910390f35b341561037957600080fd5b610126610afd565b341561038c57600080fd5b610111600160a060020a0360043516602435610b68565b34156103ae57600080fd5b6101e6600160a060020a0360043516610c7b565b34156103cd57600080fd5b6101e6600160a060020a0360043581169060243516610c8d565b34156103f257600080fd5b610111600160a060020a0360043516610cb8565b60018054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001816001161561010002031660029004801561049c5780601f106104715761010080835404028352916020019161049c565b820191906000526020600020905b81548152906001019060200180831161047f57829003601f168201915b505050505081565b60008082116104b257600080fd5b50600160a060020a033381166000908152600a60209081526040808320938616835292905220819055600192915050565b60045481565b6000600160a060020a038316151561050057600080fd5b6000821161050d57600080fd5b600160a060020a0384166000908152600860205260409020548290101561053357600080fd5b600160a060020a038316600090815260086020526040902054828101101561055a57600080fd5b600160a060020a038085166000908152600a60209081526040808320339094168352929052205482111561058d57600080fd5b600160a060020a0384166000908152600860205260409020546105b09083610d02565b600160a060020a0380861660009081526008602052604080822093909355908516815220546105df9083610d16565b600160a060020a038085166000908152600860209081526040808320949094558783168252600a81528382203390931682529190915220546106219083610d02565b600160a060020a038086166000818152600a6020908152604080832033861684529091529081902093909355600580548601905590851691600080516020610d4a8339815191529085905190815260200160405180910390a35060019392505050565b6000805433600160a060020a039081169116146106a057600080fd5b600160a060020a038316600090815260086020526040902054829010156106c657600080fd5b600082116106d357600080fd5b600160a060020a0383166000908152600860205260409020546106f69083610d02565b600160a060020a0384166000908152600860209081526040808320939093556009905220546107259083610d16565b600160a060020a0384166000818152600960205260409081902092909255907ff97a274face0b5517365ad396b1fdba6f68bd3135ef603e44272adba3af5a1e09084905190815260200160405180910390a250600192915050565b60035460ff1681565b60075481565b600160a060020a033316600090815260086020526040812054829010156107b557600080fd5b600082116107c257600080fd5b600160a060020a0333166000908152600860205260409020546107e59083610d02565b600160a060020a03331660009081526008602052604090205560045461080b9083610d02565b6004556007805483019055600160a060020a0333167fcc16f5dbb4873280815c1ee09dbd06736cffcc184412cf7a71a0fdb75d397ca58360405190815260200160405180910390a2506001919050565b60065481565b60055481565b60086020526000908152604090205481565b60005433600160a060020a0390811691161461089457600080fd5b600160a060020a03808316600090815260086020526040808220805485019055600480548501905560068054850190553090921691600080516020610d4a8339815191529084905190815260200160405180910390a381600160a060020a031630600160a060020a0316600080516020610d4a8339815191528360405190815260200160405180910390a35050565b6000805433600160a060020a0390811691161461093f57600080fd5b600160a060020a0383166000908152600960205260409020548290101561096557600080fd5b6000821161097257600080fd5b600160a060020a0383166000908152600960205260409020546109959083610d02565b600160a060020a0384166000908152600960209081526040808320939093556008905220546109c49083610d16565b600160a060020a0384166000818152600860205260409081902092909255907f2cfce4af01bcb9d6cf6c84ee1b7c491100b8695368264146a94d71e10a63083f9084905190815260200160405180910390a250600192915050565b600160a060020a03331660009081526008602052604081205411610a4257600080fd5b600160a060020a03331660009081526008602052604090205481901015610a6857600080fd5b600160a060020a0382166000908152600860205260409020548181011015610a8f57600080fd5b600160a060020a033381166000818152600860205260408082208054869003905592851680825290839020805485019055600580548501905591600080516020610d4a8339815191529084905190815260200160405180910390a35050565b600054600160a060020a031681565b60028054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001816001161561010002031660029004801561049c5780601f106104715761010080835404028352916020019161049c565b600160a060020a0382161515610b7d57600080fd5b60008111610b8a57600080fd5b600160a060020a03331660009081526008602052604090205481901015610bb057600080fd5b600160a060020a0382166000908152600860205260409020548181011015610bd757600080fd5b600160a060020a033316600090815260086020526040902054610bfa9082610d02565b600160a060020a033381166000908152600860205260408082209390935590841681522054610c299082610d16565b600160a060020a0380841660008181526008602052604090819020939093556005805485019055913390911690600080516020610d4a8339815191529084905190815260200160405180910390a35050565b60096020526000908152604090205481565b600160a060020a039182166000908152600a6020908152604080832093909416825291909152205490565b60005433600160a060020a03908116911614610cd357600080fd5b6000805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a0392909216919091179055565b6000610d1083831115610d3a565b50900390565b6000828201610d33848210801590610d2e5750838210155b610d3a565b9392505050565b801515610d4657600080fd5b505600ddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3efa165627a7a72305820e82bf8af660938db007f7381b18e74f6888cedf7aed409cf5c6d37f9507e6b800029";

    private AleefCoin(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    private AleefCoin(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public List<TransferEventResponse> getTransferEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("Transfer", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<TransferEventResponse> responses = new ArrayList<TransferEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            TransferEventResponse typedResponse = new TransferEventResponse();
            typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<TransferEventResponse> transferEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("Transfer", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, TransferEventResponse>() {
            @Override
            public TransferEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                TransferEventResponse typedResponse = new TransferEventResponse();
                typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public List<BurnEventResponse> getBurnEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("Burn", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<BurnEventResponse> responses = new ArrayList<BurnEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            BurnEventResponse typedResponse = new BurnEventResponse();
            typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<BurnEventResponse> burnEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("Burn", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, BurnEventResponse>() {
            @Override
            public BurnEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                BurnEventResponse typedResponse = new BurnEventResponse();
                typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public List<FreezeEventResponse> getFreezeEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("Freeze", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<FreezeEventResponse> responses = new ArrayList<FreezeEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            FreezeEventResponse typedResponse = new FreezeEventResponse();
            typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<FreezeEventResponse> freezeEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("Freeze", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, FreezeEventResponse>() {
            @Override
            public FreezeEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                FreezeEventResponse typedResponse = new FreezeEventResponse();
                typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public List<UnfreezeEventResponse> getUnfreezeEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("Unfreeze", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<UnfreezeEventResponse> responses = new ArrayList<UnfreezeEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            UnfreezeEventResponse typedResponse = new UnfreezeEventResponse();
            typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<UnfreezeEventResponse> unfreezeEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("Unfreeze", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, UnfreezeEventResponse>() {
            @Override
            public UnfreezeEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                UnfreezeEventResponse typedResponse = new UnfreezeEventResponse();
                typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public List<ApprovalEventResponse> getApprovalEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("Approval", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<ApprovalEventResponse> responses = new ArrayList<ApprovalEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            ApprovalEventResponse typedResponse = new ApprovalEventResponse();
            typedResponse._owner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._spender = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<ApprovalEventResponse> approvalEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("Approval", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, ApprovalEventResponse>() {
            @Override
            public ApprovalEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                ApprovalEventResponse typedResponse = new ApprovalEventResponse();
                typedResponse._owner = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._spender = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    @SuppressWarnings("rawtypes")
	public RemoteCall<String> name() {
        Function function = new Function("name", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    @SuppressWarnings("rawtypes")
	public RemoteCall<TransactionReceipt> approve(String _spender, BigInteger _value) {
        Function function = new Function(
                "approve", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_spender), 
                new org.web3j.abi.datatypes.generated.Uint256(_value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @SuppressWarnings("rawtypes")
	public RemoteCall<BigInteger> totalSupply() {
        Function function = new Function("totalSupply", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    @SuppressWarnings("rawtypes")
	public RemoteCall<TransactionReceipt> transferFrom(String _from, String _to, BigInteger _value) {
        Function function = new Function(
                "transferFrom", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_from), 
                new org.web3j.abi.datatypes.Address(_to), 
                new org.web3j.abi.datatypes.generated.Uint256(_value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @SuppressWarnings("rawtypes")
	public RemoteCall<TransactionReceipt> freeze(String _freeze, BigInteger _value) {
        Function function = new Function(
                "freeze", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_freeze), 
                new org.web3j.abi.datatypes.generated.Uint256(_value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @SuppressWarnings("rawtypes")
	public RemoteCall<BigInteger> decimals() {
        Function function = new Function("decimals", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    @SuppressWarnings("rawtypes")
	public RemoteCall<BigInteger> deleteToken() {
        Function function = new Function("deleteToken", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    @SuppressWarnings("rawtypes")
	public RemoteCall<TransactionReceipt> burn(BigInteger _value) {
        Function function = new Function(
                "burn", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @SuppressWarnings("rawtypes")
	public RemoteCall<BigInteger> mintAmount() {
        Function function = new Function("mintAmount", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    @SuppressWarnings("rawtypes")
	public RemoteCall<BigInteger> soldToken() {
        Function function = new Function("soldToken", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    @SuppressWarnings("rawtypes")
	public RemoteCall<BigInteger> balanceOf(String param0) {
        Function function = new Function("balanceOf", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    @SuppressWarnings("rawtypes")
	public RemoteCall<TransactionReceipt> mintToken(String target, BigInteger mintedAmount) {
        Function function = new Function(
                "mintToken", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(target), 
                new org.web3j.abi.datatypes.generated.Uint256(mintedAmount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @SuppressWarnings("rawtypes")
	public RemoteCall<TransactionReceipt> unfreeze(String _unfreeze, BigInteger _value) {
        Function function = new Function(
                "unfreeze", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_unfreeze), 
                new org.web3j.abi.datatypes.generated.Uint256(_value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @SuppressWarnings("rawtypes")
	public RemoteCall<TransactionReceipt> transferCrowdsale(String to, BigInteger value) {
        Function function = new Function(
                "transferCrowdsale", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(to), 
                new org.web3j.abi.datatypes.generated.Uint256(value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @SuppressWarnings("rawtypes")
	public RemoteCall<String> owner() {
        Function function = new Function("owner", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    @SuppressWarnings("rawtypes")
	public RemoteCall<String> symbol() {
        Function function = new Function("symbol", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    @SuppressWarnings("rawtypes")
	public RemoteCall<TransactionReceipt> transfer(String _to, BigInteger _value) {
        Function function = new Function(
                "transfer", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_to), 
                new org.web3j.abi.datatypes.generated.Uint256(_value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @SuppressWarnings("rawtypes")
	public RemoteCall<BigInteger> freezeOf(String param0) {
        Function function = new Function("freezeOf", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    @SuppressWarnings("rawtypes")
	public RemoteCall<BigInteger> allowance(String owner, String spender) {
        Function function = new Function("allowance", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(owner), 
                new org.web3j.abi.datatypes.Address(spender)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    @SuppressWarnings("rawtypes")
	public RemoteCall<TransactionReceipt> transferOwnership(String newOwner) {
        Function function = new Function(
                "transferOwnership", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(newOwner)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @SuppressWarnings("rawtypes")
	public static RemoteCall<AleefCoin> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String coinName, String coinSymbol, BigInteger initialSupply) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(coinName), 
                new org.web3j.abi.datatypes.Utf8String(coinSymbol), 
                new org.web3j.abi.datatypes.generated.Uint256(initialSupply)));
        return deployRemoteCall(AleefCoin.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    @SuppressWarnings("rawtypes")
	public static RemoteCall<AleefCoin> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String coinName, String coinSymbol, BigInteger initialSupply) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(coinName), 
                new org.web3j.abi.datatypes.Utf8String(coinSymbol), 
                new org.web3j.abi.datatypes.generated.Uint256(initialSupply)));
        return deployRemoteCall(AleefCoin.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static AleefCoin load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new AleefCoin(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static AleefCoin load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new AleefCoin(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class TransferEventResponse {
        public String from;

        public String to;

        public BigInteger value;
    }

    public static class BurnEventResponse {
        public String from;

        public BigInteger value;
    }

    public static class FreezeEventResponse {
        public String from;

        public BigInteger value;
    }

    public static class UnfreezeEventResponse {
        public String from;

        public BigInteger value;
    }

    public static class ApprovalEventResponse {
        public String _owner;

        public String _spender;

        public BigInteger _value;
    }
}
