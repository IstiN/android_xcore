package by.istin.android.xcore.gson;

import android.content.ContentValues;
import android.util.Pair;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import by.istin.android.xcore.annotations.Config;
import by.istin.android.xcore.annotations.converter.IConverter;
import by.istin.android.xcore.annotations.converter.gson.GsonConverter;
import by.istin.android.xcore.utils.Log;
import by.istin.android.xcore.utils.ReflectUtils;

public abstract class AbstractValuesAdapter implements JsonDeserializer<ContentValues> {

    public static final int UNKNOWN_POSITION = -1;

    private final Class<?> mContentValuesEntityClazz;

    private final ReflectUtils.ConfigWrapper mClassConfig;

    private List<ReflectUtils.XField> mEntityKeys;

    private int mCurrentPosition = 0;

    private Map<String, Set<Pair<String[], ReflectUtils.XField>>> mKeyFieldsMap;

    public Class<?> getContentValuesEntityClazz() {
        return mContentValuesEntityClazz;
    }

    public AbstractValuesAdapter(Class<?> contentValuesEntityClazz) {
        this.mContentValuesEntityClazz = contentValuesEntityClazz;
        this.mClassConfig = ReflectUtils.getClassConfig(mContentValuesEntityClazz);
        this.mEntityKeys = ReflectUtils.getEntityKeys(mContentValuesEntityClazz);
        this.mKeyFieldsMap = ReflectUtils.getKeyFieldsMap(contentValuesEntityClazz);
    }

    @Override
    public ContentValues deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        ContentValues contentValues = deserializeContentValues(null, mCurrentPosition, jsonElement, type, jsonDeserializationContext);
        mCurrentPosition++;
        return contentValues;
    }

    public ContentValues deserializeContentValues(ContentValues parent, int position, JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        if (mClassConfig != null) {
            Config.Transformer<GsonConverter.Meta> transformer = mClassConfig.transformer();
            IConverter<GsonConverter.Meta> converter = transformer.converter();
            if (converter != null) {
                ContentValues contentValues = new ContentValues();
                GsonConverter.Meta meta = new GsonConverter.Meta(this, jsonElement, type, jsonDeserializationContext, null);
                converter.convert(contentValues, null, null, meta);
                return proceed(parent, position, contentValues, jsonElement);
            }
        }
        ContentValues contentValues = new ContentValues();
        if (mEntityKeys == null) {
            return proceed(parent, position, contentValues, jsonElement);
        }
        if (!jsonElement.isJsonObject()) {
            return null;
        }
        JsonObject jsonObject = (JsonObject) jsonElement;
        Set<Map.Entry<String, JsonElement>> pairs = jsonObject.entrySet();
        //check optimized way
        if (pairs.size() < mEntityKeys.size()) {
            List<Runnable> subEntitiesOperations = new ArrayList<>();
            for (Map.Entry<String, JsonElement> pair : pairs) {
                String key = pair.getKey();
                Set<Pair<String[], ReflectUtils.XField>> fieldPairs = mKeyFieldsMap.get(key);
                if (fieldPairs == null) {
                    continue;
                }
                for (Pair<String[], ReflectUtils.XField> fieldPair : fieldPairs) {
                    ReflectUtils.XField field = fieldPair.second;
                    ReflectUtils.ConfigWrapper config = field.getConfig();
                    boolean isFirstObjectForJsonArray = config.isFirstObjectForArray();
                    JsonElement jsonValue = getTargetElement(jsonObject, isFirstObjectForJsonArray, fieldPair.first);
                    setValueToContentValues(parent, type, jsonDeserializationContext, contentValues, field, config, jsonValue, subEntitiesOperations);
                }
            }
            for (Runnable runnable : subEntitiesOperations) {
                runnable.run();
            }
            subEntitiesOperations.clear();
        } else {
            int size = mEntityKeys.size();
            for (int i = 0; i < size; i++) {
                ReflectUtils.XField field = mEntityKeys.get(i);
                ReflectUtils.ConfigWrapper config = field.getConfig();
                boolean isFirstObjectForJsonArray = config.isFirstObjectForArray();
                JsonElement jsonValue = getTargetElement(jsonObject, isFirstObjectForJsonArray, field.getSplittedSerializedName());
                setValueToContentValues(parent, type, jsonDeserializationContext, contentValues, field, config, jsonValue, null);
            }
        }
        return proceed(parent, position, contentValues, jsonElement);
    }

    public void setValueToContentValues(ContentValues parent, final Type type, final JsonDeserializationContext jsonDeserializationContext, final ContentValues contentValues, final ReflectUtils.XField field, ReflectUtils.ConfigWrapper config, final JsonElement jsonValue, List<Runnable> subEntitiesOperations) {
        if (jsonValue == null) {
            return;
        }

        final Config.DBType dbType = config.dbType();
        if (isCustomConverter(config, contentValues, parent, jsonValue, type, jsonDeserializationContext, field)) {
            return;
        }

        if (subEntitiesOperations == null) {
            checkSubEntities(type, jsonDeserializationContext, contentValues, field, jsonValue, dbType);
        } else {
            subEntitiesOperations.add(new Runnable() {
                @Override
                public void run() {
                    checkSubEntities(type, jsonDeserializationContext, contentValues, field, jsonValue, dbType);
                }
            });
        }
    }

    public void checkSubEntities(Type type, JsonDeserializationContext jsonDeserializationContext, ContentValues contentValues, ReflectUtils.XField field, JsonElement jsonValue, Config.DBType dbType) {
        //need to be at the end, because parent id sometimes depends on all keys in the list
        if (dbType == Config.DBType.ENTITY) {
            Class<?> clazz = field.getDbEntityClass();
            JsonObject subEntityJsonObject = jsonValue.getAsJsonObject();
            proceedSubEntity(type, jsonDeserializationContext, contentValues, field, clazz, subEntityJsonObject);
        } else if (dbType == Config.DBType.ENTITIES) {
            if (jsonValue.isJsonArray()) {
                JsonArray jsonArray = jsonValue.getAsJsonArray();
                proceedSubEntities(type, jsonDeserializationContext, contentValues, field, jsonArray);
            } else {
                Class<?> clazz = field.getDbEntitiesClass();
                JsonObject subEntityJsonObject = jsonValue.getAsJsonObject();
                proceedSubEntity(type, jsonDeserializationContext, contentValues, field, clazz, subEntityJsonObject);
            }
        }
    }

    public JsonElement getTargetElement(JsonObject jsonObject, boolean isFirstObjectForJsonArray, String[] values) {
        if (values.length == 1) {
            return jsonObject.get(values[0]);
        }
        JsonElement jsonValue = null;
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
        return jsonValue;
    }

    private boolean isCustomConverter(ReflectUtils.ConfigWrapper configWrapper, ContentValues contentValues, ContentValues parent, JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext, ReflectUtils.XField field) {
        IConverter converter = configWrapper.transformer().converter();
        if (converter == null) {
            return false;
        }
        try {
            GsonConverter.Meta meta = new GsonConverter.Meta(this, jsonElement, type, jsonDeserializationContext, field);
            converter.convert(contentValues, ReflectUtils.getStaticStringValue(field), parent, meta);
        } catch (UnsupportedOperationException e) {
            Log.xe(this, field.getNameOfField() + ":" + jsonElement.toString());
            throw e;
        }
        return true;
    }

    protected abstract void proceedSubEntities(Type type, JsonDeserializationContext jsonDeserializationContext, ContentValues contentValues, ReflectUtils.XField field, JsonArray jsonArray);

    protected abstract void proceedSubEntity(Type type, JsonDeserializationContext jsonDeserializationContext, ContentValues contentValues, ReflectUtils.XField field, Class<?> clazz, JsonObject subEntityJsonObject);

    protected abstract ContentValues proceed(ContentValues parent, int position, ContentValues contentValues, JsonElement jsonElement);

}