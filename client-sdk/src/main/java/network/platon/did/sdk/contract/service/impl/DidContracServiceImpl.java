package network.platon.did.sdk.contract.service.impl;

import com.platon.crypto.Credentials;
import com.platon.protocol.core.methods.response.TransactionReceipt;
import lombok.extern.slf4j.Slf4j;
import network.platon.did.common.enums.RetEnum;
import network.platon.did.common.utils.DateUtils;
import network.platon.did.contract.Did;
import network.platon.did.contract.dto.ContractNameValues;
import network.platon.did.contract.dto.DeployContractData;
import network.platon.did.contract.dto.TransactionInfo;
import network.platon.did.sdk.base.dto.DocumentData;
import network.platon.did.sdk.constant.DidConst;
import network.platon.did.sdk.constant.commonConstant;
import network.platon.did.sdk.contract.service.ContractService;
import network.platon.did.sdk.contract.service.DidContractService;
import network.platon.did.sdk.contract.service.impl.processor.DidEventProcessor;
import network.platon.did.sdk.enums.DidAttrType;
import network.platon.did.sdk.resp.BaseResp;
import network.platon.did.sdk.resp.TransactionResp;
import network.platon.did.sdk.utils.DidUtils;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
public class DidContracServiceImpl extends ContractService implements DidContractService,Serializable,Cloneable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6963401261601796153L;

	private static DidContracServiceImpl didContracServiceImpl = new DidContracServiceImpl();
	
	public static DidContracServiceImpl getInstance(){
        try {
            return (DidContracServiceImpl) didContracServiceImpl.clone();
        } catch (CloneNotSupportedException e) {
        	log.error("get instance error.", e);
        }
        return new DidContracServiceImpl();
    }
	
	@Override
	public TransactionResp<Boolean> createDid(String did, String publicKey, String publicKeyType)  {
		if (!DidUtils.isValidDid(did)) {
			log.error("Failed to call `createDid()`: the `did` is illegal");
			return TransactionResp.build(RetEnum.RET_DID_INVALID);
		}

		// check the did document is already exist ?
		BaseResp<Boolean> resp = this.isIdentityExist(DidUtils.convertDidToAddressStr(did));
		if (resp.checkFail()){
			return TransactionResp.buildWith(resp.getCode(), resp.getErrMsg());
		}

		if (resp.getData()) {
			log.error("Failed to create did, the did is already exist, did: {}", did);
			return TransactionResp.build(RetEnum.RET_DID_IDENTITY_ALREADY_EXIST);
		}

		String created = DateUtils.convertTimestampToUtc(DateUtils.getCurrentTimeStamp());

		//	function createDid(
		//		string memory createTime,
		//		string memory authentication,
		//		string memory publicKey,
		//		string memory updateTime
		//   ) public returns (bool success)
		TransactionReceipt receipt = null;
		try {
			receipt = this.getDidContract().createDid(
					created,
					buildPublicWithIndex(
							publicKey,
							publicKeyType,
							String.valueOf(1),
							DidConst.DocumentAttrStatus.DID_PUBLICKEY_VALID.getTag()),
					created).send();
		} catch (Exception e) {
			log.error(
					"Failed to create DID, the did: {}, the exception: {}",
					did, e
			);
			return TransactionResp.buildWith(RetEnum.RET_DID_CREATE_DID_ERROR.getCode(),
					"Failed to call contract, the did: " + did + ", the exception: " + e.toString());
		}

		TransactionInfo tx = new TransactionInfo(receipt);

		if (!receipt.isStatusOK()) {
			log.error(
					"Failed to create DID, the tx receipt is null, txHash is {}",
					tx.getTransactionHash()
			);
			return TransactionResp.build(RetEnum.RET_DID_CREATE_DID_ERROR);
		}
		return TransactionResp.buildTxSuccess(true, tx);
	}


	/**
	 * Fetch the DID Document info by DID
	 *
	 * @param identity
	 * @return
	 */
	@Override
	public BaseResp<DocumentData> getDocument(String identity) {

		BigInteger lastBlockNumber = BigInteger.ZERO;
		try {
			BaseResp<BigInteger> blocNumResp = this.getLatestBlockNumber(identity);
			if (blocNumResp.checkFail()){
				return BaseResp.build(blocNumResp.getCode(),
						blocNumResp.getErrMsg());
			}

			lastBlockNumber = blocNumResp.getData();
			if (BigInteger.ZERO.equals(lastBlockNumber)) {
				return BaseResp.build(RetEnum.RET_DID_IDENTITY_NOTEXIST);
			}

			// Start to extract Document Attribute and assemble it into Document Info
			DocumentData doc = DidEventProcessor
					.processBlockReceipt(this.getDidContract(),
							DidUtils.convertAddressStrToDid(identity), lastBlockNumber);

			// Maybe never match this condition
			// The document has at least one publicKey
			if (null == doc || StringUtils.isBlank(doc.getId()) || doc.getPublicKey().size() == 0 ) {
				return BaseResp.build(RetEnum.RET_DID_IDENTITY_NOTEXIST);
			}
			// set Document status
			doc.setStatus(DidConst.DocumentStatus.findStatus(this.getDocumentStatus(identity)).getTag());

			return BaseResp.buildSuccess(doc);
		} catch (Exception e) {
			log.error(
					"Failed to call `getDocument()`, the did: {}, the exception: {}",
					identity.toString(), e);
			return BaseResp.build(RetEnum.RET_DID_QUERY_DOCUMENT_ERROR.getCode(),
					"Failed to query document, the exception: " + e.toString());
		}
	}


	/**
	 * add a valid public key to document, first.
	 *
	 * @param identity
	 * @param index
	 * @param type
	 * @param publicKey
	 * @return
	 */
	@Override
	public TransactionResp<Boolean> addPublicKey(String identity, String publicKey, String type, int index) {

		TransactionReceipt receipt = null;
		try {
			receipt = this.getDidContract().setAttribute(
					DidAttrType.PUBLICKEY.getCode(),
					buildPublicWithIndex(
							publicKey,
							type,
							String.valueOf(index),
							DidConst.DocumentAttrStatus.DID_PUBLICKEY_VALID.getTag()),
					DateUtils.getCurrentTimeStampString()).send();
		} catch (Exception e) {
			log.error(
					"Failed to call add_public_key of DIDContract, the did: {}, the exception: {}",
					identity.toString(), e);
			return TransactionResp.build(RetEnum.RET_DID_IDENTITY_CALL_CONTRACT_ERROR);
		}

		TransactionInfo tx = new TransactionInfo(receipt);

		List<Did.DIDAttributeChangeEventResponse> response =
				this.getDidContract().getDIDAttributeChangeEvents(receipt);

		if (CollectionUtils.isEmpty(response)) {
			log.error(
					"Failed to add public key for DID, the tx receipt is null, txHash is {}",
					tx.getTransactionHash()
			);
			return TransactionResp.build(RetEnum.RET_DID_ADD_PUBLICKEY_ERROR);
		}
		return TransactionResp.buildTxSuccess(true, tx);
	}

	@Override
	public TransactionResp<Boolean> updatePublicKey(String identity, String publicKey, String type, int index) {

		//  document publicKey format in contract:
		//  key: Uint8(2)
		//  value: {publicKey}|{controller}|{type}|{status}|{index}
		TransactionReceipt receipt = null;
		try {
			receipt = this.getDidContract().setAttribute(
					DidAttrType.PUBLICKEY.getCode(),
					buildPublicWithIndex(
							publicKey,
							type,
							String.valueOf(index),
							DidConst.DocumentAttrStatus.DID_PUBLICKEY_VALID.getTag()),
					DateUtils.getCurrentTimeStampString()).send();
		} catch (Exception e) {
			log.error(
					"Failed to call `setAttribute()` of DIDContract, the did: {}, the exception: {}",
					identity.toString(), e);
			return TransactionResp.build(RetEnum.RET_DID_IDENTITY_CALL_CONTRACT_ERROR);
		}

		TransactionInfo tx = new TransactionInfo(receipt);

		List<Did.DIDAttributeChangeEventResponse> response =
				this.getDidContract().getDIDAttributeChangeEvents(receipt);

		if (CollectionUtils.isEmpty(response)) {
			log.error(
					"Failed to update public key for DID, the tx receipt is null, txHash is {}",
					tx.getTransactionHash()
			);
			return TransactionResp.build(RetEnum.RET_DID_UPDATE_PUBLICKEY_ERROR);
		}
		return TransactionResp.buildTxSuccess(true, tx);
	}

	@Override
	public TransactionResp<Boolean> revocationPublicKey(String identity, String publicKey, String type, int index) {

		//  document publicKey format in contract:
		//  key: Uint8(2)
		//  value: {publicKey}|{controller}|{type}|{status}|{index}
		TransactionReceipt receipt = null;
		try {
			receipt = this.getDidContract().setAttribute(
					DidAttrType.PUBLICKEY.getCode(),
					buildPublicWithIndex(
							publicKey,
							type,
							String.valueOf(index),
							DidConst.DocumentAttrStatus.DID_PUBLICKEY_INVALID.getTag()),
					DateUtils.getCurrentTimeStampString()).send();
		} catch (Exception e) {
			log.error(
					"Failed to call `setAttribute()` of DIDContract, the did: {}, the exception: {}",
					identity.toString(), e);
			return TransactionResp.build(RetEnum.RET_DID_IDENTITY_CALL_CONTRACT_ERROR);
		}

		TransactionInfo tx = new TransactionInfo(receipt);

		List<Did.DIDAttributeChangeEventResponse> response =
				this.getDidContract().getDIDAttributeChangeEvents(receipt);

		if (CollectionUtils.isEmpty(response)) {
			log.error(
					"Failed to update public key for DID, the tx receipt is null, txHash is {}",
					tx.getTransactionHash()
			);
			return TransactionResp.build(RetEnum.RET_DID_UPDATE_PUBLICKEY_ERROR);
		}
		return TransactionResp.buildTxSuccess(true, tx);
	}

	@Override
	public TransactionResp<Boolean> setService(String identity, String serviceId, String serviceType, String serviceEndPoint, DidConst.DocumentAttrStatus status) {
		//  document authentication format in contract:
		//  key: Uint8(3)
		//  value: {id}|{type}|{endPoint}|{status}
		TransactionReceipt receipt = null;
		try {
			receipt = this.getDidContract().setAttribute(
					DidAttrType.SERVICE.getCode(),
					buildService(
							serviceId,
							serviceType,
							serviceEndPoint,
							status.getTag()),
					DateUtils.getCurrentTimeStampString()).send();
		} catch (Exception e) {
			log.error(
					"Failed to call `setService()` of DIDContract, the did: {}, the exception: {}",
					identity.toString(), e
			);
			return TransactionResp.build(RetEnum.RET_DID_IDENTITY_CALL_CONTRACT_ERROR);
		}

		TransactionInfo tx = new TransactionInfo(receipt);

		List<Did.DIDAttributeChangeEventResponse> response =
				this.getDidContract().getDIDAttributeChangeEvents(receipt);

		if (CollectionUtils.isEmpty(response)) {
			log.error(
					"Failed to set service for DID, the tx receipt is null, txHash is {}",
					receipt.getTransactionHash()
			);
			return TransactionResp.build(RetEnum.RET_DID_SET_SERVICE_ALREADY_EXIST);
		}
		return TransactionResp.buildTxSuccess(true, tx);
	}


	public TransactionResp<Boolean> changeStatus(String identity, BigInteger status) {

		BaseResp<Boolean> resp = this.isIdentityExist(identity);
		if (resp.checkFail()) {
			return TransactionResp.buildWith(resp.getCode(),
					resp.getErrMsg());
		}
		if (!resp.getData()) {
			return TransactionResp.build(RetEnum.RET_DID_IDENTITY_NOTEXIST);
		}

		TransactionReceipt receipt = null;
		try {
			receipt = this.getDidContract().changeStatus(status).send();
		} catch (Exception e) {
			log.error(
					"Failed to call `changeStatus()` of DIDContract, the did: {}, the exception: {}",
					identity.toString(), e
			);
			return TransactionResp.build(RetEnum.RET_DID_IDENTITY_CALL_CONTRACT_ERROR);
		}

		TransactionInfo tx = new TransactionInfo(receipt);
		
		BaseResp<DidConst.DocumentStatus> statusResp = this.getStatus(identity);
		if (statusResp.checkFail()) {
			return TransactionResp.buildWith(statusResp.getCode(),
					statusResp.getErrMsg());
		}
		if (!statusResp.getData().getCode().equals(status)) {
			log.error(
					"Failed to change status, The value of document status in the contract has not been changed, txHash: {}, the oldStatus: {}, expectedStaus: {}",
					tx.getTransactionHash(), statusResp.getData().getCode(), status
			);
			return TransactionResp.build(RetEnum.RET_DID_SET_STATUS_ERROR);
		}
		return TransactionResp.buildTxSuccess(true, tx);
	}

	public BaseResp<DidConst.DocumentStatus> getStatus(String identity) {
		BaseResp<Boolean> resp = this.isIdentityExist(identity);
		if (resp.checkFail()) {
			return BaseResp.build(resp.getCode(), resp.getErrMsg());
		}
		if (!resp.getData()) {
			return TransactionResp.build(RetEnum.RET_DID_IDENTITY_NOTEXIST);
		}
		BigInteger status = BigInteger.valueOf(0);
		try {
			status = this.getDocumentStatus(identity);
		} catch (Exception e) {
			log.error(
					"Failed to call `get_status()` of DIDContract, the did: {}, the exception: {}",
					identity.toString(), e
			);
			return BaseResp.build(RetEnum.RET_DID_IDENTITY_CALL_CONTRACT_ERROR);
		}

		DidConst.DocumentStatus documentStatus = DidConst.DocumentStatus.findStatus(status);
		return BaseResp.buildSuccess(documentStatus);
	}
	private BigInteger getDocumentStatus(String identity) throws Exception {
		return this.getDidContract().getStatus(identity).send();
	}


	/**
	 * Determine whether the Document information corresponding to the current DID exists
	 *
	 * @param identity
	 */
	@Override
	public BaseResp<Boolean> isIdentityExist(String identity) {
		Boolean isExist = false;
		try {
			isExist = this.getDidContract().isIdentityExist(identity).send();
		} catch (Exception e) {
			log.error(
					"Failed to call `isIdentityExist()` on DidContract: the exception: {}",
					e
			);
			return BaseResp.build(RetEnum.RET_DID_IDENTITY_CALL_CONTRACT_ERROR);
		}
		return BaseResp.buildSuccess(isExist);
	}

	/**
	 * verify the identity is valid ?
	 * @param identity
	 * @return
	 */
	public BaseResp<Boolean> isValidIdentity(String identity) {
		try {
			Boolean isExist = this.getDidContract().isIdentityExist(identity).send();

			if (!isExist) {
				return BaseResp.build(RetEnum.RET_DID_IDENTITY_NOTEXIST);
			}

			BigInteger status = this.getDocumentStatus(identity);
			if (DidConst.DocumentStatus.DEACTIVATION.getCode().equals(status)) {
				return BaseResp.build(RetEnum.RET_DID_IDENTITY_ALREADY_REVOCATED);
			}
		} catch (Exception e) {
			log.error(
					"Failed to call DidContract on `isValidIdentity()`: the exception: {}",
					e
			);
			return BaseResp.build(RetEnum.RET_DID_IDENTITY_CALL_CONTRACT_ERROR);
		}
		return BaseResp.buildSuccess(true);
	}


	/**
	 * Get the blockNumber of the last changed PlatON Identity atrribute
	 * @param identity
	 * @return
	 */
	public BaseResp<BigInteger> getLatestBlockNumber(String identity) {

		BigInteger latestBlockNum = BigInteger.valueOf(0);
		try {
			latestBlockNum = this.getDidContract().getLatestBlock(identity).send();
		} catch (Exception e) {
			log.error(
					"Failed to call `getLatestBlock()` of DIDContract, the exception: {}",
					e
			);
			return BaseResp.build(RetEnum.RET_DID_IDENTITY_CALL_CONTRACT_ERROR);
		}
		return BaseResp.buildSuccess(latestBlockNum);
	}


	public TransactionResp<List<DeployContractData>> deployContract(Credentials credentials) {
		try {
			Did didContract = Did.deploy(getWeb3j(), credentials, gasProvider).send();
			Optional<TransactionReceipt> value = didContract.getTransactionReceipt();
			String didContractTransHash = "";
			if(value.isPresent()){
				didContractTransHash = value.get().getTransactionHash();
			}
			DeployContractData deployContractData = new DeployContractData(ContractNameValues.DID
					,didContract.getContractAddress(),didContractTransHash);
			deployContractData.setContractAddress(didContract.getContractAddress());
			
			List<DeployContractData> lists = Arrays.asList(deployContractData);
			return TransactionResp.buildSuccess(lists);
		} catch (Exception e) {
			log.error("deployContract CredentialContract error", e);
			return TransactionResp.build(RetEnum.RET_DEPLOY_CONTRACT_ERROR, e.getMessage());
		}
	}

	/**
	 * build the publicKey value on contract
	 *
	 * rule:
	 * 		{publicKey}|{controller}|{type}|{status}|{index}
	 * @param publicKey
	 * @param typeName
	 * @param status
	 * @param index
	 * @return
	 */
	public String buildPublicWithIndex(String publicKey, String typeName, String index, String status) {
		String keyStr = new StringBuilder()
				.append(publicKey)
				.append(commonConstant.SEPARATOR_PIPELINE)
				.append(typeName)
				.append(commonConstant.SEPARATOR_PIPELINE)
				.append(index)
				.append(commonConstant.SEPARATOR_PIPELINE)
				.append(status)
				.toString();
		return keyStr;
	}

	/**
	 * build the authentication value on contract
	 *
	 * rule:
	 * 		{serviceId}|{serviceType}|{serviceEndPoint}|{status}
	 * @param id
	 * @param type
	 * @param endPoint
	 * @param status
	 * @return
	 */
	public String buildService(String id, String type, String endPoint, String status) {
		String keyStr = new StringBuilder()
				.append(id)
				.append(commonConstant.SEPARATOR_PIPELINE)
				.append(type)
				.append(commonConstant.SEPARATOR_PIPELINE)
				.append(endPoint)
				.append(commonConstant.SEPARATOR_PIPELINE)
				.append(status)
				.toString();
		return keyStr;
	}
}
