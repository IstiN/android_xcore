package by.istin.android.xcore.db.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import by.istin.android.xcore.db.DBHelper;
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
    public int delete(String className, String where, String[] whereArgs) {
        synchronized (sLock) {
            if (!isInit) {
                initTables();
            }
            if (Build.VERSION.SDK_INT <= 9) {
                Class<?> clazz = ReflectUtils.classForName(className);
                return sDbHelper.delete(clazz, where, whereArgs);
            }
        }
        if (Build.VERSION.SDK_INT > 9) {
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
            if (Build.VERSION.SDK_INT <= 9) {
                Class<?> clazz = ReflectUtils.classForName(className);
                return sDbHelper.updateOrInsert(dataSourceRequest, clazz, initialValues);
            }
        }
        if (Build.VERSION.SDK_INT > 9) {
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
            if (Build.VERSION.SDK_INT <= 9) {
                Class<?> clazz = ReflectUtils.classForName(className);
                return sDbHelper.updateOrInsert(dataSourceRequest, clazz, values);
            }
        }
        if (Build.VERSION.SDK_INT > 9) {
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