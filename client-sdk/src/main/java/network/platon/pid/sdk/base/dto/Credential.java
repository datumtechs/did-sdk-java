package network.platon.pid.sdk.base.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Data;
import network.platon.pid.common.constant.ClaimMetaKey;
import network.platon.pid.common.constant.VpOrVcPoofKey;
import network.platon.pid.csies.utils.ConverDataUtils;
import network.platon.pid.sdk.annoation.CustomNotBlank;
import network.platon.pid.sdk.annoation.CustomSize;
import network.platon.pid.sdk.constant.ReqAnnoationArgs;
import network.platon.pid.sdk.utils.CredentialsUtils;

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
	@CustomSize(min = ReqAnnoationArgs.PID_SIZE_MIN, max = ReqAnnoationArgs.PID_SIZE_MAX)
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
	@CustomSize(min = ReqAnnoationArgs.PID_SIZE_MIN, max = ReqAnnoationArgs.PID_SIZE_MAX)
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

//	/**
//	 * Required: The create date.
//	 */
//	private Revocation revocation;

	/**
	 * Required: The create date.
	 */
	@CustomSize(min = ReqAnnoationArgs.COMMON_DATA_SIZE)
	private Map<String, Object> proof;
	
	
	@SuppressWarnings("unchecked")
	public String obtainHash() {
        Map<String, Object> salt = this.obtainSalt();
        Object disObject  = proof.get(VpOrVcPoofKey.PROOF_DISCLOSURES);
        Map<String, Object> disclosures = null;
        if(disObject != null && disObject instanceof Map) {
			disclosures = (Map<String, Object>) proof.get(VpOrVcPoofKey.PROOF_DISCLOSURES);
        }
        Credential credential = ConverDataUtils.clone(this);
        credential.setProof(null);
        return CredentialsUtils.getCredentialHash(credential, salt, disclosures);
    }
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> obtainSalt() {
		return (Map<String, Object>) this.getProof().get(VpOrVcPoofKey.PROOF_SALT);
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
