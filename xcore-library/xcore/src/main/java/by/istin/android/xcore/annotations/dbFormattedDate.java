package by.istin.android.xcore.annotations;

import android.content.ContentValues;

import com.google.gson.JsonElement;

import org.apache.commons.lang3.time.internal.FastDateFormat;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import by.istin.android.xcore.annotations.converter.IConverter;
import by.istin.android.xcore.annotations.converter.gson.GsonConverter;
import by.istin.android.xcore.utils.ReflectUtils;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface dbFormattedDate {

    Config value() default @Config(dbType = Config.DBType.LONG, transformer = Transformer.class);

    boolean isUnix() default false;

    String format();

    String contentValuesKey();

    public static class Transformer extends Config.AbstractTransformer<GsonConverter.Meta> {

        public static final IConverter<GsonConverter.Meta> CONVERTER = new GsonConverter() {
            @Override
            public void convert(ContentValues contentValues, String fieldValue, Object parent, Meta meta) {
                JsonElement jsonValue = meta.getJsonElement();
                long dateTime = jsonValue.getAsLong();
                ReflectUtils.XField field = meta.getField();
                String format = field.getFormat();
                String formatContentValuesKey = field.getFormatContentValuesKey();
                boolean formatIsUnix = field.getFormatIsUnix();
                FastDateFormat dateFormat = FastDateFormat.getInstance(format);
                long adaptive = formatIsUnix ? dateTime * 1000l : dateTime;
                contentValues.put(fieldValue, adaptive);
                contentValues.put(formatContentValuesKey, dateFormat.format(adaptive));
            }
        };

        @Override
        public IConverter<GsonConverter.Meta> converter() {
            return CONVERTER;
        }

    }
}
