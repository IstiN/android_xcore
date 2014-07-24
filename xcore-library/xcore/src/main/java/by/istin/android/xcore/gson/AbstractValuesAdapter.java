package by.istin.android.xcore.gson;

import android.content.ContentValues;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;
import java.util.List;

import by.istin.android.xcore.annotations.Config;
import by.istin.android.xcore.annotations.dbEntities;
import by.istin.android.xcore.annotations.dbEntity;
import by.istin.android.xcore.utils.Log;
import by.istin.android.xcore.utils.ReflectUtils;
import by.istin.android.xcore.utils.StringUtil;

public abstract class AbstractValuesAdapter<T> implements JsonDeserializer<T> {

    public static final int UNKNOWN_POSITION = -1;


    private final Class<?> mContentValuesEntityClazz;

    private List<ReflectUtils.XField> mEntityKeys;

    public Class<?> getContentValuesEntityClazz() {
        return mContentValuesEntityClazz;
    }

    public AbstractValuesAdapter(Class<?> contentValuesEntityClazz) {
        this.mContentValuesEntityClazz = contentValuesEntityClazz;
    }

    @Override
    public T deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return deserializeContentValues(null, UNKNOWN_POSITION, jsonElement, type, jsonDeserializationContext);
    }

    public T deserializeContentValues(T parent, int position, JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        Config classConfig = ReflectUtils.getClassConfig(mContentValuesEntityClazz);
        if (classConfig != null) {
            Class<? extends Config.Transformer> transformerClass = classConfig.transformer();
            Config.Transformer transformer = ReflectUtils.newSingleInstance(transformerClass);
            IConverter converter = transformer.converter();
            if (converter != null) {
                ContentValues contentValues = new ContentValues();
                converter.convert(contentValues, null, null, jsonElement, type, jsonDeserializationContext);
                return proceed(parent, position, contentValues);
            }
        }
        if (mEntityKeys == null) {
            mEntityKeys = ReflectUtils.getEntityKeys(mContentValuesEntityClazz);
        }
        ContentValues contentValues = new ContentValues();
        if (mEntityKeys == null) {
            return proceed(parent, position, contentValues);
        }
        if (!jsonElement.isJsonObject()) {
            return null;
        }
        JsonObject jsonObject = (JsonObject) jsonElement;
        for (ReflectUtils.XField field : mEntityKeys) {
            JsonElement jsonValue = null;
            String fieldValue = ReflectUtils.getStaticStringValue(field);
            String serializedName = fieldValue;
            if (ReflectUtils.isAnnotationPresent(field, SerializedName.class)) {
                SerializedName serializedAnnotation = ReflectUtils.getAnnotation(field, SerializedName.class);
                if (serializedAnnotation != null) {
                    serializedName = serializedAnnotation.value();
                }
            }
            Config config = field.getConfig();
            String configKey = config.key();
            if (!StringUtil.isEmpty(configKey)) {
                serializedName = configKey;
            }
            Class<? extends Config.Transformer> transformerClass = config.transformer();
            Config.Transformer transformer = ReflectUtils.newSingleInstance(transformerClass);
            String separator = transformer.subElementSeparator();
            boolean isFirstObjectForJsonArray = transformer.isFirstObjectForArray();
            if (separator != null && serializedName.contains(separator)) {
                String[] values = serializedName.split(separator);
                JsonObject tempElement = jsonObject;
                int arrayLength = values.length;
                for (int i = 0; i < arrayLength; i++) {
                    String value = values[i];
                    if (i == arrayLength - 1) {
                        jsonValue = tempElement.get(value);
                    } else {
                        JsonElement element = tempElement.get(value);
                        if (element == null) {
                            break;
                        }
                        if (element.isJsonObject()) {
                            tempElement = (JsonObject) element;
                        } else {
                            if (isFirstObjectForJsonArray && element.isJsonArray()) {
                                JsonArray jsonArray = (JsonArray) element;
                                if (jsonArray.size() > 0) {
                                    tempElement = (JsonObject) jsonArray.get(0);
                                } else {
                                    break;
                                }
                            } else {
                                break;
                            }
                        }
                    }
                }
            } else {
                jsonValue = jsonObject.get(serializedName);
            }
            if (jsonValue == null) {
                continue;
            }
            Config.DBType dbType = config.dbType();
            if (isCustomConverter(transformer, contentValues, parent, jsonValue, type, jsonDeserializationContext, fieldValue)) {
                continue;
            }
            if (dbType == Config.DBType.ENTITY) {
                dbEntity entity = ReflectUtils.getAnnotation(field, dbEntity.class);
                Class<?> clazz = entity.clazz();
                JsonObject subEntityJsonObject = jsonValue.getAsJsonObject();
                proceedSubEntity(type, jsonDeserializationContext, contentValues, field, fieldValue, clazz, subEntityJsonObject);
            } else if (dbType == Config.DBType.ENTITIES) {
                if (jsonValue.isJsonArray()) {
                    JsonArray jsonArray = jsonValue.getAsJsonArray();
                    proceedSubEntities(type, jsonDeserializationContext, contentValues, field, fieldValue, jsonArray);
                } else {
                    dbEntities entity = ReflectUtils.getAnnotation(field, dbEntities.class);
                    Class<?> clazz = entity.clazz();
                    JsonObject subEntityJsonObject = jsonValue.getAsJsonObject();
                    proceedSubEntity(type, jsonDeserializationContext, contentValues, field, fieldValue, clazz, subEntityJsonObject);
                }
            }
        }
        return proceed(parent, position, contentValues);
    }

    private boolean isCustomConverter(Config.Transformer transformer, ContentValues contentValues, T parent, JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext, String fieldValue) {
        IConverter converter = transformer.converter();
        if (converter == null) {
            return false;
        }
        try {
            converter.convert(contentValues, fieldValue, parent, jsonElement, type, jsonDeserializationContext);
        } catch (UnsupportedOperationException e) {
            Log.xe(this, fieldValue + ":" + jsonElement.toString());
            throw e;
        }
        return true;
    }

    protected abstract void proceedSubEntities(Type type, JsonDeserializationContext jsonDeserializationContext, ContentValues contentValues, ReflectUtils.XField field, String fieldValue, JsonArray jsonArray);

    protected abstract void proceedSubEntity(Type type, JsonDeserializationContext jsonDeserializationContext, ContentValues contentValues, ReflectUtils.XField field, String fieldValue, Class<?> clazz, JsonObject subEntityJsonObject);

    protected abstract T proceed(T parent, int position, ContentValues contentValues);

}