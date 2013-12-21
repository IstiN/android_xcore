package by.istin.android.xcore.db.impl.sqlite;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.utils.CursorUtils;

/**
 * Created with IntelliJ IDEA.
 * User: IstiN
 * Date: 19.10.13
 */
class SQLiteConnection implements IDBConnection {

    private SQLiteDatabase mDatabase;

    SQLiteConnection(SQLiteDatabase database) {
        mDatabase = database;
    }

    @Override
    public void execSQL(String sql) {
        mDatabase.execSQL(sql);
    }

    @Override
    public Cursor query(String table, String[] projection, String selection, String[] selectionArgs, String groupBy, String having, String sortOrder, String limit) {
        return mDatabase.query(table, projection, selection, selectionArgs, groupBy, having, sortOrder, limit);
    }

    @Override
    public boolean isExists(String tableName) {
        boolean isExists = false;
        Cursor cursor = null;
        try {
            cursor = mDatabase.query("sqlite_master", new String[]{"name"}, "type=? AND name=?", new String[]{"table", tableName}, null, null, null);
            isExists = !CursorUtils.isEmpty(cursor);
        } finally {
            CursorUtils.close(cursor);
        }
        return isExists;
    }

    @Override
    public Cursor rawQuery(String sql, String[] selectionArgs) {
        return mDatabase.rawQuery(sql, selectionArgs);
    }

    @Override
    public long insert(String tableName, ContentValues contentValues) {
        return mDatabase.insert(tableName, null, contentValues);
    }

    //TODO maybe it improve insert in future
    /*private void insertWithStatement(IDBConnection db, Class<?> clazz, ContentValues contentValues, String tableName) {
        SQLiteStatement insertStatement = dbAssociationCache.getInsertStatement(clazz);
        if (insertStatement == null) {
            List<Field> fields = ReflectUtils.getEntityKeys(clazz);
            List<String> columns = new ArrayList<String>();
            for (Field field : fields) {
                if (field.isAnnotationPresent(dbEntity.class)) {
                    continue;
                }
                if (field.isAnnotationPresent(dbEntities.class)) {
                    continue;
                }
                String name = ReflectUtils.getStaticStringValue(field);
                columns.add(name);
            }
            insertStatement = db.compileStatement(createInsert(tableName, columns.toArray(new String[columns.size()])));
            dbAssociationCache.setInsertStatement(clazz, insertStatement);
        }
        insertStatement.clearBindings();
        Set<String> keys = contentValues.keySet();
        String[] values = new String[keys.size()];
        int i = 0;
        for (String key : keys) {
            values[i] = contentValues.getAsString(key);
            i++;
        }
        insertStatement.bindAllArgsAsStrings(values);
        insertStatement.execute();
    }

    static public String createInsert(final String tableName, final String[] columnNames) {
        if (tableName == null || columnNames == null || columnNames.length == 0) {
            throw new IllegalArgumentException();
        }
        final StringBuilder s = new StringBuilder();
        s.append("INSERT OR REPLACE INTO ").append(tableName).append(" (");
        for (String column : columnNames) {
            s.append(column).append(" ,");
        }
        int length = s.length();
        s.delete(length - 2, length);
        s.append(") VALUES( ");
        for (int i = 0; i < columnNames.length; i++) {
            s.append(" ? ,");
        }
        length = s.length();
        s.delete(length - 2, length);
        s.append(")");
        return s.toString();
    }*/

    @Override
    public int update(String tableName, ContentValues contentValues, String selection, String[] selectionArgs) {
        return mDatabase.update(tableName, contentValues, selection, selectionArgs);
    }

    @Override
    @TargetApi(value = Build.VERSION_CODES.HONEYCOMB)
    public void beginTransaction() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mDatabase.beginTransactionNonExclusive();
        } else {
            mDatabase.beginTransaction();
        }
    }

    @Override
    public void setTransactionSuccessful() {
        mDatabase.setTransactionSuccessful();
    }

    @Override
    public void endTransaction() {
        mDatabase.endTransaction();
    }

    @Override
    public int delete(String tableName, String where, String[] whereArgs) {
        return mDatabase.delete(tableName, where, whereArgs);
    }

}