package by.istin.android.xcore.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value= RetentionPolicy.RUNTIME)
@Target(value=ElementType.FIELD)
public @interface JsonSubJSONObject {

    String separator() default ":";

    boolean isFirstObjectForJsonArray() default true;

}