package by.istin.android.xcore.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used for custom implementation parsing logic of data elements to the ContentValues values.
 */
@Target(value=ElementType.FIELD)
@Retention(value= RetentionPolicy.RUNTIME)
public @interface db {

    /**
     * Config for parsing
     * @return your implementation of config, for more details see @db* annotations.
     */
    Config value();

}
