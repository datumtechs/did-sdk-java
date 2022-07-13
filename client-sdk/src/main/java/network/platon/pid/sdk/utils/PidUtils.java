package network.platon.pid.sdk.utils;


import com.platon.bech32.Bech32;
import com.platon.crypto.Keys;
import com.platon.parameters.NetworkParameters;
import com.platon.utils.Numeric;
import network.platon.pid.common.config.PidConfig;
import network.platon.pid.common.utils.DateUtils;
import network.platon.pid.sdk.base.dto.*;
import network.platon.pid.sdk.constant.PidConst;
import network.platon.pid.sdk.req.agency.SetAuthorityReq;
import network.platon.pid.sdk.req.pid.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;



/**
 * PId tools
 * @Auther: Zhangrj
 * @Date: 2020年5月29日
 * @Description:
 */
public class PidUtils {

	public static String generateZeroAddr () {
		return new String(new byte[]{0});
	}

	/**
	 * generate Pid
	 * @param publicKey
	 * @return
	 */
	public static String generatePid(String publicKey) {
		if (StringUtils.isBlank(publicKey)) {
            return StringUtils.EMPTY;
        }
		String address = Keys.getAddress(publicKey);
		address = Bech32.addressEncode(NetworkParameters.getHrp(), address);
        return convertAddressStrToPid(address);
	}

	public static String appendHexPrefix(String str) {
		if (str.startsWith("0x")) {
			return str;
		}
		return "0x"+str;
	}

	public static String trimPidPrefix(String pid) {
		return StringUtils.removeStart(pid, PidConst.PID_PREFIX);
	}

	public static String convertAddressStrToPid(String address) {
		StringBuilder pid = new StringBuilder();
		pid.append(PidConst.PID_PREFIX);
		pid.append(address);
        return pid.toString();
	}

	public static String convertPidToAddressStr(String pid) {
		String pidAddrStr = trimPidPrefix(pid);
		if (!isValidAddressStr(pidAddrStr)) {
			return "";
		}
		return pidAddrStr;
	}

	public static Boolean isValidPid(String pid) {
		if (StringUtils.isBlank(pid)) {
            return false;
        }
		if(!pid.startsWith(PidConst.PID_PREFIX)) {
			return false;
		}
		return isValidAddressStr(trimPidPrefix(pid));
	}

	public static boolean isValidstring(String addr) {
		return isValidAddressStr(addr);
	}

	public static boolean isValidAddressStr(String addr) {
		if (StringUtils.isBlank(addr)
				|| !Pattern.compile(PidConst.PLATONE_ADDRESS_PATTERN).matcher(addr).matches()) {
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
		return Numeric.cleanHexPrefix(addr).length() == PidConst.ADDRESS_LENGTH_IN_HEX;
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

	public static boolean isEmptyAuthorityInfo (AuthorityInfo info) {
		if (null == info || PidUtils.generateZeroAddr().equals(info.getPid()) || StringUtils.isBlank(info.getName())) {
			return true;
		}
		return false;
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

	public static boolean verifySetAuthenticationArg(SetPidAuthReq req) {
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


	public static boolean verifySetAuthorityArg(SetAuthorityReq req) {
		return null != req
				&& verifyAuthority(req.getAuthority())
				&& StringUtils.isNotBlank(req.getPrivateKey())
				&& isPrivateKeyValid(req.getPrivateKey());
	}

	public static boolean verifyAuthority(AuthorityInfo authorityInfo) {
		return null != authorityInfo
				&& isValidPid(authorityInfo.getPid())
				&& StringUtils.isNotBlank(authorityInfo.getName())
				&& authorityInfo.getName().getBytes(StandardCharsets.UTF_8).length
				<= PidConst.MAX_AUTHORITY_ISSUER_NAME_LENGTH;

	}

	public static boolean verifyChangeDocumentStatusArg(ChangeDocumentStatusReq req) {
		return null != req
				&& StringUtils.isNotBlank(req.getPid())
				&& StringUtils.isNotBlank(req.getPrivateKey())
				&& isPrivateKeyValid(req.getPrivateKey());
	}


	public static List<DocumentPubKeyData> getValidPublicKeys (DocumentData data) {
		List<DocumentPubKeyData> keys = new ArrayList<>();
		if (null == data) {
			return keys;
		}
		for (DocumentPubKeyData pubKey: data.getPublicKey()) {
			if (StringUtils.equals(PidConst.DocumentAttrStatus.PID_PUBLICKEY_INVALID.getTag()
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

			if (StringUtils.equals(PidConst.DocumentAttrStatus.PID_PUBLICKEY_INVALID.getTag()
					, pubKey.getStatus())) {
				continue;
			}

			PidPublicKey pk = new PidPublicKey();
			pk.setId(pubKey.getId());
			pk.setType(pubKey.getType());
			pk.setController(pubKey.getController());
			pk.setPublicKeyHex(pubKey.getPublicKeyHex());

			pubKeyMap.put(pubKey.getPublicKeyHex(), pubKey.getId());
			doc.getPublicKey().add(pk);

		}

		List<DocumentAuthData> authDataList = data.getAuthentication();

		// assemble authentiacation array
		for (DocumentAuthData authData : authDataList) {
			if (StringUtils.equals(PidConst.DocumentAttrStatus.PID_AUTH_INVALID.getTag()
					, authData.getStatus())
					|| !pubKeyMap.containsKey(authData.getPublicKeyHex())) {
				continue;
			}
			doc.getAuthentication().add(pubKeyMap.get(authData.getPublicKeyHex()));
		}

		List<DocumentServiceData> serList = data.getService();

		// assemble service array
		for (DocumentServiceData ser : serList) {
			if (StringUtils.equals(PidConst.DocumentAttrStatus.PID_SERVICE_INVALID.getTag()
					, ser.getStatus())) {
				continue;
			}
			PidService service = new PidService();
			service.setId(ser.getId());
			service.setType(ser.getType());
			service.setServiceEndpoint(ser.getServiceEndpoint());

			doc.getService().add(service);
		}

		doc.setId(data.getId());
		doc.setCreated(DateUtils.convertTimestampToUtc(data.getCreated()));
		doc.setUpdated(DateUtils.convertTimestampToUtc(data.getUpdated()));
		doc.setStatus(data.getStatus());
		doc.setContext(PidConst.PID_DEFAULT_CONTEXT);
		return doc;
	}
}
