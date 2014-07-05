package by.istin.android.xcore.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import by.istin.android.xcore.gson.DefaultGsonEntitiesConverter;
import by.istin.android.xcore.gson.IGsonEntitiesConverter;

@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface dbEntities {

    Config config() default @Config(dbType = Config.DBType.ENTITIES);

	Class<?> clazz();

    Class<? extends IGsonEntitiesConverter> jsonConverter() default DefaultGsonEntitiesConverter.class;

	boolean ignorePrimitive() default false;

}