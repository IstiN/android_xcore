package by.istin.android.xcore.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = {ElementType.FIELD, ElementType.TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
/**
 * Use only for the class Entity. See example DayEntity in the test project.
 */
public @interface dbEntity {

    Config value() default @Config(dbType = Config.DBType.ENTITY);

	Class<?> clazz();
	
}