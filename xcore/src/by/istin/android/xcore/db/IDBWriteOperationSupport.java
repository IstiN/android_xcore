package by.istin.android.xcore.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import by.istin.android.xcore.source.DataSourceRequest;

/**
 * Created with IntelliJ IDEA.
 * User: IstiN
 * Date: 12.10.13
 */
public interface IDBWriteOperationSupport {

    int delete(String className, String where, String[] whereArgs);

    long updateOrInsert(DataSourceRequest dataSourceRequest, String className, ContentValues initialValues);

}
