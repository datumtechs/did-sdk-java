package network.platon.pid.sdk.utils;

import com.platon.utils.Numeric;
import lombok.extern.slf4j.Slf4j;
import network.platon.pid.common.constant.VpOrVcPoofKey;
import network.platon.pid.csies.algorithm.AlgorithmHandler;
import network.platon.pid.csies.utils.ConverDataUtils;
import network.platon.pid.sdk.base.dto.Credential;
import network.platon.pid.sdk.constant.PidConst;
import network.platon.pid.sdk.enums.CredentialDisclosedValue;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tools for credential
 * 
 * @Auther: Rongjin Zhang
 * @Date: 2020年6月5日
 * @Description:
 */
@Slf4j
@SuppressWarnings("unchecked")
public class CredentialsUtils {

	/**
	 * Get the credential hash
	 * @param credential  
	 * @param salt  The map object generated by the claim key, the value is filled with different salts to prevent hash collision
	 * @param disclosures  Selective disclosure of objects, default is to disclose all objects
	 * @return
	 */
	public static String getCredentialHash(Credential credential, Map<String, Object> salt,
			Map<String, Object> disclosures) {
		// Obtain signature data based on
		String rawData = getCredentialData(credential, salt, disclosures);
		if (StringUtils.isEmpty(rawData)) {
			return StringUtils.EMPTY;
		}
		// Hash calculation of signature data
		return ConverDataUtils.sha3(rawData);
	}

	/**
	 * Generate data to be signed according to the serialized object of credential
	 * @param credential
	 * @param salt
	 * @param disclosures
	 * @return
	 */
	public static String getCredentialData(Credential credential, Map<String, Object> salt,
			Map<String, Object> disclosures) {
		try {
			Map<String, Object> credMap = ConverDataUtils.objToMap(credential);
			// reset claim
			credMap.put(PidConst.CLAIM, getClaimHash(credential, salt, disclosures));

			Map<String, Object> proof = (Map<String, Object>) credMap.get(VpOrVcPoofKey.PROOF);
			// Remove salt for calculation proof
			if(proof == null) {
				proof = new HashMap<>();
			}
			proof.remove(VpOrVcPoofKey.PROOF_SALT);
			proof.put(VpOrVcPoofKey.PROOF_SALT, null);
			credMap.remove(VpOrVcPoofKey.PROOF);
			credMap.put(VpOrVcPoofKey.PROOF, proof);
			return ConverDataUtils.mapToCompactJson(credMap);
		} catch (Exception e) {
			log.error("get credential data error.", e);
			return StringUtils.EMPTY;
		}
	}

	/**
	 * Calculate the hash value corresponding to different claim keys, 
	 * the hash value is generated by splicing the corresponding salt on its own value
	 * @param credential
	 * @param salt
	 * @param disclosures
	 * @return
	 */
	public static Map<String, Object> getClaimHash(Credential credential, Map<String, Object> salt,
			Map<String, Object> disclosures) {
		Map<String, Object> claim = credential.getClaimData();
		Map<String, Object> newClaim = ConverDataUtils.clone((HashMap<String, Object>) claim);

		addSaltAndGetHash(newClaim, salt, disclosures);
		return newClaim;
	}

	/**
	 * Add corresponding salt to claim map
	 * @param claim
	 * @param salt
	 * @param disclosures
	 */
	private static void addSaltAndGetHash(Map<String, Object> claim, Map<String, Object> salt,
			Map<String, Object> disclosures) {
		for (Map.Entry<String, Object> entry : salt.entrySet()) {
			String key = entry.getKey();
			Object disclosureObj = null;
			if (disclosures != null) {
				disclosureObj = disclosures.get(key);
			}
			Object saltObj = salt.get(key);
			Object newClaimObj = claim.get(key);

			if (saltObj instanceof Map) {
				addSaltAndGetHash((Map<String, Object>) newClaimObj, (Map<String, Object>) saltObj,
						(Map<String, Object>) disclosureObj);
			} else if (saltObj instanceof List) {
				ArrayList<Object> disclosureObjList = null;
				if (disclosureObj != null) {
					disclosureObjList = (ArrayList<Object>) disclosureObj;
				}
				addSaltAndGetHashForList((ArrayList<Object>) newClaimObj, (ArrayList<Object>) saltObj,
						disclosureObjList);
			} else {
				addSaltByDisclose(claim, key, disclosureObj, saltObj, newClaimObj);
			}
		}
	}

	/**
	 * Recursively salt the claim
	 * @param claim
	 * @param key
	 * @param disclosureObj
	 * @param saltObj
	 * @param newClaimObj
	 */
	private static void addSaltByDisclose(Map<String, Object> claim, String key, Object disclosureObj, Object saltObj,
			Object newClaimObj) {
		if (disclosureObj == null) {
			if (!CredentialDisclosedValue.DISCLOSED.getStatus().equals(saltObj)) {
				claim.put(key, getFieldSaltHash(String.valueOf(newClaimObj), String.valueOf(saltObj)));
			}
		} else if (CredentialDisclosedValue.DISCLOSED.getStatus().equals(disclosureObj)) {
			claim.put(key, getFieldSaltHash(String.valueOf(newClaimObj), String.valueOf(saltObj)));
		}
	}

	public static String getFieldSaltHash(String field, String salt) {
		return ConverDataUtils.sha3(String.valueOf(field) + String.valueOf(salt));
	}

	/**
	 * Recursively add salt to the list object in claim
	 * @param claim
	 * @param salt
	 * @param disclosures
	 */
	private static void addSaltAndGetHashForList(List<Object> claim, List<Object> salt, List<Object> disclosures) {
		for (int i = 0; claim != null && i < claim.size(); i++) {
			Object obj = claim.get(i);
			Object saltObj = salt.get(i);
			if (obj instanceof Map) {
				Object disclosureObj = null;
				if (disclosures != null) {
					disclosureObj = disclosures.get(0);
				}
				addSaltAndGetHash((Map<String, Object>) obj, (Map<String, Object>) saltObj,
						(Map<String, Object>) disclosureObj);
			} else if (obj instanceof List) {
				ArrayList<Object> disclosureObjList = null;
				if (disclosures != null) {
					Object disclosureObj = disclosures.get(i);
					if (disclosureObj != null) {
						disclosureObjList = (ArrayList<Object>) disclosureObj;
					}
				}
				addSaltAndGetHashForList((ArrayList<Object>) obj, (ArrayList<Object>) saltObj, disclosureObjList);
			}
		}
	}


	/**
	 * convert credential to corresponding json object
	 * 
	 * @param credential
	 * @return
	 */
	public static String getCredentialThumbprintWithoutSig(Credential credential) {
		try {
			Map<String, Object> credMap = ConverDataUtils.objToMap(credential);
			credMap.remove(VpOrVcPoofKey.PROOF);
			credMap.put(VpOrVcPoofKey.PROOF, null);
			Map<String, Object> salt = (Map<String, Object>) credential.getProof().get(VpOrVcPoofKey.PROOF_SALT);
			credMap.put(PidConst.CLAIM, getClaimHash(credential, salt, null));
			return ConverDataUtils.mapToCompactJson(credMap);
		} catch (Exception e) {
			log.error("get Credential Thumbprint WithoutSig error.", e);
			return StringUtils.EMPTY;
		}
	}

	/**
	 * Verify that the data signature is correct based on the public key
	 * 
	 * @param rawData
	 * @param signature
	 * @param publicKey
	 * @return
	 */
	public static boolean verifyEccSignature(String rawData, String signature, String publicKey) {
		return AlgorithmHandler.verifySignature(rawData, signature, Numeric.toBigInt(publicKey));
	}

	/**
	 * Generate a map of salts based on the map. If fixed has a value, all salts use the fixed value
	 * @param map
	 * @param fixed
	 */
	public static void generateSalt(Map<String, Object> map, Object fixed) {
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			Object value = entry.getValue();
			if (value instanceof Map) {
				generateSalt((Map<String, Object>) value, fixed);
			} else if (value instanceof List) {
				boolean isMapOrList = generateSaltFromList((ArrayList<Object>) value, fixed);
				if (!isMapOrList) {
					if (fixed == null) {
						addSalt(entry);
					} else {
						entry.setValue(fixed);
					}
				}
			} else {
				if (fixed == null) {
					addSalt(entry);
				} else {
					entry.setValue(fixed);
				}
			}
		}
	}

	/**
	 * Add salt to saltMap
	 * @param entry
	 */
	private static void addSalt(Map.Entry<String, Object> entry) {
		String salt = ConverDataUtils.getRandomSalt();
		entry.setValue(salt);
	}

	private static boolean generateSaltFromList(List<Object> objList, Object fixed) {
		List<Object> list = (List<Object>) objList;
		for (Object obj : list) {
			if (obj instanceof Map) {
				generateSalt((Map<String, Object>) obj, fixed);
			} else if (obj instanceof List) {
				boolean result = generateSaltFromList((ArrayList<Object>) obj, fixed);
				if (!result) {
					return result;
				}
			} else {
				return false;
			}
		}
		return true;
	}
}
