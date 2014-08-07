package by.istin.android.xcore.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
/**
 * Deprecated, please use @dbSubEntity in future
 */
public @interface dbEntities {

    Config value() default @Config(dbType = Config.DBType.ENTITIES);

	Class<?> clazz();

	boolean ignorePrimitive() default false;

}