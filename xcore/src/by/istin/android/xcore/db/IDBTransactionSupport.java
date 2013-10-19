package by.istin.android.xcore.db;

/**
 * Created with IntelliJ IDEA.
 * User: IstiN
 * Date: 12.10.13
 */
public interface IDBTransactionSupport {

    void beginTransaction();

    void setTransactionSuccessful();

    void endTransaction();
}
