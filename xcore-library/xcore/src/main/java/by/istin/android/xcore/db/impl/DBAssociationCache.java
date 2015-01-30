package by.istin.android.xcore.db.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import by.istin.android.xcore.annotations.Config;
import by.istin.android.xcore.annotations.dbBoolean;
import by.istin.android.xcore.annotations.dbByte;
import by.istin.android.xcore.annotations.dbByteArray;
import by.istin.android.xcore.annotations.dbDouble;
import by.istin.android.xcore.annotations.dbFormattedDate;
import by.istin.android.xcore.annotations.dbInteger;
import by.istin.android.xcore.annotations.dbLong;
import by.istin.android.xcore.annotations.dbString;
import by.istin.android.xcore.utils.ReflectUtils;

/**
 * Created with IntelliJ IDEA.
 * User: Uladzimir_Klyshevich
 * Date: 10/14/13
 * Time: 3:20 PM
 */
class DBAssociationCache {

    private static final DBAssociationCache INSTANCE = new DBAssociationCache();

    public final static Map<Class<?>, String> TYPE_ASSOCIATION = new ConcurrentHashMap<Class<?>, String>();

    static {
        TYPE_ASSOCIATION.put(dbString.class, "LONGTEXT");
        TYPE_ASSOCIATION.put(dbInteger.class, "INTEGER");
        TYPE_ASSOCIATION.put(dbLong.class, "BIGINT");
        TYPE_ASSOCIATION.put(dbFormattedDate.class, "BIGINT");
        TYPE_ASSOCIATION.put(dbDouble.class, "DOUBLE");
        TYPE_ASSOCIATION.put(dbBoolean.class, "BOOLEAN");
        TYPE_ASSOCIATION.put(dbByte.class, "INTEGER");
        TYPE_ASSOCIATION.put(dbByteArray.class, "BLOB");
    }

    public final static Map<Config.DBType, String> DB_TYPE_ASSOCIATION = new ConcurrentHashMap<Config.DBType, String>();

    static {
        DB_TYPE_ASSOCIATION.put(Config.DBType.STRING, "LONGTEXT");
        DB_TYPE_ASSOCIATION.put(Config.DBType.INTEGER, "INTEGER");
        DB_TYPE_ASSOCIATION.put(Config.DBType.LONG, "BIGINT");
        DB_TYPE_ASSOCIATION.put(Config.DBType.DOUBLE, "DOUBLE");
        DB_TYPE_ASSOCIATION.put(Config.DBType.BOOL, "BOOLEAN");
        DB_TYPE_ASSOCIATION.put(Config.DBType.BYTE, "INTEGER");
        DB_TYPE_ASSOCIATION.put(Config.DBType.BYTE_ARRAY, "BLOB");
    }

    private final Map<Class<?>, List<ReflectUtils.XField>> mDbEntityFieldsCache = new ConcurrentHashMap<Class<?>, List<ReflectUtils.XField>>();

    private final Map<Class<?>, List<ReflectUtils.XField>> mDbEntitiesFieldsCache = new ConcurrentHashMap<Class<?>, List<ReflectUtils.XField>>();

    private final ConcurrentHashMap<String, Boolean> mCacheTable = new ConcurrentHashMap<String, Boolean>();

    private final ConcurrentHashMap<Class<?>, String> mCacheTableNames = new ConcurrentHashMap<Class<?>, String>();

    private final ConcurrentHashMap<Class<?>, String> mForeignKeys = new ConcurrentHashMap<Class<?>, String>();

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

    public String getForeignKey(Class<?> clazz) {
        return mForeignKeys.get(clazz);
    }

    public void putForeignKey(Class<?> clazz, String value) {
        mForeignKeys.put(clazz, value);
    }

}
