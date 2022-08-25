package network.platon.did.sdk.base.dto;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Data;
import network.platon.did.common.constant.ClaimMetaKey;
import network.platon.did.common.constant.VpOrVcPoofKey;
import network.platon.did.csies.utils.ConverDataUtils;
import network.platon.did.csies.utils.Sha256;
import network.platon.did.sdk.annoation.CustomNotBlank;
import network.platon.did.sdk.annoation.CustomSize;
import network.platon.did.sdk.constant.DidConst;
import network.platon.did.sdk.constant.ReqAnnoationArgs;
import network.platon.did.sdk.utils.CredentialsUtils;

/**
 * @Description: The base data structure of Credential info
 * @Author: Gavin
 * @Date: 2020-06-03 16:36
 */
@Data
public class Credential implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Required: The @context.
	 */
	@JSONField(name="@context")
	private String context;

	/**
	 * Required: The credentialId that is an UUID.
	 */
	private String id;

	/**
	 * Required: The type list of Credential.
	 */
	private List<String> type = new ArrayList<>();

	/**
	 * Required: The issuer PlatON DID.
	 */
	@CustomSize(min = ReqAnnoationArgs.DID_SIZE_MIN, max = ReqAnnoationArgs.DID_SIZE_MAX)
	private String issuer;

	/**
	 * Required: The create date.
	 */
	@CustomNotBlank
	private String issuanceDate;

	/**
	 * Required: The expire date.
	 */
	@CustomNotBlank
	private String expirationDate;

	/**
	 * Required: The holder PlatON DID.
	 */
	@CustomSize(min = ReqAnnoationArgs.DID_SIZE_MIN, max = ReqAnnoationArgs.DID_SIZE_MAX)
	private String holder;

	/**
	 * Required: The version of credential.
	 */
	private String version;

	/**
	 * Required: The required information in Claim.
	 *
	 * example:
	 *
	 * {
	 *   "pctId": 110,
	 *   "currentStatus": "Revoked",
	 *   "statusReason": "Disciplinary action"
	 * }
	 */
	@CustomSize(min = ReqAnnoationArgs.COMMON_DATA_SIZE)
	private Map<String, Object> claimMeta;

	/**
	 * Required: The main part of the claim.
	 * 			 Its content format is diversified and is constrained by the Claim template corresponding to ClaimMeta's pctId
	 *
	 * example:
	 *
	 * {
	 *    "name": "gavin",
	 *    "gender":"male",
	 *    "birth":"1989-06-06-18T21:19:10Z",
	 *    "address" : "haikou",
	 *    "MonthlySalary": 3000.00
	 * }
	 *
	 */
	@CustomSize(min = ReqAnnoationArgs.COMMON_DATA_SIZE)
	private Map<String, Object> claimData;

	/**
	 * Required: The create date.
	 */
	@CustomSize(min = ReqAnnoationArgs.COMMON_DATA_SIZE)
	private Map<String, Object> proof;
	
	
	@SuppressWarnings("unchecked")
	public String obtainHash() {
        Map<String, Object> salt = this.obtainSalt();
        Object disObject  = this.proof.get(VpOrVcPoofKey.PROOF_DISCLOSURES);
        Map<String, Object> disclosures = null;
        if(disObject != null && disObject instanceof Map) {
			disclosures = (Map<String, Object>) this.proof.get(VpOrVcPoofKey.PROOF_DISCLOSURES);
        }
        Credential credential = ConverDataUtils.clone(this);
        credential.setProof(null);
        return CredentialsUtils.getCredentialHash(credential, salt, disclosures);
    }

	@SuppressWarnings("unchecked")
	public String obtainRawData() {
		Map<String, Object> salt = this.obtainSalt();
		Object disObject  = this.proof.get(VpOrVcPoofKey.PROOF_DISCLOSURES);
		Map<String, Object> disclosures = null;
		if(disObject != null && disObject instanceof Map) {
			disclosures = (Map<String, Object>) this.proof.get(VpOrVcPoofKey.PROOF_DISCLOSURES);
		}
		Credential credential = ConverDataUtils.clone(this);
		credential.setProof(null);

		return CredentialsUtils.getCredentialData(credential, salt, disclosures);

	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> obtainSalt() {
		BigInteger seedUint64 = new BigInteger(String.valueOf(this.proof.get(VpOrVcPoofKey.PROOF_SEED)));
		byte[] seed = Sha256.uint64ToByte(seedUint64);
		HashMap<String, Object> saltMap = ConverDataUtils.clone((HashMap<String, Object>)this.claimData);
		CredentialsUtils.generateSalt(saltMap, seed);
		return saltMap;
	}
	
	public String obtainPctId() {
		return (String) this.getClaimMeta().get(ClaimMetaKey.PCTID);
	}

	public String obtainPublickeyId() {
		return (String) this.getProof().get(VpOrVcPoofKey.PROOF_VERIFICATIONMETHOD);
	}

	public String obtainSign() {
		return (String) this.getProof().get(VpOrVcPoofKey.PROOF_JWS);
	}
}
