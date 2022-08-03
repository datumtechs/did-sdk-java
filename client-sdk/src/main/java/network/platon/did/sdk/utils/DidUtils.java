package network.platon.did.sdk.utils;


import com.platon.bech32.Bech32;
import com.platon.crypto.Keys;
import com.platon.parameters.NetworkParameters;
import com.platon.utils.Numeric;
import network.platon.did.sdk.base.dto.*;
import network.platon.did.sdk.constant.DidConst;
import network.platon.did.sdk.req.did.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;



/**
 * DId tools
 * @Auther: Zhangrj
 * @Date: 2020年5月29日
 * @Description:
 */
public class DidUtils {

	public static String generateZeroAddr () {
		return new String(new byte[]{0});
	}

	/**
	 * generate Did
	 * @param publicKey
	 * @return
	 */
	public static String generateDid(String publicKey) {
		if (StringUtils.isBlank(publicKey)) {
            return StringUtils.EMPTY;
        }
		String address = Keys.getAddress(publicKey);
		address = Bech32.addressEncode(NetworkParameters.getHrp(), address);
        return convertAddressStrToDid(address);
	}

	public static String appendHexPrefix(String str) {
		if (str.startsWith("0x")) {
			return str;
		}
		return "0x"+str;
	}

	public static String trimDidPrefix(String did) {
		return StringUtils.removeStart(did, DidConst.DID_PREFIX);
	}

	public static String convertAddressStrToDid(String address) {
		StringBuilder did = new StringBuilder();
		did.append(DidConst.DID_PREFIX);
		did.append(address);
        return did.toString();
	}

	public static String convertDidToAddressStr(String did) {
		String didAddrStr = trimDidPrefix(did);
		if (!isValidAddressStr(didAddrStr)) {
			return "";
		}
		return didAddrStr;
	}

	public static Boolean isValidDid(String did) {
		if (StringUtils.isBlank(did)) {
            return false;
        }
		if(!did.startsWith(DidConst.DID_PREFIX)) {
			return false;
		}
		return isValidAddressStr(trimDidPrefix(did));
	}

	public static boolean isValidstring(String addr) {
		return isValidAddressStr(addr);
	}

	public static boolean isValidAddressStr(String addr) {
		if (StringUtils.isBlank(addr)
				|| !Pattern.compile(DidConst.PLATON_ADDRESS_PATTERN).matcher(addr).matches()) {
			return false;
		}
		if(!addr.startsWith(NetworkParameters.getHrp())) {
			return false;
		}

		try {
			new String(addr);
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	public static boolean isValidAddressLength(String addr) {
		return Numeric.cleanHexPrefix(addr).length() == DidConst.ADDRESS_LENGTH_IN_HEX;
	}

	public static boolean isPrivateKeyValid(String privateKey) {
		String priKey = privateKey;
		if (!StringUtils.startsWith(privateKey, "0x")) {
			priKey = "0x" + privateKey;
		}
		return StringUtils.isNotBlank(privateKey)
				&& NumberUtils.isCreatable(priKey)
				&& Numeric.toBigInt(Numeric
				.hexStringToByteArray(privateKey)).compareTo(BigInteger.ZERO) > 0;
	}

	public static boolean verifyAddPublicKeyArg(AddPublicKeyReq req) {
		return null != req
				&& null != req.getType()
				&& StringUtils.isNotBlank(req.getPublicKey())
				&& StringUtils.isNotBlank(req.getPrivateKey())
				&& isPrivateKeyValid(req.getPrivateKey());
	}

	public static boolean verifyUpdatePublicKeyArg(UpdatePublicKeyReq req) {
		return null != req
				&& null != req.getType()
				&& StringUtils.isNotBlank(req.getPublicKey())
				&& StringUtils.isNotBlank(req.getPrivateKey())
				&& isPrivateKeyValid(req.getPrivateKey());
	}


	public static boolean verifyRevocationPublicKeyArg(RevocationPublicKeyReq req) {
		return null != req
				&& StringUtils.isNotBlank(req.getPublicKey())
				&& StringUtils.isNotBlank(req.getPrivateKey())
				&& isPrivateKeyValid(req.getPrivateKey());
	}

	public static boolean verifySetServiceArg(SetServiceReq req) {
		return null != req
				&& StringUtils.isNotBlank(req.getService().getId())
				&& StringUtils.isNotBlank(req.getService().getType())
				&& StringUtils.isNotBlank(req.getService().getServiceEndpoint())
				&& StringUtils.isNotBlank(req.getPrivateKey())
				&& isPrivateKeyValid(req.getPrivateKey());
	}

	public static boolean verifyChangeDocumentStatusArg(ChangeDocumentStatusReq req) {
		return null != req
				&& StringUtils.isNotBlank(req.getPrivateKey())
				&& isPrivateKeyValid(req.getPrivateKey());
	}


	public static List<DocumentPubKeyData> getValidPublicKeys (DocumentData data) {
		List<DocumentPubKeyData> keys = new ArrayList<>();
		if (null == data) {
			return keys;
		}
		for (DocumentPubKeyData pubKey: data.getPublicKey()) {
			if (StringUtils.equals(DidConst.DocumentAttrStatus.DID_PUBLICKEY_INVALID.getTag()
					, pubKey.getStatus())) {
				continue;
			}
			keys.add(pubKey);
		}
		return keys;
	}


	public static Document assembleDocumentPojo(DocumentData data) {
		if (null == data) {
			return null;
		}

		Document doc = new Document();

		Map<String, String> pubKeyMap = new HashMap<>();

		List<DocumentPubKeyData> pubKeyDataList = data.getPublicKey();

		// assemble public key array
		for (DocumentPubKeyData pubKey : pubKeyDataList) {

			if (StringUtils.equals(DidConst.DocumentAttrStatus.DID_PUBLICKEY_INVALID.getTag()
					, pubKey.getStatus())) {
				continue;
			}

			DidPublicKey pk = new DidPublicKey();
			pk.setId(pubKey.getId());
			pk.setType(pubKey.getType());
			pk.setPublicKeyHex(pubKey.getPublicKeyHex());

			pubKeyMap.put(pubKey.getPublicKeyHex(), pubKey.getId());
			doc.getPublicKey().add(pk);

		}

		List<DocumentServiceData> serList = data.getService();

		// assemble service array
		for (DocumentServiceData ser : serList) {
			if (StringUtils.equals(DidConst.DocumentAttrStatus.DID_SERVICE_INVALID.getTag()
					, ser.getStatus())) {
				continue;
			}
			DidService service = new DidService();
			service.setId(ser.getId());
			service.setType(ser.getType());
			service.setServiceEndpoint(ser.getServiceEndpoint());

			doc.getService().add(service);
		}

		doc.setId(data.getId());
		doc.setCreated(data.getCreated());
		doc.setUpdated(data.getUpdated());
		doc.setStatus(data.getStatus());
		doc.setContext(DidConst.DID_DEFAULT_CONTEXT);
		return doc;
	}
}
