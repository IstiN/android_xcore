package by.istin.android.xcore.db.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.db.IDBConnector;
import by.istin.android.xcore.db.IDBSupport;
import by.istin.android.xcore.db.entity.IBeforeArrayUpdate;
import by.istin.android.xcore.db.operation.IDBBatchOperationSupport;
import by.istin.android.xcore.preference.PreferenceHelper;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.DataSourceRequestEntity;
import by.istin.android.xcore.source.SyncDataSourceRequestEntity;
import by.istin.android.xcore.utils.Log;
import by.istin.android.xcore.utils.ReflectUtils;

/**
 * Created with IntelliJ IDEA.
 * User: IstiN
 * Date: 19.10.13
 */
public abstract class AbstractDBSupport implements IDBSupport {

    public static final String PREF_KEY_DB_VERSION_CODE = "db_vc";
    public static final String PREF_KEY_DB_VERSION_NAME = "db_vn";
    //we need only one instance of helper
    private DBHelper mDbHelper;

    private IDBConnector mConnector;

    private final Object mLock = new Object();

    private volatile boolean isInit = false;

    private Class<?>[] mEntities;

    private String mName;

    public AbstractDBSupport(String name) {
        this.mName = name;
    }

    @Override
    public DBHelper getDBHelper() {
        return mDbHelper;
    }

    @Override
    public String getName() {
        return mName;
    }

    private void initTables() {
        if (Log.isDebug() || isVersionChanged()) {
            mDbHelper.createTablesForModels(mEntities);
            updateDbVersion(Log.getVersionCode(), Log.getVersionName());
            Log.d("DBSupport", "db upgraded");
        }
        isInit = true;
    }

    private boolean isVersionChanged() {
        int versionCode = Log.getVersionCode();
        String versionName = Log.getVersionName();
        int dbVc = PreferenceHelper.getInt(PREF_KEY_DB_VERSION_CODE+mName, -1);
        if (dbVc == -1 || dbVc != versionCode) {
            updateDbVersion(versionCode, versionName);
            return true;
        }
        String dbVersionName = PreferenceHelper.getString(PREF_KEY_DB_VERSION_NAME+mName, null);
        if (dbVersionName == null || !dbVersionName.equals(versionName)) {
            updateDbVersion(versionCode, versionName);
            return true;
        }
        Log.d("DBSupport", "version is not changed " + dbVc + " " + versionCode + " " + dbVersionName + " " + versionName);
        return false;
    }

    private void updateDbVersion(int pVersionCode, String pVersionName) {
        PreferenceHelper.set(PREF_KEY_DB_VERSION_CODE+mName, pVersionCode);
        PreferenceHelper.set(PREF_KEY_DB_VERSION_NAME+mName, pVersionName);
    }

    @Override
    public void create(Context context, Class<?>[] entities) {
        mConnector = createConnector(mName, context);
        mDbHelper = new DBHelper(mConnector);
        mEntities = entities;
    }

    public IDBConnector getConnector() {
        checkTables();
        return mConnector;
    }

    @Override
    public IDBBatchOperationSupport getConnectionForBatchOperation() {
        checkTables();
        final IDBConnection writableDatabase = mDbHelper.getWritableDbConnection();
        return new IDBBatchOperationSupport() {

            @Override
            public int delete(String className, String where, String[] whereArgs) {
                return mDbHelper.delete(writableDatabase, ReflectUtils.classForName(className), where, whereArgs);
            }

            @Override
            public long updateOrInsert(DataSourceRequest dataSourceRequest, String className, ContentValues initialValues) {
                Class<?> classOfModel = ReflectUtils.classForName(className);
                IBeforeArrayUpdate beforeArrayUpdate = ReflectUtils.getInstanceInterface(classOfModel, IBeforeArrayUpdate.class);
                if (beforeArrayUpdate != null) {
                    beforeArrayUpdate.onBeforeListUpdate(mDbHelper, writableDatabase, dataSourceRequest, 0, initialValues);
                }
                return mDbHelper.updateOrInsert(dataSourceRequest, writableDatabase, classOfModel, initialValues);
            }

            @Override
            public void beginTransaction() {
                mDbHelper.beginTransaction(writableDatabase);
            }

            @Override
            public void setTransactionSuccessful() {
                mDbHelper.setTransactionSuccessful(writableDatabase);
            }

            @Override
            public void endTransaction() {
                mDbHelper.endTransaction(writableDatabase);
            }
        };
    }

    @Override
    public int delete(String className, String where, String[] whereArgs) {
        checkTables();
        Class<?> clazz = ReflectUtils.classForName(className);
        return mDbHelper.delete(clazz, where, whereArgs);
    }


    @Override
    public long updateOrInsert(DataSourceRequest dataSourceRequest, String className, ContentValues initialValues) {
        checkTables();
        Class<?> clazz = ReflectUtils.classForName(className);
        return mDbHelper.updateOrInsert(dataSourceRequest, clazz, initialValues);
    }

    @Override
    public int updateOrInsert(DataSourceRequest dataSourceRequest, String className, ContentValues[] values) {
        checkTables();
        Class<?> clazz = ReflectUtils.classForName(className);
        return mDbHelper.updateOrInsert(dataSourceRequest, clazz, values);
    }

    @Override
    public Cursor rawQuery(String sql, String[] args) {
        checkTables();
        return mDbHelper.rawQuery(sql, args);
    }

    @Override
    public Cursor query(String className, String[] projection, String selection, String[] selectionArgs, String groupBy, String having, String sortOrder, String limitParam) {
        checkTables();
        Class<?> clazz = ReflectUtils.classForName(className);
        return mDbHelper.query(clazz, projection, selection, selectionArgs, groupBy, having, sortOrder, limitParam);
    }

    public void checkTables() {
        if (isInit) {
            return;
        }
        synchronized (mLock) {
            if (!isInit) {
                initTables();
            }
        }
    }

}