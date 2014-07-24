package by.istin.android.xcore.annotations;

import android.content.ContentValues;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Type;

import by.istin.android.xcore.gson.IConverter;

@Target(value=ElementType.FIELD)
@Retention(value= RetentionPolicy.RUNTIME)
public @interface dbString {

    Config value() default @Config(dbType = Config.DBType.STRING, transformer = Transformer.class);

    public static class Transformer extends Config.DefaultTransformer {

        public static final IConverter CONVERTER = new IConverter() {
            @Override
            public void convert(ContentValues contentValues, String fieldValue, Object parent, JsonElement jsonValue, Type type, JsonDeserializationContext jsonDeserializationContext) {
                if (jsonValue.isJsonNull() || !jsonValue.isJsonPrimitive()) {
                    return;
                }
                contentValues.put(fieldValue, jsonValue.getAsString());
            }
        };

        @Override
        public IConverter converter() {
            return CONVERTER;
        }

    }

}