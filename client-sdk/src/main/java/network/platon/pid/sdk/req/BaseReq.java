package network.platon.pid.sdk.req;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;
import network.platon.pid.sdk.annoation.CustomIgnore;
import network.platon.pid.sdk.annoation.CustomMax;
import network.platon.pid.sdk.annoation.CustomMin;
import network.platon.pid.sdk.annoation.CustomNotBlank;
import network.platon.pid.sdk.annoation.CustomNotNull;
import network.platon.pid.sdk.annoation.CustomPattern;
import network.platon.pid.sdk.annoation.CustomSize;
import network.platon.pid.common.enums.RetEnum;
import network.platon.pid.sdk.resp.BaseResp;
import network.platon.pid.sdk.utils.ConvertUtils;


@Slf4j
public abstract class BaseReq {

	/**
	 * Verify request parameters
	 * 
	 * @return
	 */
	public BaseResp<String> validFiled() {
		Field[] fields = this.getClass().getDeclaredFields();
		try {
			for (Field field : fields) {
				if (field.isSynthetic())
					continue;
				BaseResp<String> baseResp = checkObject(this, field);
				if (baseResp != null) {
					return baseResp;
				}
			}
		} catch (Exception e) {
			log.error("analysis error", e);
			return BaseResp.buildException();
		}

		return BaseResp.buildSuccess();
	}

	private BaseResp<String> checkObject(Object o, Field field) throws Exception {
		//If there is no annotation, then return directly
		Annotation[] annotations = field.getAnnotations();
		if(annotations.length == 0) {
			return null;
		}
				
		//Exclude parameters that do not require verification
		String fieldName = field.getName();
		if ("serialVersionUID".equals(fieldName) || "log".equals(fieldName) || "logger".equals(fieldName)) {
			return null;
		}
		Method getMethod = o.getClass().getDeclaredMethod("get" + ConvertUtils.captureName(fieldName));
		Object v = getMethod.invoke(o);
		
		//Loop through annotations to verify annotation parameters
		for (Annotation annotation : annotations) {
			BaseResp<String> baseResp = BaseResp.buildSuccess();
			String tipName = o.getClass().getSimpleName() + "." + fieldName;
			if (annotation.annotationType().equals(CustomNotNull.class)) {
				baseResp = this.checkNull(v, tipName, annotation);
			} else if (annotation.annotationType().equals(CustomNotBlank.class)) {
				baseResp = this.checkBlank(v, tipName, annotation);
			} else if (annotation.annotationType().equals(CustomSize.class)) {
				baseResp = this.checkSize(v, tipName, annotation);
			} else if (annotation.annotationType().equals(CustomMin.class)) {
				baseResp = this.checkMin(v, tipName, annotation);
			} else if (annotation.annotationType().equals(CustomMax.class)) {
				baseResp = this.checkMax(v, tipName, annotation);
			} else if (annotation.annotationType().equals(CustomPattern.class)) {
				baseResp = this.checkPattern(v, tipName, annotation);
			}
			if (baseResp.checkFail()) {
				return baseResp;
			}
		}

		//If the annotated object is an embedded object, you need to recursively traverse
		if (checkType(v)) {
			//If the object does not need to be annotated by igonre, you do not need to continue to traverse it.
			CustomIgnore customIgnore = field.getAnnotation(CustomIgnore.class);
			if (customIgnore != null) {
				return null;
			}
			Field[] fields = v.getClass().getDeclaredFields();
			for (Field field2 : fields) {
				BaseResp<String> baseResp = checkObject(v, field2);
				if (baseResp != null) {
					return baseResp;
				}
			}
		}
		return null;
	}

	private Boolean checkType(Object v) {
		if (v == null || v instanceof String || v instanceof Long|| v instanceof  Integer || v instanceof Map
				|| v instanceof List || v instanceof BigInteger)
			return false;
		return true;
	}

	private BaseResp<String> checkNull(Object v, String tipName, Annotation annotation) {
		CustomNotNull notNull = (CustomNotNull) annotation;
		String desc = StringUtils.isNotEmpty(notNull.desc()) ? notNull.desc() : tipName;
		if (v == null) {
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID, desc + " not null");
		}
		return BaseResp.buildSuccess();
	}

	private BaseResp<String> checkBlank(Object v, String tipName, Annotation annotation) {
		CustomNotBlank notBlank = (CustomNotBlank) annotation;
		String desc = StringUtils.isNotEmpty(notBlank.desc()) ? notBlank.desc() : tipName;
		if (v instanceof String && StringUtils.isBlank(String.valueOf(v))) {
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID, desc + " not blank");
		}
		return BaseResp.buildSuccess();
	}

	@SuppressWarnings("rawtypes")
	private BaseResp<String> checkSize(Object v, String tipName, Annotation annotation) {
		CustomSize size = (CustomSize) annotation;
		String desc = StringUtils.isNotEmpty(size.desc()) ? size.desc() : tipName;
		if (v == null) {
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID, desc + " not null");
		}
		if (v instanceof String && (((String) v).length() < size.min() || ((String) v).length() > size.max())) {
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID,
					desc + " length greater than the maximum value allowed or less than the minimum value allowed");
		} else if (v instanceof List && (((List) v).size() < size.min() || ((List) v).size() > size.max())) {
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID,
					desc + " size greater than the maximum value allowed or less than the minimum value allowed");
		} else if (v instanceof Map && (((Map) v).size() < size.min() || ((Map) v).size() > size.max())) {
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID,
					desc + " size greater than the maximum value allowed or less than the minimum value allowed");
		}
		return BaseResp.buildSuccess();
	}

	private BaseResp<String> checkMin(Object v, String tipName, Annotation annotation) {
		CustomMin min = (CustomMin) annotation;
		String desc = StringUtils.isNotEmpty(min.desc()) ? min.desc() : tipName;
		if (v == null) {
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID, desc + " not null");
		}
		if (v instanceof Integer && ((Integer) v) < min.value()) {
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID,
					desc + " value is less than the minimum value allowed");
		}
		if (v instanceof Long && ((Long) v) < min.value()) {
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID,
					desc + " value is less than the minimum value allowed");
		}
		return BaseResp.buildSuccess();
	}

	private BaseResp<String> checkMax(Object v, String tipName, Annotation annotation) {
		CustomMax max = (CustomMax) annotation;
		String desc = StringUtils.isNotEmpty(max.desc()) ? max.desc() : tipName;
		if (v == null) {
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID, desc + " not null");
		}
		if (v instanceof Integer && ((Integer) v) > max.value()) {
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID,
					desc + " value is less than the minimum value allowed");
		}
		if (v instanceof Long && ((Long) v) > max.value()) {
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID,
					desc + " value is less than the minimum value allowed");
		}
		return BaseResp.buildSuccess();
	}

	private BaseResp<String> checkPattern(Object v, String tipName, Annotation annotation) {
		CustomPattern customPattern = (CustomPattern) annotation;
		String desc = StringUtils.isNotEmpty(customPattern.desc()) ? customPattern.desc() : tipName;
		if (v == null) {
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID, desc + " not null");
		}
		if (v instanceof String && !Pattern.compile(customPattern.value()).matcher(String.valueOf(v)).matches()) {
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID,
					desc + " is incorrect and cannot be matched the correct value");
		}
		return BaseResp.buildSuccess();
	}
}
