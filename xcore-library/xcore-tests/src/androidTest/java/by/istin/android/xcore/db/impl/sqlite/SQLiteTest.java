package by.istin.android.xcore.db.impl.sqlite;

import android.app.Application;
import android.test.ApplicationTestCase;

import by.istin.android.xcore.db.IDBConnector;

public class SQLiteTest extends ApplicationTestCase<Application> {

    public SQLiteTest() {
        super(Application.class);
    }

    private IDBConnector mDBConnector;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        createApplication();
        mDBConnector = new SQLiteConnector(getApplication(), getContext().getPackageName());
    }

    //TODO

}
