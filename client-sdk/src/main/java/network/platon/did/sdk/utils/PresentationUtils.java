package network.platon.did.sdk.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import network.platon.did.sdk.constant.DidConst;
import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;
import network.platon.did.common.constant.ClaimMetaKey;
import network.platon.did.common.enums.RetEnum;
import network.platon.did.csies.utils.ConverDataUtils;
import network.platon.did.sdk.base.dto.ClaimPolicy;
import network.platon.did.sdk.base.dto.Credential;
import network.platon.did.sdk.enums.CredentialDisclosedValue;

@Slf4j
@SuppressWarnings({ "rawtypes", "unchecked" })
public class PresentationUtils {

	/**
	 * Add missing key to policy
	 * @param disclosureMap
	 * @param claimMap
	 */
	public static void addKeyToPolicy(Map<String, Object> disclosureMap, Map<String, Object> claimMap) {
		for (Map.Entry<String, Object> entry : claimMap.entrySet()) {
			String claimK = entry.getKey();
			Object claimV = entry.getValue();
			if (claimV instanceof Map) {
				HashMap claimHashMap = (HashMap) claimV;
				if (!disclosureMap.containsKey(claimK)) {
					disclosureMap.put(claimK, new HashMap());
				}
				HashMap disclosureHashMap = (HashMap) disclosureMap.get(claimK);
				addKeyToPolicy(disclosureHashMap, claimHashMap);
			} else if (claimV instanceof List) {
				ArrayList claimList = (ArrayList) claimV;
				// Determine whether claimList contains a Map structure or a single structure
				boolean isSampleList = isSampleListForClaim(claimList);
				if (isSampleList) {
					if (!disclosureMap.containsKey(claimK)) {
						disclosureMap.put(claimK, CredentialDisclosedValue.NOT_DISCLOSED.getStatus());
					}
				} else {
					if (!disclosureMap.containsKey(claimK)) {
						disclosureMap.put(claimK, new ArrayList());
					}
					ArrayList disclosureList = (ArrayList) disclosureMap.get(claimK);
					addKeyToPolicyList(disclosureList, claimList);
				}
			} else {
				if (!disclosureMap.containsKey(claimK)) {
					disclosureMap.put(claimK, CredentialDisclosedValue.NOT_DISCLOSED.getStatus());
				}
			}
		}
	}

	/**
	 * Add key to the plocy of the list structure
	 * @param disclosureList
	 * @param claimList
	 */
	private static void addKeyToPolicyList(ArrayList disclosureList, ArrayList claimList) {
		for (int i = 0; i < claimList.size(); i++) {
			Object claimObj = claimList.get(i);
			if (claimObj instanceof Map) {
				Object disclosureObj = disclosureList.size() == 0 ? null : disclosureList.get(0);
				if (disclosureObj == null) {
					disclosureList.add(new HashMap());
				}
				HashMap disclosureHashMap = (HashMap) disclosureList.get(0);
				addKeyToPolicy(disclosureHashMap, (HashMap) claimObj);
				break;
			} else if (claimObj instanceof List) {
				Object disclosureObj = disclosureList.get(i);
				if (disclosureObj == null) {
					disclosureList.add(new ArrayList());
				}
				ArrayList disclosureArrayList = (ArrayList) disclosureList.get(i);
				addKeyToPolicyList(disclosureArrayList, (ArrayList) claimObj);
			}
		}
	}

	/**
	 * Determine the basic structure of the list, whether it has a map object or a list object
	 * @param claimList
	 * @return
	 */
	private static boolean isSampleListForClaim(ArrayList claimList) {
		if (claimList == null || claimList.isEmpty()) {
			return true;
		}
		Object claimObj = claimList.get(0);
		if (claimObj instanceof Map) {
			return false;
		}
		if (claimObj instanceof List) {
			return isSampleListForClaim((ArrayList) claimObj);
		}
		return true;
	}

	/**
	 * Salting the selectively disclosed fields
	 * @param disclosureMap
	 * @param saltMap
	 * @param claim
	 */
	public static void addSelectSalt(Map<String, Object> disclosureMap, Map<String, Object> saltMap,
			Map<String, Object> claim) {
		for (Map.Entry<String, Object> entry : disclosureMap.entrySet()) {
			String disclosureKey = entry.getKey();
			Object value = entry.getValue();
			Object saltV = saltMap.get(disclosureKey);
			Object claimV = claim.get(disclosureKey);
			if (value == null) {
				log.error("disclosureMap value is null.");
				return;
			} else if ((value instanceof Map) && (claimV instanceof Map)) {
				addSelectSalt((HashMap) value, (HashMap) saltV, (HashMap) claimV);
			} else if (value instanceof List) {
				addSaltForList((ArrayList<Object>) value, (ArrayList<Object>) saltV, (ArrayList<Object>) claimV);
			} else {
				addHashToClaim(saltMap, claim, disclosureKey, value, saltV, claimV);
			}
		}
	}

	/**
	 * Process the fields selectively disclosed in the claim and backfill the hash value
	 * @param saltMap
	 * @param claim
	 * @param disclosureKey
	 * @param value
	 * @param saltV
	 * @param claimV
	 */
	private static void addHashToClaim(Map<String, Object> saltMap, Map<String, Object> claim, String disclosureKey,
			Object value, Object saltV, Object claimV) {

		if (((Integer) value).equals(CredentialDisclosedValue.NOT_DISCLOSED.getStatus())
				&& claim.containsKey(disclosureKey)) {
			String hash = CredentialsUtils.getFieldSaltHash(JSONObject.toJSONString(claimV), String.valueOf(saltV));
			claim.put(disclosureKey, hash);
		}
	}

	/**
	 * 
	 * @param disclosures
	 * @param salt
	 * @param claim
	 */
	private static void addSaltForList(List<Object> disclosures, List<Object> salt, List<Object> claim) {
		for (int i = 0; claim != null && i < disclosures.size(); i++) {
			Object disclosureObj = disclosures.get(i);
			Object claimObj = claim.get(i);
			Object saltObj = salt.get(i);
			if (disclosureObj instanceof Map) {
				addSaltForList((HashMap) disclosureObj, salt, claim);
			} else if (disclosureObj instanceof List) {
				addSaltForList((ArrayList<Object>) disclosureObj, (ArrayList<Object>) saltObj,
						(ArrayList<Object>) claimObj);
			}
		}
	}

	private static void addSaltForList(Map<String, Object> disclosures, List<Object> salt, List<Object> claim) {
		for (int i = 0; claim != null && i < claim.size(); i++) {
			Object claimObj = claim.get(i);
			Object saltObj = salt.get(i);
			addSelectSalt(disclosures, (HashMap) saltObj, (HashMap) claimObj);
		}
	}

	/**
	 * Verify that the plolicy parameter is legal
	 * @param credential
	 * @param claimPolicy
	 * @param pctid
	 * @return
	 */
	public static RetEnum verifyPolicy(Credential credential, ClaimPolicy claimPolicy, String pctid) {
		Map<String, Object> saltMap = credential.obtainSalt();
		Map<String, Object> selectMap;
		try {
			selectMap = ConverDataUtils.deserialize(claimPolicy.getDisclosedFieldsJson(),HashMap.class);
		} catch (Exception e) {
			log.error("claimPolicy objToMap error", e);
			return RetEnum.RET_CREDENTIAL_TRANSFER_MAP_ERROR;
		}

		Object claimPctid = credential.getClaimMeta().get(ClaimMetaKey.PCTID);
		if (!StringUtils.equals(String.valueOf(claimPctid), pctid)) {
			log.error("the presenter pctid->{} of presentation does not match the credential's pctid ->{}. ", pctid,
					claimPctid);
			return RetEnum.RET_CREDENTIAL_DID_ERROR;
		}
		verifyDisclosureAndSalt(selectMap, saltMap);

		return RetEnum.RET_SUCCESS;
	}

	/**
	 * Verify that the salt and disclosure object keys are consistent
	 * @param disclosureMap
	 * @param saltMap
	 * @return
	 */
	private static RetEnum verifyDisclosureAndSalt(Map<String, Object> disclosureMap, Map<String, Object> saltMap) {
		for (String disclosureK : disclosureMap.keySet()) {
			Object disclosureV = disclosureMap.get(disclosureK);
			Object saltV = saltMap.get(disclosureK);
			if (saltV == null) {
				log.error("the presenter salt not contains key : {}. ", disclosureK);
				return RetEnum.RET_PRESENTATION_SALT_ERROR;
			}
			//Recursively traverse all keys, list, map objects in the map, etc., whether they match and match
			if (disclosureV instanceof Map) {
				RetEnum resp = verifyDisclosureAndSalt((HashMap) disclosureV, (HashMap) saltV);
				if (!RetEnum.isSuccess(resp)) {
					return resp;
				}
			} else if (disclosureV instanceof List) {
				ArrayList<Object> disclosurs = (ArrayList<Object>) disclosureV;
				RetEnum resp = verifyDisclosureAndSaltList(disclosurs, (ArrayList<Object>) saltV);
				if (!RetEnum.isSuccess(resp)) {
					return resp;
				}
			} else {
				Integer disclosure = (Integer) disclosureV;

				if (saltV == null || (!CredentialDisclosedValue.checkStatus(disclosure))) {
					log.error("policy disclosureValue {} illegal.", disclosure);
					return RetEnum.RET_PRESENTATION_POLICY_DISCLOSUREVALUE_ILLEGAL;
				}

				String salt = String.valueOf(saltV);
				if ((disclosure.equals(CredentialDisclosedValue.NOT_DISCLOSED.getStatus()) && salt.length()> 1)
						|| (disclosure.equals(CredentialDisclosedValue.NOT_DISCLOSED.getStatus())
								&& !salt.equals(String.valueOf(CredentialDisclosedValue.NOT_DISCLOSED.getStatus())))) {
					return RetEnum.RET_PRESENTATION_DISCLOSUREVALUE_NOTMATCH_SALTVALUE;
				}
				if (disclosure.equals(CredentialDisclosedValue.NOT_DISCLOSED.getStatus()) && salt.length() <= 1) {
					return RetEnum.RET_PRESENTATION_DISCLOSUREVALUE_NOTMATCH_SALTVALUE;
				}
			}
		}
		return RetEnum.RET_SUCCESS;
	}

	private static RetEnum verifyDisclosureAndSaltList(List<Object> disclosureList, List<Object> saltList) {
		for (int i = 0; i < disclosureList.size(); i++) {
			Object disclosure = disclosureList.get(i);
			Object saltV = saltList.get(i);
			if (disclosure instanceof Map) {
				RetEnum resp = verifyDisclosureAndSaltList((HashMap) disclosure, (ArrayList<Object>) saltList);
				if (!RetEnum.isSuccess(resp)) {
					return resp;
				}
			} else if (disclosure instanceof List) {
				RetEnum resp = verifyDisclosureAndSaltList((ArrayList<Object>) disclosure, (ArrayList<Object>) saltV);
				if (!RetEnum.isSuccess(resp)) {
					return resp;
				}
			}
		}
		return RetEnum.RET_SUCCESS;
	}

	private static RetEnum verifyDisclosureAndSaltList(Map<String, Object> disclosure, List<Object> saltList) {
		for (int i = 0; i < saltList.size(); i++) {
			Object saltV = saltList.get(i);
			RetEnum resp = verifyDisclosureAndSalt((HashMap) disclosure, (HashMap) saltV);
			if (!RetEnum.isSuccess(resp)) {
				return resp;
			}
		}
		return RetEnum.RET_SUCCESS;
	}
}
