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

    private static IDBConnector sDbConnector;

    private static final Object sLock = new Object();

    @Override
    public IDBConnector createConnector(Context context) {
        synchronized (sLock) {
            //we need be sure, that have only one sqlite connector
            if (sDbConnector == null) {
                sDbConnector = new SQLiteConnector(context);
            }
        }
        return sDbConnector;
    }

}