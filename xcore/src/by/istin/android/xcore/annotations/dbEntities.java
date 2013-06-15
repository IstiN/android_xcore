package by.istin.android.xcore.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface dbEntities {

	Class<?> clazz();
	
	/**
	 * Returns key for content values for byte[] with entities,
	 * override if more than one sub entity in the model
	 * @return key for dbEntity name
	 */
	String contentValuesKey() default "dbEntities";
	
	
	/**
	 * Return true if you want notify content resolver about db changes. 
	 * @return flag for notidy content resolver about changes, default false
	 */
	boolean isNotify() default false;
	
}