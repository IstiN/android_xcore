package by.istin.android.xcore.db;

/**
 * Created with IntelliJ IDEA.
 * User: IstiN
 * Date: 18.10.13
 */
public interface IDBConnector extends IDBBatchOperationSupport {

    IDBConnection getWritableConnection();

    IDBConnection getReadableConnection();

    String getCreateFilesTableSQLTemplate(String table);

    void execSQL(String sql);
}
