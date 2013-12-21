package by.istin.android.xcore.gson;

import android.content.ContentValues;

import by.istin.android.xcore.annotations.dbEntities;
import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.db.entity.IBeforeArrayUpdate;
import by.istin.android.xcore.db.impl.DBHelper;
import by.istin.android.xcore.source.DataSourceRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * Created by IstiN on 6.12.13.
 */
@Deprecated
public interface IGsonEntitiesConverter {

    void convert(Params params);

    public static class Params {
        private final IBeforeArrayUpdate beforeListUpdate;
        private final Type type;
        private final JsonDeserializationContext jsonDeserializationContext;
        private final ContentValues contentValues;
        private final Class<?> clazz;
        private final Field field;
        private final DataSourceRequest dataSourceRequest;
        private final IDBConnection dbConnection;
        private final DBHelper dbHelper;
        private final String fieldValue;
        private final JsonArray jsonArray;
        private final String foreignKey;
        private final Long id;
        private final dbEntities entity;

        public Params(IBeforeArrayUpdate beforeListUpdate, Type type, JsonDeserializationContext jsonDeserializationContext, ContentValues contentValues, Class<?> clazz, Field field, DataSourceRequest dataSourceRequest, IDBConnection dbConnection, DBHelper dbHelper, String fieldValue, JsonArray jsonArray, String foreignKey, Long id, dbEntities entity) {
            this.beforeListUpdate = beforeListUpdate;
            this.type = type;
            this.jsonDeserializationContext = jsonDeserializationContext;
            this.contentValues = contentValues;
            this.clazz = clazz;
            this.field = field;
            this.dataSourceRequest = dataSourceRequest;
            this.dbConnection = dbConnection;
            this.dbHelper = dbHelper;
            this.fieldValue = fieldValue;
            this.jsonArray = jsonArray;
            this.foreignKey = foreignKey;
            this.id = id;
            this.entity = entity;
        }

        public IBeforeArrayUpdate getBeforeListUpdate() {
            return beforeListUpdate;
        }

        public Type getType() {
            return type;
        }

        public JsonDeserializationContext getJsonDeserializationContext() {
            return jsonDeserializationContext;
        }

        public ContentValues getContentValues() {
            return contentValues;
        }

        public Class<?> getClazz() {
            return clazz;
        }

        public Field getField() {
            return field;
        }

        public DataSourceRequest getDataSourceRequest() {
            return dataSourceRequest;
        }

        public IDBConnection getDbConnection() {
            return dbConnection;
        }

        public DBHelper getDbHelper() {
            return dbHelper;
        }

        public String getFieldValue() {
            return fieldValue;
        }

        public JsonArray getJsonArray() {
            return jsonArray;
        }

        public String getForeignKey() {
            return foreignKey;
        }

        public Long getId() {
            return id;
        }

        public dbEntities getEntity() {
            return entity;
        }
    }
}
