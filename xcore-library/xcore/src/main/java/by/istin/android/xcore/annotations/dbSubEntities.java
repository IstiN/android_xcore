package by.istin.android.xcore.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface dbSubEntities {

    Config value() default @Config(dbType = Config.DBType.ENTITIES);

	String key();

	boolean ignorePrimitive() default false;

}