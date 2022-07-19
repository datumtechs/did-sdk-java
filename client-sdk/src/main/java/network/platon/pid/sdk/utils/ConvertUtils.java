package network.platon.pid.sdk.utils;

import network.platon.pid.common.utils.DateUtils;
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


}
