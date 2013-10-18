package by.istin.android.xcore.db.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import by.istin.android.xcore.db.DBHelper;
import by.istin.android.xcore.db.IDBBatchOperationSupport;
import by.istin.android.xcore.db.IDBConnector;
import by.istin.android.xcore.db.IDBSupport;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.DataSourceRequestEntity;
import by.istin.android.xcore.source.SyncDataSourceRequestEntity;
import by.istin.android.xcore.utils.ReflectUtils;

/**
 * Created with IntelliJ IDEA.
 * User: IstiN
 * Date: 15.10.13
 */
public class SQLiteSupport implements IDBSupport {

    public static final int FROYO = 8;

    //we need only one instance of helper
    private static DBHelper sDbHelper;

    private static volatile Object sLock = new Object();

    private static volatile boolean isInit = false;

    private Class<?>[] mEntities;

    private void initTables() {
        sDbHelper.createTablesForModels(DataSourceRequestEntity.class);
        sDbHelper.createTablesForModels(SyncDataSourceRequestEntity.class);
        sDbHelper.createTablesForModels(mEntities);
        isInit = true;
    }

    @Override
    public void create(Context context, Class<?>[] entities) {
        synchronized (sLock) {
            if (sDbHelper == null) {
                sDbHelper = new DBHelper(context);
            }
        }
        mEntities = entities;
    }

    @Override
    public IDBBatchOperationSupport getConnectionForBatchOperation() {
        final SQLiteDatabase writableDatabase = sDbHelper.getWritableDatabase();
        return new IDBBatchOperationSupport() {

            @Override
            public void beginTransaction(IDBConnector dbConnector) {
                sDbHelper.beginTransaction(writableDatabase);
            }

            @Override
            public int delete(String className, String where, String[] whereArgs) {
                return sDbHelper.delete(writableDatabase, ReflectUtils.classForName(className), where, whereArgs);
            }

            @Override
            public long updateOrInsert(DataSourceRequest dataSourceRequest, String className, ContentValues initialValues) {
                return sDbHelper.updateOrInsert(dataSourceRequest, writableDatabase, ReflectUtils.classForName(className), initialValues);
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
        int buildVersion = Build.VERSION.SDK_INT;
        synchronized (sLock) {
            if (!isInit) {
                initTables();
            }
            if (buildVersion <= FROYO) {
                Class<?> clazz = ReflectUtils.classForName(className);
                return sDbHelper.delete(clazz, where, whereArgs);
            }
        }
        if (buildVersion > FROYO) {
            Class<?> clazz = ReflectUtils.classForName(className);
            return sDbHelper.delete(clazz, where, whereArgs);
        } else {
            throw new IllegalStateException("can't be happens");
        }
    }


    @Override
    public long updateOrInsert(DataSourceRequest dataSourceRequest, String className, ContentValues initialValues) {
        synchronized (sLock) {
            if (!isInit) {
                initTables();
            }
            if (Build.VERSION.SDK_INT <= FROYO) {
                Class<?> clazz = ReflectUtils.classForName(className);
                return sDbHelper.updateOrInsert(dataSourceRequest, clazz, initialValues);
            }
        }
        if (Build.VERSION.SDK_INT > FROYO) {
            Class<?> clazz = ReflectUtils.classForName(className);
            return sDbHelper.updateOrInsert(dataSourceRequest, clazz, initialValues);
        } else {
            throw new IllegalStateException("can't be happens");
        }
    }

    @Override
    public int updateOrInsert(DataSourceRequest dataSourceRequest, String className, ContentValues[] values) {
        synchronized (sLock) {
            if (!isInit) {
                initTables();
            }
            if (Build.VERSION.SDK_INT <= FROYO) {
                Class<?> clazz = ReflectUtils.classForName(className);
                return sDbHelper.updateOrInsert(dataSourceRequest, clazz, values);
            }
        }
        if (Build.VERSION.SDK_INT > FROYO) {
            Class<?> clazz = ReflectUtils.classForName(className);
            return sDbHelper.updateOrInsert(dataSourceRequest, clazz, values);
        } else {
            throw new IllegalStateException("can't be happens");
        }
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