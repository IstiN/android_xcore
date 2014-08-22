package by.istin.android.xcore.gson;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;

import by.istin.android.xcore.ContextHolder;
import by.istin.android.xcore.annotations.dbEntities;
import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.db.IDBConnector;
import by.istin.android.xcore.db.entity.IBeforeArrayUpdate;
import by.istin.android.xcore.db.entity.IBeforeUpdate;
import by.istin.android.xcore.db.entity.IGenerateID;
import by.istin.android.xcore.db.impl.DBHelper;
import by.istin.android.xcore.processor.IOnProceedEntity;
import by.istin.android.xcore.provider.IDBContentProviderSupport;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.utils.ReflectUtils;

public class DBContentValuesAdapter extends AbstractValuesAdapter {

    private final IDBConnection dbConnection;

    private ITransactionCreationController transactionCreationController;

    private final DBHelper dbHelper;

    private final DataSourceRequest dataSourceRequest;

    private int count = 0;

    private final IBeforeArrayUpdate beforeListUpdate;

    private final IOnProceedEntity onProceedEntity;

    private IGenerateID generateID;

    private String foreignKey;

    public static class WritableConnectionWrapper implements IDBConnection {

        private IDBConnection connection;

        private final IDBConnector connector;

        WritableConnectionWrapper(IDBConnector connector, IDBConnection connection) {
            this.connection = connection;
            this.connector = connector;
        }

        @Override
        public void execSQL(String sql) {
            connection.execSQL(sql);
        }

        @Override
        public Cursor query(String table, String[] projection, String selection, String[] selectionArgs, String groupBy, String having, String sortOrder, String limit) {
            return connection.query(table, projection, selection, selectionArgs, groupBy, having, sortOrder, limit);
        }

        @Override
        public boolean isExists(String tableName) {
            return connection.isExists(tableName);
        }

        @Override
        public Cursor rawQuery(String sql, String[] selectionArgs) {
            return connection.rawQuery(sql, selectionArgs);
        }

        @Override
        public int delete(String tableName, String where, String[] whereArgs) {
            return connection.delete(tableName, where, whereArgs);
        }

        @Override
        public long insert(String tableName, ContentValues contentValues) {
            return connection.insert(tableName, contentValues);
        }

        @Override
        public void beginTransaction() {
            connection.beginTransaction();
        }

        @Override
        public void setTransactionSuccessful() {
            connection.setTransactionSuccessful();
        }

        @Override
        public void endTransaction() {
            connection.endTransaction();
        }

        @Override
        public int update(String tableName, ContentValues contentValues, String selection, String[] selectionArgs) {
            return connection.update(tableName, contentValues, selection, selectionArgs);
        }

        protected void doneAndCreateNewTransaction() {
            setTransactionSuccessful();
            endTransaction();
            connection = connector.getWritableConnection();
            connection.beginTransaction();
        }
    }

    public static interface ITransactionCreationController {

        boolean isCreateNewTransaction(int count);

        void onTransactionRecreated();
    }

    public DBContentValuesAdapter(Class<?> contentValuesClass, DataSourceRequest dataSourceRequest, IDBContentProviderSupport dbContentProvider) {
        this(contentValuesClass, dataSourceRequest, dbContentProvider, null);
    }

    public DBContentValuesAdapter(Class<?> contentValuesClass, DataSourceRequest dataSourceRequest, IDBContentProviderSupport dbContentProvider, ITransactionCreationController contentValuesAdapterTransactionListener) {
        super(contentValuesClass);
        IDBConnector connector = dbContentProvider.getDbSupport().createConnector(ContextHolder.getInstance().getContext());
        IDBConnection writableConnection = connector.getWritableConnection();
        this.transactionCreationController = contentValuesAdapterTransactionListener;
        this.dbConnection = new WritableConnectionWrapper(connector, writableConnection);
        this.dbHelper = dbContentProvider.getDbSupport().getOrCreateDBHelper(ContextHolder.getInstance().getContext());
        this.dataSourceRequest = dataSourceRequest;
        this.beforeListUpdate = ReflectUtils.getInstanceInterface(contentValuesClass, IBeforeArrayUpdate.class);
        this.onProceedEntity = ReflectUtils.getInstanceInterface(contentValuesClass, IOnProceedEntity.class);
        this.generateID = ReflectUtils.getInstanceInterface(getContentValuesEntityClazz(), IGenerateID.class);
        this.foreignKey = DBHelper.getForeignKey(getContentValuesEntityClazz());
    }


    public DBContentValuesAdapter(Class<?> contentValuesClass, DataSourceRequest dataSourceRequest, IDBConnection dbConnection, DBHelper dbHelper) {
        super(contentValuesClass);
        this.dbConnection = dbConnection;
        this.dbHelper = dbHelper;
        this.dataSourceRequest = dataSourceRequest;
        this.beforeListUpdate = ReflectUtils.getInstanceInterface(contentValuesClass, IBeforeArrayUpdate.class);
        this.onProceedEntity = ReflectUtils.getInstanceInterface(contentValuesClass, IOnProceedEntity.class);
        this.generateID = ReflectUtils.getInstanceInterface(getContentValuesEntityClazz(), IGenerateID.class);
        this.foreignKey = DBHelper.getForeignKey(getContentValuesEntityClazz());
    }

    public IDBConnection getDbConnection() {
        return this.dbConnection;
    }

    @Override
    protected void proceedSubEntities(Type type, JsonDeserializationContext jsonDeserializationContext, ContentValues contentValues, ReflectUtils.XField field, String fieldValue, JsonArray jsonArray) {
        dbEntities entity = ReflectUtils.getAnnotation(field, dbEntities.class);
        Class<?> clazz = entity.clazz();
        IBeforeArrayUpdate beforeListUpdate = ReflectUtils.getInstanceInterface(clazz, IBeforeArrayUpdate.class);
        Long id = getParentId(contentValues);
        IGsonEntitiesConverter gsonEntityConverter = GsonEntitiesConverter.INSTANCE;
        gsonEntityConverter.convert(new IGsonEntitiesConverter.Params(
                beforeListUpdate,
                type,
                jsonDeserializationContext,
                contentValues,
                clazz,
                field,
                dataSourceRequest,
                dbConnection,
                dbHelper,
                fieldValue,
                jsonArray,
                foreignKey,
                id,
                entity,
                count)
        );
    }

    @Override
    protected void proceedSubEntity(Type type, JsonDeserializationContext jsonDeserializationContext, ContentValues contentValues, ReflectUtils.XField field, String fieldValue, Class<?> clazz, JsonObject subEntityJsonObject) {
        DBContentValuesAdapter contentValuesAdapter = new DBContentValuesAdapter(clazz, dataSourceRequest, dbConnection, dbHelper);
        ContentValues values = contentValuesAdapter.deserializeContentValues(contentValues, UNKNOWN_POSITION, subEntityJsonObject, type, jsonDeserializationContext);
        Long id = getParentId(contentValues);
        values.put(foreignKey, id);
        IOnProceedEntity onProceedEntity = ReflectUtils.getInstanceInterface(clazz, IOnProceedEntity.class);
        if (onProceedEntity == null || !onProceedEntity.onProceedEntity(dbHelper, dbConnection, dataSourceRequest, contentValues, values, -1, subEntityJsonObject)) {
            dbHelper.updateOrInsert(dataSourceRequest, dbConnection, clazz, values);
        }
    }

    private Long getParentId(ContentValues contentValues) {
        Long id = contentValues.getAsLong(BaseColumns._ID);
        if (id == null) {
            if (generateID == null) {
                throw new IllegalStateException("can not put sub entity without parent id, use IGenerateID.class for generate ID for "+ getContentValuesEntityClazz());
            }
            id = generateID.generateId(dbHelper, dbConnection, dataSourceRequest, contentValues);
            contentValues.put(BaseColumns._ID, id);
        }
        return id;
    }

    @Override
    protected ContentValues proceed(ContentValues parent, int position, ContentValues contentValues, JsonElement jsonElement) {
        if (parent == null) {
            if (contentValues == null) {
                return null;
            }
            if (beforeListUpdate != null) {
                beforeListUpdate.onBeforeListUpdate(dbHelper, dbConnection, dataSourceRequest, count, contentValues);
            }
            if (onProceedEntity == null || !onProceedEntity.onProceedEntity(dbHelper, dbConnection, dataSourceRequest, null, contentValues, position, jsonElement)) {
                dbHelper.updateOrInsert(dataSourceRequest, dbConnection, getContentValuesEntityClazz(), contentValues);
            }
            if (transactionCreationController != null) {
                if (transactionCreationController.isCreateNewTransaction(count)) {
                    if (dbConnection instanceof WritableConnectionWrapper) {
                        ((WritableConnectionWrapper) dbConnection).doneAndCreateNewTransaction();
                        transactionCreationController.onTransactionRecreated();
                    }
                }
            }
            count++;
        }
        return contentValues;
    }

    public DBHelper getDbHelper() {
        return dbHelper;
    }

    public DataSourceRequest getDataSourceRequest() {
        return dataSourceRequest;
    }

    public int getCount() {
        return count;
    }


}