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

    public SQLiteSupport(String name) {
        super(name);
    }

    @Override
    public IDBConnector createConnector(String name, Context context) {
        return new SQLiteConnector(context, getName());
    }

}