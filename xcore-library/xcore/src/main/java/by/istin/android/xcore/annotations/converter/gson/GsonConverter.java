package by.istin.android.xcore.annotations.converter.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;

import java.lang.reflect.Type;

import by.istin.android.xcore.annotations.converter.IConverter;
import by.istin.android.xcore.gson.AbstractValuesAdapter;
import by.istin.android.xcore.utils.ReflectUtils;

/**
 * GsonConverter provides Meta for gson elements parsing.
 */
public abstract class GsonConverter implements IConverter<GsonConverter.Meta> {

    /**
     * Meta provides basic information for parsing Gson elements. Used in the AbstractValuesAdapter.
     */
    public static class Meta {

        /**
         * Current contentValuesAdapter.
         */
        private final AbstractValuesAdapter abstractValuesAdapter;

        /**
         * Current field wrapper.
         */
        private final ReflectUtils.XField field;

        /**
         * Current jsonElement.
         */
        private JsonElement jsonElement;

        /**
         * Current reflection type.
         */
        private Type type;

        /**
         * Curreng gson context.
         */
        private JsonDeserializationContext jsonDeserializationContext;

        /**
         * Default constructor.
         *
         * @param abstractValuesAdapter      current contentValuesAdapter
         * @param jsonElement                current jsonElement
         * @param type                       current reflection type
         * @param jsonDeserializationContext current gson context
         * @param field                      current field wrapper
         */
        public Meta(
                AbstractValuesAdapter abstractValuesAdapter,
                JsonElement jsonElement,
                Type type,
                JsonDeserializationContext jsonDeserializationContext,
                ReflectUtils.XField field
        ) {
            this.jsonElement = jsonElement;
            this.type = type;
            this.jsonDeserializationContext = jsonDeserializationContext;
            this.abstractValuesAdapter = abstractValuesAdapter;
            this.field = field;
        }

        /**
         * Returns JsonElement.
         *
         * @return jsonElement
         */
        public JsonElement getJsonElement() {
            return jsonElement;
        }

        /**
         * Return reflection type of field.
         *
         * @return reflection type
         */
        public Type getType() {
            return type;
        }

        /**
         * Return gson context.
         *
         * @return gson context
         */
        public JsonDeserializationContext getJsonDeserializationContext() {
            return jsonDeserializationContext;
        }

        /**
         * Return current contentValues adapter.
         *
         * @return contentValuesAdapter
         */
        public AbstractValuesAdapter getAbstractValuesAdapter() {
            return abstractValuesAdapter;
        }

        /**
         * Return current field wrapper.
         *
         * @return field
         */
        public ReflectUtils.XField getField() {
            return field;
        }
    }

}
