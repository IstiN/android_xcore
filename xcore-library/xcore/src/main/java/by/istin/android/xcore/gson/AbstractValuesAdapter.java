package by.istin.android.xcore.gson;

import android.content.ContentValues;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;

import by.istin.android.xcore.annotations.JsonEntityConverter;
import by.istin.android.xcore.annotations.JsonSubJSONObject;
import by.istin.android.xcore.annotations.dbBoolean;
import by.istin.android.xcore.annotations.dbByte;
import by.istin.android.xcore.annotations.dbDouble;
import by.istin.android.xcore.annotations.dbEntities;
import by.istin.android.xcore.annotations.dbEntity;
import by.istin.android.xcore.annotations.dbInteger;
import by.istin.android.xcore.annotations.dbLong;
import by.istin.android.xcore.annotations.dbString;
import by.istin.android.xcore.utils.ReflectUtils;

public abstract class AbstractValuesAdapter<T> implements JsonDeserializer<T> {

    public static final int UNKNOWN_POSITION = -1;


    private Class<?> mContentValuesEntityClazz;

    private List<Field> mEntityKeys;

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

    protected T deserializeContentValues(T parent, int position, JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        if (mEntityKeys == null) {
            mEntityKeys = ReflectUtils.getEntityKeys(mContentValuesEntityClazz);
        }
        ContentValues contentValues = new ContentValues();
        if (mEntityKeys == null) {
            return proceed(parent, position, contentValues);
        }
        if (isCustomConverter(contentValues, parent, jsonElement, type, jsonDeserializationContext)) {
            return proceed(parent, position, contentValues);
        }
        if (jsonElement.isJsonPrimitive()) {
            return null;
        }
        if (jsonElement.isJsonArray()) {
            return null;
        }
        JsonObject jsonObject = (JsonObject) jsonElement;
        for (Field field : mEntityKeys) {
            JsonElement jsonValue = null;
            String fieldValue = ReflectUtils.getStaticStringValue(field);
            String serializedName = fieldValue;
            if (field.isAnnotationPresent(SerializedName.class)) {
                SerializedName serializedAnnotation = field.getAnnotation(SerializedName.class);
                if (serializedAnnotation != null) {
                    serializedName = serializedAnnotation.value();
                }
            }
            String separator = null;
            boolean isFirstObjectForJsonArray = false;
            if (field.isAnnotationPresent(JsonSubJSONObject.class)) {
                JsonSubJSONObject jsonSubJSONObject = field.getAnnotation(JsonSubJSONObject.class);
                if (jsonSubJSONObject != null) {
                    separator = jsonSubJSONObject.separator();
                    isFirstObjectForJsonArray = jsonSubJSONObject.isFirstObjectForJsonArray();
                }
            }
            if (separator != null && serializedName.contains(separator)) {
                String[] values = serializedName.split(separator);
                JsonObject tempElement = jsonObject;
                for (int i = 0; i < values.length; i++) {
                    if (i == values.length - 1) {
                        jsonValue = tempElement.get(values[i]);
                    } else {
                        JsonElement element = tempElement.get(values[i]);
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
            if (jsonValue.isJsonPrimitive()) {
                putPrimitiveValue(contentValues, field, jsonValue, fieldValue);
            } else if (ReflectUtils.isAnnotationPresent(field, dbEntity.class)) {
                dbEntity entity = field.getAnnotation(dbEntity.class);
                Class<?> clazz = entity.clazz();
                JsonObject subEntityJsonObject = jsonValue.getAsJsonObject();
                proceedSubEntity(type, jsonDeserializationContext, contentValues, field, fieldValue, clazz, subEntityJsonObject);
            } else if (field.isAnnotationPresent(dbEntities.class)) {
                if (jsonValue.isJsonArray()) {
                    JsonArray jsonArray = jsonValue.getAsJsonArray();
                    proceedSubEntities(type, jsonDeserializationContext, contentValues, field, fieldValue, jsonArray);
                } else {
                    dbEntities entity = field.getAnnotation(dbEntities.class);
                    Class<?> clazz = entity.clazz();
                    JsonObject subEntityJsonObject = jsonValue.getAsJsonObject();
                    proceedSubEntity(type, jsonDeserializationContext, contentValues, field, fieldValue, clazz, subEntityJsonObject);
                }
            }
        }
        return proceed(parent, position, contentValues);
    }

    private boolean isCustomConverter(ContentValues contentValues, T parent, JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        JsonEntityConverter annotation = mContentValuesEntityClazz.getAnnotation(JsonEntityConverter.class);
        if (annotation == null) {
            return false;
        }
        Class<? extends IGsonEntityConverter> primitiveConverter = annotation.converter();
        IGsonEntityConverter primitiveConverterInstance = ReflectUtils.getInstanceInterface(primitiveConverter, IGsonEntityConverter.class);
        primitiveConverterInstance.convert(contentValues, parent, jsonElement, type, jsonDeserializationContext);
        return true;
    }

    protected void putPrimitiveValue(ContentValues contentValues, Field field, JsonElement jsonValue, String fieldValue) {
        if (ReflectUtils.isAnnotationPresent(field, dbLong.class)) {
            contentValues.put(fieldValue, jsonValue.getAsLong());
        } else if (ReflectUtils.isAnnotationPresent(field, dbString.class)) {
            contentValues.put(fieldValue, jsonValue.getAsString());
        } else if (ReflectUtils.isAnnotationPresent(field, dbBoolean.class)) {
            contentValues.put(fieldValue, jsonValue.getAsBoolean());
        } else if (ReflectUtils.isAnnotationPresent(field, dbByte.class)) {
            contentValues.put(fieldValue, jsonValue.getAsByte());
        } else if (ReflectUtils.isAnnotationPresent(field, dbDouble.class)) {
            contentValues.put(fieldValue, jsonValue.getAsDouble());
        } else if (ReflectUtils.isAnnotationPresent(field, dbInteger.class)) {
            contentValues.put(fieldValue, jsonValue.getAsInt());
        }
    }

    protected abstract void proceedSubEntities(Type type, JsonDeserializationContext jsonDeserializationContext, ContentValues contentValues, Field field, String fieldValue, JsonArray jsonArray);

    protected abstract void proceedSubEntity(Type type, JsonDeserializationContext jsonDeserializationContext, ContentValues contentValues, Field field, String fieldValue, Class<?> clazz, JsonObject subEntityJsonObject);

    protected abstract T proceed(T parent, int position, ContentValues contentValues);

}