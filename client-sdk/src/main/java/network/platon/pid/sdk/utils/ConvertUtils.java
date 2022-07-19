package network.platon.pid.sdk.utils;

import network.platon.pid.common.utils.DateUtils;
import network.platon.pid.contract.AuthorityController;
import network.platon.pid.csies.utils.ConverDataUtils;
import network.platon.pid.sdk.constant.PidConst;
import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.*;

/**
 * 数据转换工具类
 * 
 * @file ConvertUtil.java
 * @description
 * @author zhangrj
 */
public class ConvertUtils {
	private ConvertUtils() {
	}

//	public static final String UTC_STANDRARD_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";

	public static String captureName(String name) {
		if(StringUtils.isBlank(name)) {
			return "";
		}
		char[] cs = name.toCharArray();
		if (cs[0] >= 'a' && cs[0] <= 'z') {
			cs[0] = (char) (cs[0] - 32);
	    }
		return String.valueOf(cs);

	}

	public static String appendHexPrefix(String value) {
		if(value.startsWith("0x")) {
			return value;
		}
		return "0x"+value;

	}
	
	public static class ConvertResultEntry {
		private Boolean status = false;
		private Object  data;
		ConvertResultEntry() {
			status = false;
			data = null;
		}
		public Boolean getStatus() {
			return status;
		}
		public void setStatus(Boolean status) {
			this.status = status;
		}
		public Object getData() {
			return data;
		}
		public void setData(Object data) {
			this.data = data;
		}
	}

	public static ConvertResultEntry convertStrToBytesMax32 (String str){
		byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
		ConvertResultEntry entry = new ConvertResultEntry();
		if (bytes.length <= PidConst.MAX_AUTHORITY_ISSUER_NAME_LENGTH) {
			byte[] newBytes = new byte[bytes.length];
			System.arraycopy(bytes, 0, newBytes, 0, bytes.length);
			entry.status = true;
			entry.data = newBytes;
		}
		return entry;
	}

	public static ConvertResultEntry convertBytesMax32ToStr (byte[] bytes){
		String str = "";
		ConvertResultEntry entry = new ConvertResultEntry();
		if (bytes.length <= PidConst.MAX_AUTHORITY_ISSUER_NAME_LENGTH) {
			byte[] newBytes = new byte[bytes.length];
			System.arraycopy(bytes, 0, newBytes, 0, bytes.length);
			str = new String(bytes, StandardCharsets.UTF_8);
			entry.status = true;
			entry.data = str.trim();
		}
		return entry;
	}

	@SuppressWarnings("unchecked")
	public static AuthorityInfo covertAuthorityInfoByContractAuthority(
			AuthorityController.AuthorityInfo authorityInfo) {

		AuthorityInfo info = new AuthorityInfo();
		String nameStr = new String(authorityInfo.name, StandardCharsets.UTF_8).trim();
		if(StringUtils.isBlank(nameStr)) {
			return info;
		}
		info.setPid(PidUtils.convertAddressStrToPid(authorityInfo.pid));
		info.setName(nameStr);
		long timeInMillis = authorityInfo.create_time.value.longValue();
		if(timeInMillis != 0l) {
			info.setCreateTime(DateUtils.convertTimestampToUtc(timeInMillis));
		}
		if(authorityInfo.accumulate.length != 0) {
			info.setAccumulate(new BigInteger(authorityInfo.accumulate));
		}
		if(authorityInfo.extra.length != 0) {
			info.setExtra(ConverDataUtils.deserialize(new String(authorityInfo.extra,
					StandardCharsets.UTF_8) ,HashMap.class));
		}

		return info;
	}

	public static AuthorityController.AuthorityInfo covertContractAuthorityByAuthorityInfo(
			AuthorityInfo authorityInfo) throws ParseException {

		AuthorityController.AuthorityInfo info = new AuthorityController.AuthorityInfo();

		info.pid = PidUtils.convertPidToAddressStr(authorityInfo.getPid());
		byte[] name = new byte[32];
		byte[] authName = authorityInfo.getName().getBytes(StandardCharsets.UTF_8);
		System.arraycopy(authName, 0, name, 0, authName.length);
		
		info.name = name;

		if(StringUtils.isNotBlank(authorityInfo.getCreateTime())) {
			long timeStamp = 0;
			try {
				timeStamp =  DateUtils.convertUtcDateToTimeStamp(authorityInfo.getCreateTime());
			} catch (ParseException e) {
				throw e;
			}
			info.create_time = Uint64.of(timeStamp);
		}
		
		if(authorityInfo.getAccumulate() != null) {
			info.accumulate = authorityInfo.getAccumulate().toByteArray();
		}
		if(authorityInfo.getExtra() != null) {
			info.extra = ConverDataUtils.serialize(authorityInfo.getExtra())
				.getBytes(StandardCharsets.UTF_8);
		}
		return info;
	}



}
