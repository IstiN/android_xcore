package by.istin.android.xcore.gson;

import android.content.ContentValues;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;

import java.lang.reflect.Type;

import by.istin.android.xcore.annotations.dbEntities;
import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.db.entity.IBeforeArrayUpdate;
import by.istin.android.xcore.db.impl.DBHelper;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.utils.ReflectUtils;

/**
 * Created by IstiN on 6.12.13.
 */
interface IGsonEntitiesConverter {

    void convert(Params params);

    public static class Params {
        private final IBeforeArrayUpdate beforeListUpdate;
        private final Type type;
        private final JsonDeserializationContext jsonDeserializationContext;
        private final ContentValues contentValues;
        private final Class<?> clazz;
        private final ReflectUtils.XField field;
        private final DataSourceRequest dataSourceRequest;
        private final IDBConnection dbConnection;
        private final DBHelper dbHelper;
        private final JsonArray jsonArray;
        private final String foreignKey;
        private final Long id;
        private final int count;

        public Params(IBeforeArrayUpdate beforeListUpdate, Type type, JsonDeserializationContext jsonDeserializationContext, ContentValues contentValues, Class<?> clazz, ReflectUtils.XField field, DataSourceRequest dataSourceRequest, IDBConnection dbConnection, DBHelper dbHelper, JsonArray jsonArray, String foreignKey, Long id, int count) {
            this.beforeListUpdate = beforeListUpdate;
            this.type = type;
            this.jsonDeserializationContext = jsonDeserializationContext;
            this.contentValues = contentValues;
            this.clazz = clazz;
            this.field = field;
            this.dataSourceRequest = dataSourceRequest;
            this.dbConnection = dbConnection;
            this.dbHelper = dbHelper;
            this.jsonArray = jsonArray;
            this.foreignKey = foreignKey;
            this.id = id;
            this.count = count;
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

        public ReflectUtils.XField getField() {
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

        public JsonArray getJsonArray() {
            return jsonArray;
        }

        public String getForeignKey() {
            return foreignKey;
        }

        public Long getId() {
            return id;
        }

        public int getCount() {
            return count;
        }
    }
}
