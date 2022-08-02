package network.platon.did.sdk.annoation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CustomIgnore {

	/**
	 * Return information if the parameter is wrong, if it is empty, return the default parameter name
	 * @return
	 */
	String desc() default ""; // 返回中文
	
	int min() default Integer.MIN_VALUE;
	
	int max() default Integer.MAX_VALUE;
}
