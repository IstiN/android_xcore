package by.istin.android.xcore.annotations;

import android.content.ContentValues;

import com.google.gson.JsonElement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import by.istin.android.xcore.annotations.converter.IConverter;
import by.istin.android.xcore.annotations.converter.gson.GsonConverter;

@Target(value=ElementType.FIELD)
@Retention(value= RetentionPolicy.RUNTIME)
public @interface dbBoolean {

    Config value() default @Config(dbType = Config.DBType.BOOL, transformer = Transformer.class);

    public static class Transformer extends Config.AbstractTransformer<GsonConverter.Meta> {

        public static final IConverter<GsonConverter.Meta> CONVERTER = new GsonConverter() {
            @Override
            public void convert(ContentValues contentValues, String fieldValue, Object parent, Meta meta) {
                JsonElement jsonValue = meta.getJsonElement();
                if (jsonValue.isJsonNull() || !jsonValue.isJsonPrimitive()) {
                    return;
                }
                contentValues.put(fieldValue, jsonValue.getAsBoolean());
            }
        };

        @Override
        public IConverter<GsonConverter.Meta> converter() {
            return CONVERTER;
        }

    }
}
