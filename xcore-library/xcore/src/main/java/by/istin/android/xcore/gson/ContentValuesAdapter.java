package by.istin.android.xcore.gson;

import android.content.ContentValues;
import by.istin.android.xcore.annotations.dbEntities;
import by.istin.android.xcore.annotations.dbEntity;
import by.istin.android.xcore.model.JSONModel;
import by.istin.android.xcore.utils.BytesUtils;
import by.istin.android.xcore.utils.ReflectUtils;

import com.google.gson.*;

import java.lang.reflect.Type;

public class ContentValuesAdapter extends AbstractValuesAdapter<ContentValues> {

    public ContentValuesAdapter(Class<?> contentValuesClass) {
        super(contentValuesClass);
    }

    @Override
    protected void proceedSubEntities(Type type, JsonDeserializationContext jsonDeserializationContext, ContentValues contentValues, ReflectUtils.XField field, String fieldValue, JsonArray jsonArray) {
        dbEntities entity = ReflectUtils.getAnnotation(field, dbEntities.class);
        Class<?> clazz = entity.clazz();
        ContentValuesAdapter contentValuesAdapter = new ContentValuesAdapter(clazz);
        ContentValues[] values = new ContentValues[jsonArray.size()];
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonElement item = jsonArray.get(i);
            if (item.isJsonPrimitive()) {
                JsonParser parser = new JsonParser();
                JSONModel jsonObject = new JSONModel();
                String string = item.getAsString();
                if (string.contains("\"")) {
                    string = string.replace("\"", "&quot;");
                }
                jsonObject.set("value", string);
                item = parser.parse(jsonObject.toString());
            }
            values[i] = contentValuesAdapter.deserialize(item, type, jsonDeserializationContext);
        }
        contentValues.put(fieldValue, BytesUtils.arrayToByteArray(values));
        contentValues.put(entity.contentValuesKey(), entity.clazz().getCanonicalName());
    }

    @Override
    protected void proceedSubEntity(Type type, JsonDeserializationContext jsonDeserializationContext, ContentValues contentValues, ReflectUtils.XField field, String fieldValue, Class<?> clazz, JsonObject subEntityJsonObject) {
        ContentValuesAdapter contentValuesAdapter = new ContentValuesAdapter(clazz);
        ContentValues values = contentValuesAdapter.deserialize(subEntityJsonObject, type, jsonDeserializationContext);
        contentValues.put(fieldValue, BytesUtils.toByteArray(values));
        dbEntity annotation = ReflectUtils.getAnnotation(field, dbEntity.class);
        contentValues.put(annotation.contentValuesKey(), annotation.clazz().getCanonicalName());
    }

    @Override
    protected ContentValues proceed(ContentValues parent, int position, ContentValues contentValues) {
        return contentValues;
    }
}