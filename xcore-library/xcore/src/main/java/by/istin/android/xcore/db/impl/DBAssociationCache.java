package by.istin.android.xcore.db.impl;

import android.database.sqlite.SQLiteStatement;
import by.istin.android.xcore.annotations.*;
import by.istin.android.xcore.utils.ReflectUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Uladzimir_Klyshevich
 * Date: 10/14/13
 * Time: 3:20 PM
 */
class DBAssociationCache {

    private static DBAssociationCache INSTANCE = new DBAssociationCache();

    public final static Map<Class<?>, String> TYPE_ASSOCIATION = new ConcurrentHashMap<Class<?>, String>();

    static {
        TYPE_ASSOCIATION.put(dbString.class, "LONGTEXT");
        TYPE_ASSOCIATION.put(dbInteger.class, "INTEGER");
        TYPE_ASSOCIATION.put(dbLong.class, "BIGINT");
        TYPE_ASSOCIATION.put(dbDouble.class, "DOUBLE");
        TYPE_ASSOCIATION.put(dbBoolean.class, "BOOLEAN");
        TYPE_ASSOCIATION.put(dbByte.class, "INTEGER");
        TYPE_ASSOCIATION.put(dbByteArray.class, "BLOB");
    }

    private Map<Class<?>, List<ReflectUtils.XField>> mDbEntityFieldsCache = new ConcurrentHashMap<Class<?>, List<ReflectUtils.XField>>();

    private Map<Class<?>, List<ReflectUtils.XField>> mDbEntitiesFieldsCache = new ConcurrentHashMap<Class<?>, List<ReflectUtils.XField>>();

    private ConcurrentHashMap<String, Boolean> mCacheTable = new ConcurrentHashMap<String, Boolean>();

    private ConcurrentHashMap<Class<?>, String> mCacheTableNames = new ConcurrentHashMap<Class<?>, String>();

    private ConcurrentHashMap<Class<?>, SQLiteStatement> mCacheInsertStatements = new ConcurrentHashMap<Class<?>, SQLiteStatement>();

    private ConcurrentHashMap<Class<?>, String> mForeignKeys = new ConcurrentHashMap<Class<?>, String>();

    private DBAssociationCache() {

    }

    public static DBAssociationCache get() {
        return INSTANCE;
    }


    public List<ReflectUtils.XField> getEntityFields(Class<?> clazz) {
        return mDbEntityFieldsCache.get(clazz);
    }

    public List<ReflectUtils.XField> getEntitiesFields(Class<?> clazz) {
        return mDbEntitiesFieldsCache.get(clazz);
    }


    public void putEntityFields(Class<?> classOfModel, List<ReflectUtils.XField> list) {
        mDbEntityFieldsCache.put(classOfModel, list);
    }

    public void putEntitiesFields(Class<?> classOfModel, List<ReflectUtils.XField> list) {
        mDbEntitiesFieldsCache.put(classOfModel, list);
    }

    public void setTableCreated(String tableName) {
        setTableCreated(tableName, true);
    }

    public void setTableCreated(String tableName, Boolean isCreated) {
        if (isCreated == null) {
            mCacheTable.remove(tableName);
        } else {
            mCacheTable.put(tableName, isCreated);
        }
    }

    public Boolean isTableCreated(String tableName) {
        return mCacheTable.get(tableName);
    }

    public String getTableName(Class<?> clazz) {
        return mCacheTableNames.get(clazz);
    }

    public void setTableName(Class<?> clazz, String tableName) {
        mCacheTableNames.put(clazz, tableName);
    }

    public SQLiteStatement getInsertStatement(Class<?> clazz) {
        return mCacheInsertStatements.get(clazz);
    }

    public void setInsertStatement(Class<?> clazz, SQLiteStatement insertStatement) {
        mCacheInsertStatements.put(clazz, insertStatement);
    }

    public String getForeignKey(Class<?> clazz) {
        return mForeignKeys.get(clazz);
    }

    public void putForeignKey(Class<?> clazz, String value) {
        mForeignKeys.put(clazz, value);
    }

}
