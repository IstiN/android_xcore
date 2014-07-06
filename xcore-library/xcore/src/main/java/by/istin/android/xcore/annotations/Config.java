package by.istin.android.xcore.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import by.istin.android.xcore.gson.IConverter;
import by.istin.android.xcore.utils.StringUtil;

@Target(value=ElementType.METHOD)
@Retention(value= RetentionPolicy.RUNTIME)
public @interface Config {

    public static enum DBType {

        BOOL(), BYTE, BYTE_ARRAY, DOUBLE, INTEGER, LONG, STRING, ENTITY(false), ENTITIES(false);

        private boolean isPrimitive = true;

        DBType() {

        }

        DBType(boolean isPrimitive) {
            this.isPrimitive = isPrimitive;
        }

        public boolean isPrimitive() {
            return isPrimitive;
        }
    }

    DBType dbType();

    String key() default StringUtil.EMPTY;

    Class<? extends Transformer> transformer() default DefaultTransformer.class;

    public static interface Transformer {

        public String subElementSeparator();

        public boolean isFirstObjectForArray();

        public IConverter converter();
    }

    public static class DefaultTransformer implements Transformer {

        public static final String SEPARATOR = ":";

        @Override
        public String subElementSeparator() {
            return SEPARATOR;
        }

        @Override
        public boolean isFirstObjectForArray() {
            return true;
        }

        @Override
        public IConverter converter() {
            return null;
        }
    }

}
