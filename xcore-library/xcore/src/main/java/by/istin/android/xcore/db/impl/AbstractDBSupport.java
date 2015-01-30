package by.istin.android.xcore.db.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.db.IDBSupport;
import by.istin.android.xcore.db.entity.IBeforeArrayUpdate;
import by.istin.android.xcore.db.operation.IDBBatchOperationSupport;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.DataSourceRequestEntity;
import by.istin.android.xcore.source.SyncDataSourceRequestEntity;
import by.istin.android.xcore.utils.ReflectUtils;

/**
 * Created with IntelliJ IDEA.
 * User: IstiN
 * Date: 19.10.13
 */
public abstract class AbstractDBSupport implements IDBSupport {

    //we need only one instance of helper
    private volatile static DBHelper sDbHelper;

    private static final Object sLock = new Object();

    private static volatile boolean isInit = false;

    private Class<?>[] mEntities;

    private String mName;

    public AbstractDBSupport(String name) {
        this.mName = name;
    }

    @Override
    public String getName() {
        return mName;
    }

    private void initTables() {
        sDbHelper.createTablesForModels(DataSourceRequestEntity.class);
        sDbHelper.createTablesForModels(SyncDataSourceRequestEntity.class);
        sDbHelper.createTablesForModels(mEntities);
        isInit = true;
    }

    @Override
    public void create(Context context, Class<?>[] entities) {
        getOrCreateDBHelper(context);
        if (entities != null) {
            mEntities = entities.clone();
        } else {
            mEntities = null;
        }
    }

    public DBHelper getOrCreateDBHelper(Context context) {
        DBHelper result = sDbHelper;
        if (result == null) {
            synchronized (sLock) {
                result = sDbHelper;
                if (result == null) {
                    sDbHelper = result = new DBHelper(createConnector(context));
                }
            }
        }
        return result;
    }

    @Override
    public IDBBatchOperationSupport getConnectionForBatchOperation() {
        synchronized (sLock) {
            if (!isInit) {
                initTables();
            }
        }
        final IDBConnection writableDatabase = sDbHelper.getWritableDbConnection();
        return new IDBBatchOperationSupport() {

            @Override
            public int delete(String className, String where, String[] whereArgs) {
                return sDbHelper.delete(writableDatabase, ReflectUtils.classForName(className), where, whereArgs);
            }

            @Override
            public long updateOrInsert(DataSourceRequest dataSourceRequest, String className, ContentValues initialValues) {
                Class<?> classOfModel = ReflectUtils.classForName(className);
                IBeforeArrayUpdate beforeArrayUpdate = ReflectUtils.getInstanceInterface(classOfModel, IBeforeArrayUpdate.class);
                if (beforeArrayUpdate != null) {
                    beforeArrayUpdate.onBeforeListUpdate(sDbHelper, writableDatabase, dataSourceRequest, 0, initialValues);
                }
                return sDbHelper.updateOrInsert(dataSourceRequest, writableDatabase, classOfModel, initialValues);
            }

            @Override
            public void beginTransaction() {
                sDbHelper.beginTransaction(writableDatabase);
            }

            @Override
            public void setTransactionSuccessful() {
                sDbHelper.setTransactionSuccessful(writableDatabase);
            }

            @Override
            public void endTransaction() {
                sDbHelper.endTransaction(writableDatabase);
            }
        };
    }

    @Override
    public int delete(String className, String where, String[] whereArgs) {
        synchronized (sLock) {
            if (!isInit) {
                initTables();
            }
        }
        Class<?> clazz = ReflectUtils.classForName(className);
        return sDbHelper.delete(clazz, where, whereArgs);
    }


    @Override
    public long updateOrInsert(DataSourceRequest dataSourceRequest, String className, ContentValues initialValues) {
        synchronized (sLock) {
            if (!isInit) {
                initTables();
            }
        }
        Class<?> clazz = ReflectUtils.classForName(className);
        return sDbHelper.updateOrInsert(dataSourceRequest, clazz, initialValues);
    }

    @Override
    public int updateOrInsert(DataSourceRequest dataSourceRequest, String className, ContentValues[] values) {
        synchronized (sLock) {
            if (!isInit) {
                initTables();
            }
        }
        Class<?> clazz = ReflectUtils.classForName(className);
        return sDbHelper.updateOrInsert(dataSourceRequest, clazz, values);
    }

    @Override
    public Cursor rawQuery(String sql, String[] args) {
        synchronized (sLock) {
            if (!isInit) {
                initTables();
            }
        }
        return sDbHelper.rawQuery(sql, args);
    }

    @Override
    public Cursor query(String className, String[] projection, String selection, String[] selectionArgs, String groupBy, String having, String sortOrder, String limitParam) {
        synchronized (sLock) {
            if (!isInit) {
                initTables();
            }
        }
        Class<?> clazz = ReflectUtils.classForName(className);
        return sDbHelper.query(clazz, projection, selection, selectionArgs, groupBy, having, sortOrder, limitParam);
    }

}