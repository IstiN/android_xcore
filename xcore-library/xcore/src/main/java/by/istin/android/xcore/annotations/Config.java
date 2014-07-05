package by.istin.android.xcore.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import by.istin.android.xcore.utils.StringUtil;

@Target(value=ElementType.METHOD)
@Retention(value= RetentionPolicy.RUNTIME)
public @interface Config {

    public static enum DBType {
        BOOL, BYTE, BYTE_ARRAY, DOUBLE, INTEGER, LONG, STRING, ENTITY, ENTITIES;
    }

    DBType dbType();

    String key() default StringUtil.EMPTY;

}
