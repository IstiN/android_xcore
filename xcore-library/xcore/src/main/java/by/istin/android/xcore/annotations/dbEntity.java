package by.istin.android.xcore.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface dbEntity {

	Class<?> clazz();
	
	/**
	 * Returns key for content values for byte[] with entity
	 * override if more than one sub entity in the model
	 * @return key for dbEntity name
	 */
	String contentValuesKey() default "dbEntity";
	
}