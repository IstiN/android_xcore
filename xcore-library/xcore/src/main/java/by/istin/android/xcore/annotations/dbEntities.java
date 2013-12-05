package by.istin.android.xcore.annotations;

import by.istin.android.xcore.gson.DefaultGsonEntitiesConverter;
import by.istin.android.xcore.gson.IGsonEntitiesConverter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface dbEntities {

	Class<?> clazz();

    Class<? extends IGsonEntitiesConverter> jsonConverter() default DefaultGsonEntitiesConverter.class;

	/**
	 * Returns key for content values for byte[] with entities,
	 * override if more than one sub entity in the model
	 * @return key for dbEntity name
	 */
	String contentValuesKey() default "dbEntities";
	
}