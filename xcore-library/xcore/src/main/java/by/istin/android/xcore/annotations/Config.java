package by.istin.android.xcore.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import by.istin.android.xcore.annotations.converter.IConverter;
import by.istin.android.xcore.utils.StringUtil;

/**
 * Config annotation used for configuration db* annotation during data parsing to db.
 */
@Target(value=ElementType.METHOD)
@Retention(value= RetentionPolicy.RUNTIME)
public @interface Config {

    /**
     * DBType used for convert data types to the DB types.
     */
    public static enum DBType {

        BOOL, BYTE, BYTE_ARRAY, DOUBLE, INTEGER, LONG, STRING, ENTITY, ENTITIES;

        DBType() {

        }

    }

    /**
     * Returns dbType
     * @return dbType of data element that will be insert to DB.
     */
    DBType dbType();

    /**
     * Data element key.
     * @return name of data element key.
     */
    String key() default StringUtil.EMPTY;

    /**
     * Transformer used for extends parsing logic. Like join sub elements to one entity or add some magic to the parsing
     * @return DefaultTransformer by default. If you need some special implements Transformer interface
     */
    Class<? extends Transformer> transformer() default DefaultTransformer.class;

    /**
     * Transformer interface provides functionality to customize parsing logic
     */
    public static interface Transformer<Meta> {

        /**
         * Returns element separator for parsing data like
         * <code>
         *     {
         *          "entity":
         *          {
         *              "subEntity" :
         *                  {
         *                      "key" : "value"
         *                  }
         *          }
         *     }
         * </code>
         * @return  String value in DefaultTransformer this is ":"
         */
        public String subElementSeparator();


        /**
         * If your dataArray contains only one element you can set this flag to "true" and use @dbEntity annotation to parse this element
         * @return true or false, in the DefaultTransformer true
         */
        public boolean isFirstObjectForArray();

        /**
         * You can specify parsing by your needs. Default implementation of converter you can see in the db* annotations.
         * @return IConverter implementation that convert data element to the DBType
         */
        public IConverter<Meta> converter();
    }

    public static class DefaultTransformer extends AbstractTransformer<Object>{

        @Override
        public IConverter<Object> converter() {
            return null;
        }
    }
    /**
     * Default transformer uses in most cases to transform data elements to the DB ContentValues.
     * IConverter will be specified in future implementation
     */
    public abstract static class AbstractTransformer<Meta> implements Transformer<Meta> {

        /**
         * Default separator for sub elements ignoring ":"
         */
        public static final String SEPARATOR = ":";

        /**
         * Returns sub element separator
         * @return ":"
         */
        @Override
        public String subElementSeparator() {
            return SEPARATOR;
        }

        /**
         * By default if we will use @dbEntity annotation and will have dataArray it will be parsed like @dbEntity
         * @return true
         */
        @Override
        public boolean isFirstObjectForArray() {
            return true;
        }

    }

}
