package by.istin.android.xcore.gson;

import android.content.ContentValues;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;

import by.istin.android.xcore.annotations.JsonSubJSONObject;
import by.istin.android.xcore.annotations.dbBoolean;
import by.istin.android.xcore.annotations.dbByte;
import by.istin.android.xcore.annotations.dbDouble;
import by.istin.android.xcore.annotations.dbEntities;
import by.istin.android.xcore.annotations.dbEntity;
import by.istin.android.xcore.annotations.dbInteger;
import by.istin.android.xcore.annotations.dbLong;
import by.istin.android.xcore.annotations.dbString;
import by.istin.android.xcore.utils.BytesUtils;
import by.istin.android.xcore.utils.ReflectUtils;

public class ContentValuesAdapter implements JsonDeserializer<ContentValues> {

	private Class<?> contentValuesEntityClazz;
	
	private List<Field> entityKeys;
	
	private IJsonPrimitiveHandler jsonPrimitiveHandler;
	
	public static interface IJsonPrimitiveHandler {
		
		ContentValues convert(JsonPrimitive jsonPrimitive);
		
	}
	
	public IJsonPrimitiveHandler getJsonPrimitiveHandler() {
		return jsonPrimitiveHandler;
	}

	public void setJsonPrimitiveHandler(IJsonPrimitiveHandler jsonPrimitiveHandler) {
		this.jsonPrimitiveHandler = jsonPrimitiveHandler;
	}

	public ContentValuesAdapter(Class<?> contentValuesEntityClazz) {
		this.contentValuesEntityClazz = contentValuesEntityClazz;
	}
	
	@Override
	public ContentValues deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		if (entityKeys == null) {
			entityKeys = ReflectUtils.getEntityKeys(contentValuesEntityClazz); 
		}
		ContentValues contentValues = new ContentValues();
		if (entityKeys == null) {
			return contentValues;
		}
		if (jsonElement.isJsonPrimitive()) {
			if (jsonPrimitiveHandler == null) {
				return null;
			} else {
				return jsonPrimitiveHandler.convert((JsonPrimitive)jsonElement);
			}
		}
		JsonObject jsonObject = (JsonObject)jsonElement;
		for (Field field : entityKeys) {
			String fieldValue = ReflectUtils.getStaticStringValue(field);
			String serializaedName = fieldValue;
			SerializedName serializedAnnotation = field.getAnnotation(SerializedName.class);
			if (serializedAnnotation != null) {
				serializaedName = serializedAnnotation.value();
			}
			JsonElement jsonValue = null;
            JsonSubJSONObject jsonSubJSONObject = field.getAnnotation(JsonSubJSONObject.class);
            String separator = null;
            if (jsonSubJSONObject != null) {
                separator = jsonSubJSONObject.separator();
            }
			if (separator != null && serializaedName.contains(separator)) {
				String[] values = serializaedName.split(separator);
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
                            break;
                        }
					}
				}
			} else {
				jsonValue = jsonObject.get(serializaedName);
			}
			if (jsonValue == null) {
				continue;
			}
			if (jsonValue.isJsonPrimitive()) {
                if (field.isAnnotationPresent(dbLong.class)) {
                    contentValues.put(fieldValue, jsonValue.getAsLong());
                } else if (field.isAnnotationPresent(dbString.class)) {
                    contentValues.put(fieldValue, jsonValue.getAsString());
                } else if (field.isAnnotationPresent(dbBoolean.class)) {
                    contentValues.put(fieldValue, jsonValue.getAsBoolean());
                } else if (field.isAnnotationPresent(dbByte.class)) {
                    contentValues.put(fieldValue, jsonValue.getAsByte());
                } else if (field.isAnnotationPresent(dbDouble.class)) {
                    contentValues.put(fieldValue, jsonValue.getAsDouble());
                } else if (field.isAnnotationPresent(dbInteger.class)) {
                    contentValues.put(fieldValue, jsonValue.getAsInt());
                }
            } else if (field.isAnnotationPresent(dbEntity.class)) {
				dbEntity entity = field.getAnnotation(dbEntity.class);
				Class<?> clazz = entity.clazz();
				ContentValuesAdapter contentValuesAdaper = new ContentValuesAdapter(clazz);
				ContentValues values = contentValuesAdaper.deserialize(jsonValue.getAsJsonObject(), type, jsonDeserializationContext);
				contentValuesAdaper = null;
				contentValues.put(fieldValue, BytesUtils.toByteArray(values));
				dbEntity annotation = field.getAnnotation(dbEntity.class);
				contentValues.put(annotation.contentValuesKey(), annotation.clazz().getCanonicalName());
			} else if (field.isAnnotationPresent(dbEntities.class)) {
				JsonArray jsonArray = jsonValue.getAsJsonArray();
				dbEntities entity = field.getAnnotation(dbEntities.class);
				Class<?> clazz = entity.clazz();
				ContentValuesAdapter contentValuesAdaper = new ContentValuesAdapter(clazz);
				ContentValues[] values = new ContentValues[jsonArray.size()];
				for (int i = 0; i < jsonArray.size(); i++) {
					values[i] = contentValuesAdaper.deserialize(jsonArray.get(i), type, jsonDeserializationContext);
				}
				contentValuesAdaper = null;
				contentValues.put(fieldValue, BytesUtils.arrayToByteArray(values));
				dbEntities annotation = field.getAnnotation(dbEntities.class);
				contentValues.put(annotation.contentValuesKey(), annotation.clazz().getCanonicalName());
			}
		}
		return contentValues;
	}

}
