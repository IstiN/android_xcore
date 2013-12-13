package by.istin.android.xcore.gson;

import android.content.ContentValues;
import android.provider.BaseColumns;
import by.istin.android.xcore.ContextHolder;
import by.istin.android.xcore.annotations.dbEntities;
import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.db.entity.IBeforeArrayUpdate;
import by.istin.android.xcore.db.entity.IGenerateID;
import by.istin.android.xcore.db.impl.DBHelper;
import by.istin.android.xcore.provider.IDBContentProviderSupport;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.utils.ReflectUtils;
import com.google.gson.*;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class DBContentValuesAdapter extends ContentValuesAdapter {

    private final IDBConnection dbConnection;

    private final DBHelper dbHelper;

    private final DataSourceRequest dataSourceRequest;

    private int count = 0;

    private final IBeforeArrayUpdate beforeListUpdate;

    public DBContentValuesAdapter(Class<?> contentValuesClass, DataSourceRequest dataSourceRequest, IDBContentProviderSupport dbContentProvider) {
        super(contentValuesClass);
        this.dbConnection = dbContentProvider.getDbSupport().createConnector(ContextHolder.getInstance().getContext()).getWritableConnection();
        this.dbHelper = dbContentProvider.getDbSupport().getOrCreateDBHelper(ContextHolder.getInstance().getContext());
        this.dataSourceRequest = dataSourceRequest;
        this.beforeListUpdate = ReflectUtils.getInstanceInterface(contentValuesClass, IBeforeArrayUpdate.class);
    }


    protected DBContentValuesAdapter(Class<?> contentValuesClass, DataSourceRequest dataSourceRequest, IDBConnection dbConnection, DBHelper dbHelper) {
        super(contentValuesClass);
        this.dbConnection = dbConnection;
        this.dbHelper = dbHelper;
        this.dataSourceRequest = dataSourceRequest;
        this.beforeListUpdate = ReflectUtils.getInstanceInterface(contentValuesClass, IBeforeArrayUpdate.class);
    }

    public IDBConnection getDbConnection() {
        return this.dbConnection;
    }

    @Override
    protected void proceedSubEntities(Type type, JsonDeserializationContext jsonDeserializationContext, ContentValues contentValues, Field field, String fieldValue, JsonArray jsonArray) {
        dbEntities entity = field.getAnnotation(dbEntities.class);
        Class<?> clazz = entity.clazz();
        IBeforeArrayUpdate beforeListUpdate = ReflectUtils.getInstanceInterface(clazz, IBeforeArrayUpdate.class);
        String foreignKey = DBHelper.getForeignKey(getContentValuesEntityClazz());
        Long id = getParentId(contentValues);
        Class<? extends IGsonEntitiesConverter> jsonConverter = entity.jsonConverter();
        IGsonEntitiesConverter gsonEntityConverter = ReflectUtils.getInstanceInterface(jsonConverter, IGsonEntitiesConverter.class);
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
                id)
        );
    }

    @Override
    protected void proceedSubEntity(Type type, JsonDeserializationContext jsonDeserializationContext, ContentValues contentValues, Field field, String fieldValue, Class<?> clazz, JsonObject subEntityJsonObject) {
        DBContentValuesAdapter contentValuesAdapter = new DBContentValuesAdapter(clazz, dataSourceRequest, dbConnection, dbHelper);
        ContentValues values = contentValuesAdapter.deserializeContentValues(contentValues, UNKNOWN_POSITION, subEntityJsonObject, type, jsonDeserializationContext);
        Long id = getParentId(contentValues);
        values.put(DBHelper.getForeignKey(getContentValuesEntityClazz()), id);
        dbHelper.updateOrInsert(dataSourceRequest, dbConnection, clazz, values);
    }

    private Long getParentId(ContentValues contentValues) {
        Long id = contentValues.getAsLong(BaseColumns._ID);
        if (id == null) {
            IGenerateID generateID = ReflectUtils.getInstanceInterface(getContentValuesEntityClazz(), IGenerateID.class);
            if (generateID == null) {
                throw new IllegalStateException("can not put sub entity without parent id, use IGenerateID.class for generate ID for "+ getContentValuesEntityClazz());
            }
            id = generateID.generateId(dbHelper, dbConnection, dataSourceRequest, contentValues);
            contentValues.put(BaseColumns._ID, id);
        }
        return id;
    }

    @Override
    protected ContentValues proceed(ContentValues parent, int position, ContentValues contentValues) {
        if (parent == null) {
            if (contentValues == null) {
                return null;
            }
            if (beforeListUpdate != null) {
                beforeListUpdate.onBeforeListUpdate(dbHelper, dbConnection, dataSourceRequest, count, contentValues);
            }
            dbHelper.updateOrInsert(dataSourceRequest, dbConnection, getContentValuesEntityClazz(), contentValues);
            count++;
        }
        return contentValues;
    }


}