package network.platon.did.sdk.contract.service;
import network.platon.did.sdk.base.dto.DocumentData;
import network.platon.did.sdk.constant.DidConst;
import network.platon.did.sdk.resp.BaseResp;
import network.platon.did.sdk.resp.TransactionResp;

import java.math.BigInteger;

public interface DidContractService {


	TransactionResp<Boolean> createDid(String did, String publicKey, String publicKeyType);

	BaseResp<DocumentData> getDocument(String identity);

	TransactionResp<Boolean> addPublicKey(String identity, String publicKey, String type, int index);

	TransactionResp<Boolean> updatePublicKey(String identity, String publicKey, String type, int index);

	TransactionResp<Boolean> revocationPublicKey(String identity, String publicKey, String type, int index);

	TransactionResp<Boolean> setService(String identity, String serviceId, String serviceType, String serviceEndPoint, DidConst.DocumentAttrStatus status);

	TransactionResp<Boolean> changeStatus(String identity, BigInteger status);

	BaseResp<DidConst.DocumentStatus> getStatus(String identity);

	BaseResp<Boolean> isIdentityExist(String identity);

	BaseResp<Boolean> isValidIdentity(String identity);

}
