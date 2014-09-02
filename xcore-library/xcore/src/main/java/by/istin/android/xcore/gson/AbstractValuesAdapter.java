package by.istin.android.xcore.gson;

import android.content.ContentValues;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.List;

import by.istin.android.xcore.annotations.Config;
import by.istin.android.xcore.annotations.converter.gson.GsonConverter;
import by.istin.android.xcore.annotations.converter.IConverter;
import by.istin.android.xcore.utils.Log;
import by.istin.android.xcore.utils.ReflectUtils;
import by.istin.android.xcore.utils.StringUtil;

public abstract class AbstractValuesAdapter implements JsonDeserializer<ContentValues> {

    public static final int UNKNOWN_POSITION = -1;


    private final Class<?> mContentValuesEntityClazz;

    private List<ReflectUtils.XField> mEntityKeys;

    private int mCurrentPosition = 0;

    public Class<?> getContentValuesEntityClazz() {
        return mContentValuesEntityClazz;
    }

    public AbstractValuesAdapter(Class<?> contentValuesEntityClazz) {
        this.mContentValuesEntityClazz = contentValuesEntityClazz;
    }

    @Override
    public ContentValues deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        ContentValues contentValues = deserializeContentValues(null, mCurrentPosition, jsonElement, type, jsonDeserializationContext);
        mCurrentPosition++;
        return contentValues;
    }

    public ContentValues deserializeContentValues(ContentValues parent, int position, JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        ReflectUtils.ConfigWrapper classConfig = ReflectUtils.getClassConfig(mContentValuesEntityClazz);
        if (classConfig != null) {
            Config.Transformer<GsonConverter.Meta> transformer = classConfig.transformer();
            IConverter<GsonConverter.Meta> converter = transformer.converter();
            if (converter != null) {
                ContentValues contentValues = new ContentValues();
                GsonConverter.Meta meta = new GsonConverter.Meta(this, jsonElement, type, jsonDeserializationContext, null);
                converter.convert(contentValues, null, null, meta);
                return proceed(parent, position, contentValues, jsonElement);
            }
        }
        if (mEntityKeys == null) {
            mEntityKeys = ReflectUtils.getEntityKeys(mContentValuesEntityClazz);
        }
        ContentValues contentValues = new ContentValues();
        if (mEntityKeys == null) {
            return proceed(parent, position, contentValues, jsonElement);
        }
        if (!jsonElement.isJsonObject()) {
            return null;
        }
        JsonObject jsonObject = (JsonObject) jsonElement;
        for (ReflectUtils.XField field : mEntityKeys) {
            JsonElement jsonValue = null;
            String fieldValue = ReflectUtils.getStaticStringValue(field);
            String serializedName = fieldValue;
            serializedName = field.getSerializedNameValue(serializedName);
            ReflectUtils.ConfigWrapper config = field.getConfig();
            String configKey = config.key();
            if (!StringUtil.isEmpty(configKey)) {
                serializedName = configKey;
            }
            Config.Transformer<GsonConverter.Meta> transformer = config.transformer();
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
            if (isCustomConverter(transformer, contentValues, parent, jsonValue, type, jsonDeserializationContext, fieldValue, field)) {
                continue;
            }
            if (dbType == Config.DBType.ENTITY) {
                Class<?> clazz = field.getDbEntityClass();
                JsonObject subEntityJsonObject = jsonValue.getAsJsonObject();
                proceedSubEntity(type, jsonDeserializationContext, contentValues, field, fieldValue, clazz, subEntityJsonObject);
            } else if (dbType == Config.DBType.ENTITIES) {
                if (jsonValue.isJsonArray()) {
                    JsonArray jsonArray = jsonValue.getAsJsonArray();
                    proceedSubEntities(type, jsonDeserializationContext, contentValues, field, fieldValue, jsonArray);
                } else {
                    Class<?> clazz = field.getDbEntitiesClass();
                    JsonObject subEntityJsonObject = jsonValue.getAsJsonObject();
                    proceedSubEntity(type, jsonDeserializationContext, contentValues, field, fieldValue, clazz, subEntityJsonObject);
                }
            }
        }
        return proceed(parent, position, contentValues, jsonElement);
    }

    private boolean isCustomConverter(Config.Transformer<GsonConverter.Meta> transformer, ContentValues contentValues, ContentValues parent, JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext, String fieldValue, ReflectUtils.XField field) {
        IConverter converter = transformer.converter();
        if (converter == null) {
            return false;
        }
        try {
            GsonConverter.Meta meta = new GsonConverter.Meta(this, jsonElement, type, jsonDeserializationContext, field);
            converter.convert(contentValues, fieldValue, parent, meta);
        } catch (UnsupportedOperationException e) {
            Log.xe(this, fieldValue + ":" + jsonElement.toString());
            throw e;
        }
        return true;
    }

    protected abstract void proceedSubEntities(Type type, JsonDeserializationContext jsonDeserializationContext, ContentValues contentValues, ReflectUtils.XField field, String fieldValue, JsonArray jsonArray);

    protected abstract void proceedSubEntity(Type type, JsonDeserializationContext jsonDeserializationContext, ContentValues contentValues, ReflectUtils.XField field, String fieldValue, Class<?> clazz, JsonObject subEntityJsonObject);

    protected abstract ContentValues proceed(ContentValues parent, int position, ContentValues contentValues, JsonElement jsonElement);

}