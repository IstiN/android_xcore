package by.istin.android.xcore.db;

/**
 * Created with IntelliJ IDEA.
 * User: IstiN
 * Date: 18.10.13
 */
public interface IDBConnector {

    IDBConnection getWritableConnection();

    IDBConnection getReadableConnection();

    String getCreateTableSQLTemplate(String table);

}