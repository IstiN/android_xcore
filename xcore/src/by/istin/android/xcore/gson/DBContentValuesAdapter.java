package by.istin.android.xcore.gson;

import android.content.ContentValues;
import by.istin.android.xcore.annotations.dbEntities;
import by.istin.android.xcore.annotations.dbEntity;
import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.db.impl.DBHelper;
import by.istin.android.xcore.provider.impl.DBContentProviderFactory;
import by.istin.android.xcore.utils.BytesUtils;
import com.google.gson.*;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class DBContentValuesAdapter extends AbstractValuesAdapter<ContentValues> {

    private final IDBConnection dbConnection;

    public DBContentValuesAdapter(Class<?> contentValuesClass, IDBConnection connection) {
        super(contentValuesClass);
        dbConnection = connection;
    }

    @Override
    protected void proceedSubEntities(Type type, JsonDeserializationContext jsonDeserializationContext, ContentValues contentValues, Field field, String fieldValue, JsonArray jsonArray) {
        dbEntities entity = field.getAnnotation(dbEntities.class);
        Class<?> clazz = entity.clazz();
        DBContentValuesAdapter contentValuesAdapter = new DBContentValuesAdapter(clazz, dbConnection);
        ContentValues[] values = new ContentValues[jsonArray.size()];
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonElement item = jsonArray.get(i);
            if (item.isJsonPrimitive()) {
                JsonParser parser = new JsonParser();
                item = parser.parse("{\"value\": \""+item.getAsString()+"\"}");
                values[i] = contentValuesAdapter.deserialize(item, type, jsonDeserializationContext);
            }
            contentValues.put(fieldValue, BytesUtils.arrayToByteArray(values));
            dbEntities annotation = field.getAnnotation(dbEntities.class);
            contentValues.put(annotation.contentValuesKey(), annotation.clazz().getCanonicalName());
        }
    }

    @Override
    protected void proceedSubEntity(Type type, JsonDeserializationContext jsonDeserializationContext, ContentValues contentValues, Field field, String fieldValue, Class<?> clazz, JsonObject subEntityJsonObject) {
        DBContentValuesAdapter contentValuesAdapter = new DBContentValuesAdapter(clazz, dbConnection);
        ContentValues values = contentValuesAdapter.deserialize(subEntityJsonObject, type, jsonDeserializationContext);
        contentValues.put(fieldValue, BytesUtils.toByteArray(values));
        dbEntity annotation = field.getAnnotation(dbEntity.class);
        contentValues.put(annotation.contentValuesKey(), annotation.clazz().getCanonicalName());
    }

    @Override
    protected ContentValues proceed(ContentValues contentValues) {
        return contentValues;
    }
}