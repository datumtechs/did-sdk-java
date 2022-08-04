package network.platon.did.sdk.utils;

import com.platon.utils.Numeric;
import lombok.extern.slf4j.Slf4j;
import network.platon.did.common.constant.VpOrVcPoofKey;
import network.platon.did.csies.algorithm.AlgorithmHandler;
import network.platon.did.csies.utils.ConverDataUtils;
import network.platon.did.csies.utils.Sha256;
import network.platon.did.sdk.base.dto.Credential;
import network.platon.did.sdk.constant.DidConst;
import network.platon.did.sdk.enums.CredentialDisclosedValue;
import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.util.*;

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
			Map<String, Object> disclosures, byte[] seed) {
		// Obtain signature data based on
		String rawData = getCredentialData(credential, salt, disclosures, seed);
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
			Map<String, Object> disclosures, byte[] seed) {
		try {
			Map<String, Object> credMap = ConverDataUtils.objToMap(credential);

			// reset claim
			Map<String, Object> claimHash = getClaimHash(credential, salt, disclosures, seed);
			credMap.put(DidConst.CLAIM, claimHash);

			// reset credential claim data
			Map<String, Object> credentialClaimData = credential.getClaimData();
			credentialClaimData.put(DidConst.CLAIMROOTHASH, claimHash.get(DidConst.CLAIMROOTHASH));
			credentialClaimData.put(DidConst.CLAIMSEED,  claimHash.get(DidConst.CLAIMSEED));

			Map<String, Object> proof = (Map<String, Object>) credMap.get(VpOrVcPoofKey.PROOF);
			// Remove salt for calculation proof
			if(proof == null) {
				proof = new HashMap<>();
			}
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
			Map<String, Object> disclosures, byte[] seed) {
		Map<String, Object> claim = credential.getClaimData();
		Map<String, Object> newClaim = ConverDataUtils.clone((HashMap<String, Object>) claim);

		String allNewValueHashes = addSaltAndGetHash(newClaim, salt, disclosures);
		newClaim.put(DidConst.CLAIMROOTHASH, ConverDataUtils.sha3(allNewValueHashes).substring(2));
		newClaim.put(DidConst.CLAIMSEED, Sha256.byteToUin64(seed));
		return newClaim;
	}

	/**
	 * Add corresponding salt to claim map
	 * @param claim
	 * @param salt
	 * @param disclosures
	 */
	private static String addSaltAndGetHash(Map<String, Object> claim, Map<String, Object> salt,
			Map<String, Object> disclosures) {
		String allNewValueHashes = "";
		for (Map.Entry<String, Object> entry : salt.entrySet()) {
			String key = entry.getKey();
			Object disclosureObj = null;
			if (disclosures != null) {
				disclosureObj = disclosures.get(key);
			}
			Object saltObj = salt.get(key);
			Object newClaimObj = claim.get(key);

			if (saltObj instanceof Map) {
				allNewValueHashes += addSaltAndGetHash((Map<String, Object>) newClaimObj, (Map<String, Object>) saltObj,
						(Map<String, Object>) disclosureObj);
			} else if (saltObj instanceof List) {
				ArrayList<Object> disclosureObjList = null;
				if (disclosureObj != null) {
					disclosureObjList = (ArrayList<Object>) disclosureObj;
				}
				allNewValueHashes += addSaltAndGetHashForList((ArrayList<Object>) newClaimObj, (ArrayList<Object>) saltObj,
						disclosureObjList);
			} else {
				allNewValueHashes += addSaltByDisclose(claim, key, disclosureObj, saltObj, newClaimObj);
			}
		}

		return allNewValueHashes;
	}

	/**
	 * Recursively salt the claim
	 * @param claim
	 * @param key
	 * @param disclosureObj
	 * @param saltObj
	 * @param newClaimObj
	 */
	private static String addSaltByDisclose(Map<String, Object> claim, String key, Object disclosureObj, Object saltObj,
			Object newClaimObj) {
		String newValue = "";
		if (disclosureObj == null) {
			if (!CredentialDisclosedValue.DISCLOSED.getStatus().equals(saltObj)) {
				newValue = getFieldSaltHash(String.valueOf(newClaimObj), String.valueOf(saltObj));
				claim.put(key, newValue);
			}
		} else if (CredentialDisclosedValue.DISCLOSED.getStatus().equals(disclosureObj)) {
			newValue = getFieldSaltHash(String.valueOf(newClaimObj), String.valueOf(saltObj));
			claim.put(key, newValue);
		}

		return newValue;
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
	private static String addSaltAndGetHashForList(List<Object> claim, List<Object> salt, List<Object> disclosures) {
		String allNewValueHashes = "";
		for (int i = 0; claim != null && i < claim.size(); i++) {
			Object obj = claim.get(i);
			Object saltObj = salt.get(i);
			if (obj instanceof Map) {
				Object disclosureObj = null;
				if (disclosures != null) {
					disclosureObj = disclosures.get(0);
				}
				allNewValueHashes += addSaltAndGetHash((Map<String, Object>) obj, (Map<String, Object>) saltObj,
						(Map<String, Object>) disclosureObj);
			} else if (obj instanceof List) {
				ArrayList<Object> disclosureObjList = null;
				if (disclosures != null) {
					Object disclosureObj = disclosures.get(i);
					if (disclosureObj != null) {
						disclosureObjList = (ArrayList<Object>) disclosureObj;
					}
				}
				allNewValueHashes += addSaltAndGetHashForList((ArrayList<Object>) obj, (ArrayList<Object>) saltObj, disclosureObjList);
			}
		}

		return allNewValueHashes;
	}

	public static boolean verifyClaimDataRootHash(Map<String, Object> claimData){

		Map<String, Object> newClaim = ConverDataUtils.clone((HashMap<String, Object>) claimData);
		newClaim.remove(DidConst.CLAIMROOTHASH);
		newClaim.remove(DidConst.CLAIMSEED);

		BigInteger seedUint64 = new BigInteger(String.valueOf(claimData.get(DidConst.CLAIMSEED)));
		byte[] seed = Sha256.uint64ToByte(seedUint64);

		HashMap<String, Object> saltMap = ConverDataUtils.clone((HashMap<String, Object>)newClaim);
		CredentialsUtils.generateSalt(saltMap, seed);

		String rootHash = (String)claimData.get(DidConst.CLAIMROOTHASH);

		String allNewValueHashes = addSaltAndGetHash(newClaim, saltMap, null);
		return StringUtils.equals(rootHash, ConverDataUtils.sha3(allNewValueHashes).substring(2));
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
	 * @param seed
	 */
	public static void generateSalt(Map<String, Object> map, byte[] seed) {
		if(null == seed){
			Random r = new Random();
			long oneRandom = r.nextLong();
			seed = Sha256.uint64ToByte(new BigInteger(String.valueOf(oneRandom)));
		}

		List<Map.Entry<String, Object>> list = new ArrayList<Map.Entry<String, Object>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Object>>() {

			@Override
			public int compare(Map.Entry<String, Object> o1, Map.Entry<String, Object> o2) {
				return o1.getKey().compareTo(o2.getKey());
			}
		});

		for (Map.Entry<String, Object> entry : list) {
			Object value = entry.getValue();
			if (value instanceof Map) {
				generateSalt((Map<String, Object>) value, seed);
			} else if (value instanceof List) {
				boolean isMapOrList = generateSaltFromList((ArrayList<Object>) value, seed);
				if (!isMapOrList) {
					entry.setValue(seed);
				}
			} else {
				seed = Sha256.sha256(seed);
				entry.setValue(Sha256.byteToUin64(seed));
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

	private static boolean generateSaltFromList(List<Object> objList, byte[] seed) {
		List<Object> list = (List<Object>) objList;
		for (Object obj : list) {
			if (obj instanceof Map) {
				generateSalt((Map<String, Object>) obj, seed);
			} else if (obj instanceof List) {
				boolean result = generateSaltFromList((ArrayList<Object>) obj, seed);
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
