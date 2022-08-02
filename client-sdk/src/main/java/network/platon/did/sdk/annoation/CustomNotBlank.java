package network.platon.did.sdk.annoation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Determine if the request parameter string is empty
 * @Auther: Rongjin Zhang
 * @Date: 2020年6月3日
 * @Description:
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CustomNotBlank {

	/**
	 * Return information if the parameter is wrong, if it is empty, return the default parameter name
	 * @return
	 */
	String desc() default ""; 
}
