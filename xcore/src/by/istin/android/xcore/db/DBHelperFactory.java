package by.istin.android.xcore.db;

import by.istin.android.xcore.ContextHolder;
import by.istin.android.xcore.db.DBHelper;

/**
 * Created with IntelliJ IDEA.
 * User: IstiN
 * Date: 12.10.13
 * Time: 15.13
 */
public class DBHelperFactory {

    private DBHelperFactory () {}

    private static class DBHelperHolder {
        private static final DBHelper INSTANCE = new DBHelper(ContextHolder.getInstance().getContext());
    }

    public static DBHelper getInstance() {
        return DBHelperHolder.INSTANCE;
    }

}
