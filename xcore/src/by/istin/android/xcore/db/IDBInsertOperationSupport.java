package by.istin.android.xcore.db;

import android.content.ContentValues;

/**
 * Created with IntelliJ IDEA.
 * User: IstiN
 * Date: 12.10.13
 */
public interface IDBInsertOperationSupport {

    long insert(String tableName, ContentValues contentValues);

}
