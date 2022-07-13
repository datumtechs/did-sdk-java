package network.platon.pid.sdk.contract.service;
import network.platon.pid.sdk.base.dto.DocumentData;
import network.platon.pid.sdk.constant.PidConst;
import network.platon.pid.sdk.resp.BaseResp;
import network.platon.pid.sdk.resp.TransactionResp;

import java.math.BigInteger;

public interface PidContractService {


	TransactionResp<Boolean> createPid(String pid, String publicKey);

	BaseResp<DocumentData> getDocument(String identity);

	TransactionResp<Boolean> addPublicKey(String identity, String controllerIdentity, String type, String publicKey);

	TransactionResp<Boolean> updatePublicKey(String identity, Integer index, String controllerIdentity, String type, String publicKey, PidConst.DocumentAttrStatus status);

	TransactionResp<Boolean> setAuthentication(String identity, String controllerIdentity, String publicKey, PidConst.DocumentAttrStatus status);

	TransactionResp<Boolean> setService(String identity, String serviceId, String serviceType, String serviceEndPoint, PidConst.DocumentAttrStatus status);

	TransactionResp<Boolean> changeStatus(String identity, BigInteger status);

	BaseResp<PidConst.DocumentStatus> getStatus(String identity);

	BaseResp<Boolean> isIdentityExist(String identity);

	BaseResp<Boolean> isValidIdentity(String identity);

}
