package by.istin.android.xcore.db.impl.sqlite;

import android.content.Context;
import by.istin.android.xcore.db.IDBConnector;
import by.istin.android.xcore.db.impl.AbstractDBSupport;

/**
 * Created with IntelliJ IDEA.
 * User: IstiN
 * Date: 15.10.13
 */
public class SQLiteSupport extends AbstractDBSupport {

    private volatile IDBConnector mDbConnector;

    @Override
    public IDBConnector createConnector(Context context) {
        IDBConnector dbConnector = mDbConnector;
        if (dbConnector == null) {
            synchronized (this) {
                //we need be sure, that have only one sqlite connector
                dbConnector = mDbConnector;
                if (dbConnector == null) {
                    mDbConnector = dbConnector = new SQLiteConnector(context);
                }
            }
        }
        return dbConnector;
    }

}