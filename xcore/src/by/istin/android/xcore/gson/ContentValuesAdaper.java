package by.istin.android.xcore.gson;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;

import android.content.ContentValues;
import by.istin.android.xcore.annotations.dbBoolean;
import by.istin.android.xcore.annotations.dbByte;
import by.istin.android.xcore.annotations.dbDouble;
import by.istin.android.xcore.annotations.dbEntity;
import by.istin.android.xcore.annotations.dbInteger;
import by.istin.android.xcore.annotations.dbLong;
import by.istin.android.xcore.annotations.dbString;
import by.istin.android.xcore.utils.ReflectUtils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;

public class ContentValuesAdaper implements JsonDeserializer<ContentValues> {

	private Class<?> contentValuesEntityClazz;
	
	private List<Field> entityKeys;
	
	public ContentValuesAdaper(Class<?> contentValuesEntityClazz) {
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
		JsonObject jsonObject = (JsonObject)jsonElement;
		for (Field field : entityKeys) {
			String fieldName = field.getName();
			String serializaedName = fieldName;
			SerializedName serializedAnnotation = field.getAnnotation(SerializedName.class);
			if (serializedAnnotation != null) {
				serializaedName = serializedAnnotation.value();
			}
			JsonElement jsonValue = jsonObject.get(serializaedName);
			if (jsonValue == null) {
				continue;
			}
			if (field.isAnnotationPresent(dbLong.class)) {
				contentValues.put(fieldName, jsonValue.getAsLong());
			} else if (field.isAnnotationPresent(dbString.class)) {
				contentValues.put(fieldName, jsonValue.getAsString());
			} else if (field.isAnnotationPresent(dbBoolean.class)) {
				contentValues.put(fieldName, jsonValue.getAsBoolean());
			} else if (field.isAnnotationPresent(dbByte.class)) {
				contentValues.put(fieldName, jsonValue.getAsByte());
			} else if (field.isAnnotationPresent(dbDouble.class)) {
				contentValues.put(fieldName, jsonValue.getAsDouble());
			} else if (field.isAnnotationPresent(dbInteger.class)) {
				contentValues.put(fieldName, jsonValue.getAsInt());
			} else if (field.isAnnotationPresent(dbEntity.class)) {
				//TODO convert entity
				//contentValues.putNull(key)
			}
		}
		return contentValues;
	}

}
