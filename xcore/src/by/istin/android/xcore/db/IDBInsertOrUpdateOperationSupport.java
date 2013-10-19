package by.istin.android.xcore.db;

import android.content.ContentValues;
import by.istin.android.xcore.source.DataSourceRequest;

/**
 * Created with IntelliJ IDEA.
 * User: IstiN
 * Date: 12.10.13
 */
public interface IDBInsertOrUpdateOperationSupport {

    long updateOrInsert(DataSourceRequest dataSourceRequest, String className, ContentValues initialValues);

}
