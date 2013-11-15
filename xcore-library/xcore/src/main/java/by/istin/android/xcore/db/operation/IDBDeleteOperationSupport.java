package by.istin.android.xcore.db.operation;

/**
 * Created with IntelliJ IDEA.
 * User: IstiN
 * Date: 12.10.13
 */
public interface IDBDeleteOperationSupport {

    int delete(String tableName, String where, String[] whereArgs);

}
