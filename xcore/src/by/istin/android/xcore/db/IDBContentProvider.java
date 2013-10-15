package by.istin.android.xcore.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import by.istin.android.xcore.source.DataSourceRequest;

/**
 * Created with IntelliJ IDEA.
 * User: IstiN
 * Date: 12.10.13
 * Time: 15.31
 */
public interface IDBContentProvider {

    //Content provider methods
    boolean onCreate();

    int delete(Uri uri, String where, String[] whereArgs);

    Uri insert(Uri uri, ContentValues initialValues);

    int bulkInsert(Uri uri, ContentValues[] values);

    Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder);

    Cursor rawQuery(String sqlQuery, String[] selectionArgs);

    //DB methods methods
}
