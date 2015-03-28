/**
 *
 */
package by.istin.android.xcore.db.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.provider.BaseColumns;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import by.istin.android.xcore.annotations.db;
import by.istin.android.xcore.annotations.dbEntities;
import by.istin.android.xcore.annotations.dbEntity;
import by.istin.android.xcore.annotations.dbIndex;
import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.db.IDBConnector;
import by.istin.android.xcore.db.entity.IBeforeArrayUpdate;
import by.istin.android.xcore.db.entity.IBeforeUpdate;
import by.istin.android.xcore.db.entity.IGenerateID;
import by.istin.android.xcore.db.entity.IMerge;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.utils.CursorUtils;
import by.istin.android.xcore.utils.Log;
import by.istin.android.xcore.utils.ReflectUtils;
import by.istin.android.xcore.utils.StringUtil;

/**
 * @author Uladzimir_Klyshevich
 */
public class DBHelper {

    private static final String TAG = DBHelper.class.getSimpleName();

    private final IDBConnector mDbConnector;

    private final DBAssociationCache dbAssociationCache;

    public static final boolean IS_LOG_ENABLED = false;

    public static interface ITableNameGenerator {

        String generateTableName(Class clazz);

    }

    public static class Xcore1TableNameGenerator implements ITableNameGenerator {

        @Override
        public String generateTableName(Class clazz) {
            return clazz.getCanonicalName().replace(".", "_");
        }
    }

    public static class Xcore2TableNameGenerator implements ITableNameGenerator {

        @Override
        public String generateTableName(Class clazz) {
            String canonicalName = clazz.getCanonicalName();
            String[] split = canonicalName.split("\\.");
            int length = split.length;
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < length; i++) {
                String s = split[i];
                if (i == length - 1) {
                    builder.append("_").append(s);
                } else {
                    builder.append(s.charAt(0));
                }
            }
            return builder.toString();
        }
    }

    private static ITableNameGenerator sTableNameGenerator = new Xcore2TableNameGenerator();

    public static void setTableNameGenerator(ITableNameGenerator tableNameGenerator) {
        sTableNameGenerator = tableNameGenerator;
    }

    public DBHelper(IDBConnector dbConnector) {
        super();
        mDbConnector = dbConnector;
        dbAssociationCache = DBAssociationCache.get();
    }

    public static String getTableName(Class<?> clazz) {
        DBAssociationCache associationCache = DBAssociationCache.get();
        String tableName = associationCache.getTableName(clazz);
        if (tableName == null) {
            tableName = sTableNameGenerator.generateTableName(clazz);
            associationCache.setTableName(clazz, tableName);
        }
        return tableName;
    }

    public synchronized void createTablesForModels(Class<?>... models) {
        if (IS_LOG_ENABLED)
            Log.xd(this, "mDbConnector.getWritableConnection()");
        IDBConnection dbWriter = mDbConnector.getWritableConnection();
        if (IS_LOG_ENABLED)
            Log.xd(this, "dbWriter.beginTransaction();");
        dbWriter.beginTransaction();
        StringBuilder indexSqlBuilder = new StringBuilder();
        for (Class<?> classOfModel : models) {
            String table = getTableName(classOfModel);
            dbAssociationCache.setTableCreated(table, null);
            String createTableSQLTemplate = mDbConnector.getCreateTableSQLTemplate(table);
            if (IS_LOG_ENABLED)
                Log.xd(this, "dbWriter.execSQL(mDbConnector.getCreateTableSQLTemplate(table)); " + createTableSQLTemplate);
            dbWriter.execSQL(createTableSQLTemplate);
            Cursor columns = null;
            try {
                if (IS_LOG_ENABLED)
                    Log.xd(this, "columns = dbWriter.query(table, null, null, null, null, null, null, \"0,1\");");
                columns = dbWriter.query(table, null, null, null, null, null, null, "0,1");
                List<ReflectUtils.XField> fields = ReflectUtils.getEntityKeys(classOfModel);
                if (fields == null) {
                    continue;
                }
                for (ReflectUtils.XField field : fields) {
                    try {
                        String name = ReflectUtils.getStaticStringValue(field);
                        if (name.equals(BaseColumns._ID)) {
                            continue;
                        }
                        if (columns.getColumnIndex(name) != -1) {
                            continue;
                        }
                        Set<Class<? extends Annotation>> annotations = field.getAnnotations();
                        ReflectUtils.ConfigWrapper config = field.getConfig();
                        String type = null;
                        for (Class<? extends Annotation> classOfAnnotation : annotations) {
                            if (DBAssociationCache.TYPE_ASSOCIATION.containsKey(classOfAnnotation)) {
                                type = DBAssociationCache.TYPE_ASSOCIATION.get(classOfAnnotation);
                            } else if (classOfAnnotation.equals(dbEntity.class)) {
                                List<ReflectUtils.XField> list = dbAssociationCache.getEntityFields(classOfModel);
                                if (list == null) {
                                    list = new ArrayList<>();
                                }
                                list.add(field);
                                dbAssociationCache.putEntityFields(classOfModel, list);
                            } else if (classOfAnnotation.equals(dbEntities.class)) {
                                List<ReflectUtils.XField> list = dbAssociationCache.getEntitiesFields(classOfModel);
                                if (list == null) {
                                    list = new ArrayList<>();
                                }
                                list.add(field);
                                dbAssociationCache.putEntitiesFields(classOfModel, list);
                            } else if (classOfAnnotation.equals(dbIndex.class)) {
                                indexSqlBuilder.append(mDbConnector.getCreateIndexSQLTemplate(table, name));
                            } else if (classOfAnnotation.equals(db.class)) {
                                type = DBAssociationCache.DB_TYPE_ASSOCIATION.get(config.dbType());
                            }
                        }
                        if (type == null) {
                            continue;
                        }
                        if (IS_LOG_ENABLED)
                            Log.xd(this, "dbWriter.execSQL(mDbConnector.getCreateColumnSQLTemplate(table, name, type));");
                        String createColumnSQLTemplate = mDbConnector.getCreateColumnSQLTemplate(table, name, type);
                        dbWriter.execSQL(createColumnSQLTemplate);
                    } catch (SQLException e) {
                        if (IS_LOG_ENABLED)
                            Log.w(TAG, e);
                    }
                }
            } finally {
                CursorUtils.close(columns);
            }
            String sql = indexSqlBuilder.toString();
            Log.xd(this, sql);
            if (!StringUtil.isEmpty(sql)) {
                try {
                    dbWriter.execSQL(sql);
                    if (IS_LOG_ENABLED)
                        Log.xd(this, "dbWriter.execSQL(sql);");
                } catch (SQLException e) {
                    if (IS_LOG_ENABLED)
                        Log.w(TAG, e);
                }
            }
            //NPE with proguard
            if (indexSqlBuilder.length() > 0) {
                indexSqlBuilder.setLength(0);
            }
        }
        setTransactionSuccessful(dbWriter);
        endTransaction(dbWriter);
    }

    public int delete(Class<?> clazz, String where, String[] whereArgs) {
        return delete(null, getTableName(clazz), where, whereArgs);
    }

    public int delete(IDBConnection db, Class<?> clazz, String where, String[] whereArgs) {
        return delete(db, getTableName(clazz), where, whereArgs);
    }

    public int delete(String tableName, String where, String[] whereArgs) {
        return delete(null, tableName, where, whereArgs);
    }

    public int delete(IDBConnection db, String tableName, String where, String[] whereArgs) {
        if (isExists(tableName)) {
            if (db == null) {
                db = mDbConnector.getWritableConnection();
            }
            return db.delete(tableName, where, whereArgs);
        } else {
            return 0;
        }
    }

    public boolean isExists(String tableName) {
        Boolean isTableCreated = dbAssociationCache.isTableCreated(tableName);
        if (isTableCreated != null) {
            return isTableCreated;
        }
        IDBConnection readableDb = mDbConnector.getReadableConnection();
        boolean isExists = readableDb.isExists(tableName);
        dbAssociationCache.setTableCreated(tableName, isExists);
        return isExists;

    }

    public int updateOrInsert(Class<?> classOfModel, ContentValues... contentValues) {
        return updateOrInsert(null, classOfModel, contentValues);
    }

    public int updateOrInsert(DataSourceRequest dataSourceRequest, Class<?> classOfModel, ContentValues... contentValues) {
        if (contentValues == null) {
            return 0;
        }
        IDBConnection db = mDbConnector.getWritableConnection();
        try {
            beginTransaction(db);
            int count = updateOrInsert(dataSourceRequest, classOfModel, db, contentValues);
            setTransactionSuccessful(db);
            return count;
        } finally {
            endTransaction(db);
        }
    }

    public int updateOrInsert(DataSourceRequest dataSourceRequest, Class<?> classOfModel, IDBConnection db, ContentValues[] contentValues) {
        IBeforeArrayUpdate beforeListUpdate = ReflectUtils.getInstanceInterface(classOfModel, IBeforeArrayUpdate.class);
        int count = 0;
        for (int i = 0; i < contentValues.length; i++) {
            ContentValues contentValue = contentValues[i];
            if (contentValue == null) {
                continue;
            }
            if (beforeListUpdate != null) {
                beforeListUpdate.onBeforeListUpdate(this, db, dataSourceRequest, i, contentValue);
            }
            long id = updateOrInsert(dataSourceRequest, db, classOfModel, contentValue);
            if (id != -1l) {
                count++;
            }
        }
        return count;
    }

    public long updateOrInsert(DataSourceRequest dataSourceRequest, IDBConnection db, Class<?> classOfModel, ContentValues contentValues) {
        boolean requestWithoutTransaction = false;
        boolean isNewDbConnection = db == null;
        if (isNewDbConnection) {
            db = mDbConnector.getWritableConnection();
            requestWithoutTransaction = true;
            beginTransaction(db);
        }
        try {
            if (isNewDbConnection) {
                IBeforeUpdate beforeUpdate = ReflectUtils.getInstanceInterface(classOfModel, IBeforeUpdate.class);
                if (beforeUpdate != null) {
                    beforeUpdate.onBeforeUpdate(this, db, dataSourceRequest, contentValues);
                }
            }
            String idAsString = contentValues.getAsString(BaseColumns._ID);
            Long id = null;
            if (idAsString == null) {
                IGenerateID generateId = ReflectUtils.getInstanceInterface(classOfModel, IGenerateID.class);
                if (generateId != null) {
                    id = generateId.generateId(this, db, dataSourceRequest, contentValues);
                    contentValues.put(BaseColumns._ID, id);
                }
                if (id == null) {
                    Log.xd(this, "error to insert ContentValues[" + classOfModel + "]: " + contentValues.toString());
                    throw new IllegalArgumentException("content values needs to contains _ID. Details: " +
                            "error to insert ContentValues[" + classOfModel + "]: " + contentValues.toString());
                }
            } else {
                id = Long.valueOf(idAsString);
            }
            String tableName = getTableName(classOfModel);
            IMerge merge = ReflectUtils.getInstanceInterface(classOfModel, IMerge.class);
            long rowId = 0;
            if (merge == null) {
                rowId = db.insertOrReplace(tableName, contentValues);
            } else {
                Cursor cursor = null;
                try {
                    cursor = query(classOfModel, null, BaseColumns._ID + " = ?", new String[]{String.valueOf(id)}, null, null, null, null);
                    if (cursor == null || !cursor.moveToFirst()) {
                        rowId = internalInsert(db, contentValues, tableName);
                        if (rowId == -1l) {
                            throw new IllegalArgumentException("can not insert content values:" + contentValues.toString() + " to table " + classOfModel + ". Check keys in ContentValues and fields in model.");
                        }
                    } else {
                        ContentValues oldContentValues = new ContentValues();
                        CursorUtils.cursorRowToContentValues(classOfModel, cursor, oldContentValues);
                        merge.merge(this, db, dataSourceRequest, oldContentValues, contentValues);
                        if (!isContentValuesEquals(oldContentValues, contentValues)) {
                            internalUpdate(db, contentValues, id, tableName);
                            rowId = id;
                        } else {
                            rowId = -1l;
                        }
                    }
                } finally {
                    CursorUtils.close(cursor);
                }
            }
            if (requestWithoutTransaction) {
                setTransactionSuccessful(db);
            }
            return rowId;
        } finally {
            if (requestWithoutTransaction) {
                endTransaction(db);
            }
        }
    }

    private int internalUpdate(IDBConnection db, ContentValues contentValues, Long id, String tableName) {
        return db.update(tableName, contentValues, BaseColumns._ID + " = " + id, null);
    }

    public void endTransaction(IDBConnection dbWriter) {
        dbWriter.endTransaction();
    }

    public void setTransactionSuccessful(IDBConnection dbWriter) {
        dbWriter.setTransactionSuccessful();
    }


    public void beginTransaction(IDBConnection dbWriter) {
        dbWriter.beginTransaction();
    }

    private long internalInsert(IDBConnection db, ContentValues contentValues, String tableName) {
        return db.insert(tableName, contentValues);
    }

    public static boolean isContentValuesEquals(ContentValues oldContentValues, ContentValues contentValues) {
        Set<Entry<String, Object>> keySet = contentValues.valueSet();
        for (Entry<String, Object> entry : keySet) {
            Object newObject = entry.getValue();
            Object oldObject = oldContentValues.get(entry.getKey());
            if (newObject == null && oldObject == null) {
                continue;
            }
            if (newObject == null || !newObject.equals(oldObject)) {
                return false;
            }
        }
        return true;
    }


    public static String getForeignKey(Class<?> foreignEntity) {
        DBAssociationCache associationCache = DBAssociationCache.get();
        String foreignKey = associationCache.getForeignKey(foreignEntity);
        if (foreignKey == null) {
            foreignKey = foreignEntity.getSimpleName().toLowerCase() + "_id";
            associationCache.putForeignKey(foreignEntity, foreignKey);
            return foreignKey;
        }
        return foreignKey;
    }

    public Cursor query(Class<?> clazz, String[] projection,
                        String selection, String[] selectionArgs, String groupBy,
                        String having, String sortOrder, String limit) {
        return query(getTableName(clazz), projection, selection, selectionArgs, groupBy, having, sortOrder, limit);
    }

    public Cursor query(String tableName, String[] projection,
                        String selection, String[] selectionArgs, String groupBy,
                        String having, String sortOrder, String limit) {
        if (isExists(tableName)) {
            IDBConnection db = mDbConnector.getReadableConnection();
            return db.query(tableName, projection, selection, selectionArgs, groupBy, having, sortOrder, limit);
        } else {
            return null;
        }
    }

    public Cursor rawQuery(String sql, String[] selectionArgs) {
        IDBConnection db = mDbConnector.getReadableConnection();
        return db.rawQuery(sql, selectionArgs);
    }

    public static void moveFromOldValues(ContentValues oldValues, ContentValues newValues, String... keys) {
        for (String key : keys) {
            Object value = oldValues.get(key);
            if (value != null && newValues.get(key) == null) {
                if (value instanceof Long) {
                    newValues.put(key, (Long) value);
                } else if (value instanceof Integer) {
                    newValues.put(key, (Integer) value);
                } else if (value instanceof String) {
                    if (!StringUtil.isEmpty(value)) {
                        newValues.put(key, (String) value);
                    }
                } else if (value instanceof Byte) {
                    newValues.put(key, (Byte) value);
                } else if (value instanceof byte[]) {
                    newValues.put(key, (byte[]) value);
                } else if (value instanceof Boolean) {
                    newValues.put(key, (Boolean) value);
                } else if (value instanceof Double) {
                    newValues.put(key, (Double) value);
                } else if (value instanceof Float) {
                    newValues.put(key, (Float) value);
                } else if (value instanceof Short) {
                    newValues.put(key, (Short) value);
                }
            }
        }
    }

    public static ContentValues duplicateContentValues(ContentValues contentValues) {
        ContentValues values = new ContentValues();
        Set<Entry<String, Object>> entries = contentValues.valueSet();
        for (Entry<String, Object> keyValue : entries) {
            values.put(keyValue.getKey(), String.valueOf(keyValue.getValue()));
        }
        return values;
    }

    public IDBConnection getWritableDbConnection() {
        return mDbConnector.getWritableConnection();
    }
}
