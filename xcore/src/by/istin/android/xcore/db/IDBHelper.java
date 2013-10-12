package by.istin.android.xcore.db;

import android.content.ContentValues;
import android.database.Cursor;
import by.istin.android.xcore.source.DataSourceRequest;

/**
 * Created with IntelliJ IDEA.
 * User: IstiN
 * Date: 12.10.13
 * Time: 15.31
 */
public interface IDBHelper {

    boolean onCreate();

    void delete(Class<?> clazz, String where, String whereArgs);

    long updateOrInsert(DataSourceRequest dataSourceRequest, Class<?> clazz, ContentValues contentValues);

    Cursor rawQuery(String sqlQuery, String[] selectionArgs);

}
