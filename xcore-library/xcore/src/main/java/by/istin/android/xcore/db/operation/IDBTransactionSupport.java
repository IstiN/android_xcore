package by.istin.android.xcore.db.operation;

/**
 * Interface used to control transactions during work with db
 */
public interface IDBTransactionSupport {

    /**
     * Start new transaction
     */
    void beginTransaction();

    /**
     * Set transaction successful
     */
    void setTransactionSuccessful();

    /**
     * End transaction
     */
    void endTransaction();
}
