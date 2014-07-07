package by.istin.android.xcore.annotations;

import android.content.ContentValues;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Type;

import by.istin.android.xcore.gson.IConverter;
import by.istin.android.xcore.utils.Log;
import by.istin.android.xcore.utils.StringUtil;

@Target(value=ElementType.FIELD)
@Retention(value= RetentionPolicy.RUNTIME)
public @interface dbBoolean {

    Config config() default @Config(dbType = Config.DBType.BOOL, transformer = Transformer.class);

    public static class Transformer extends Config.DefaultTransformer {

        public static final IConverter CONVERTER = new IConverter() {
            @Override
            public void convert(ContentValues contentValues, String fieldValue, Object parent, JsonElement jsonValue, Type type, JsonDeserializationContext jsonDeserializationContext) {
                if (jsonValue.isJsonNull()) {
                    return;
                }
                contentValues.put(fieldValue, jsonValue.getAsBoolean());
            }
        };

        @Override
        public IConverter converter() {
            return CONVERTER;
        }

    }
}
