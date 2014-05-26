package by.istin.android.xcore.provider.impl;

import android.content.Context;
import by.istin.android.xcore.db.impl.sqlite.SQLiteSupport;
import by.istin.android.xcore.provider.IDBContentProviderSupport;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: IstiN
 * Date: 15.10.13
 */
public class DBContentProviderFactory {

    private DBContentProviderFactory() {}

    private static class DBContentProviderFactoryHolder {
        private static final DBContentProviderFactory INSTANCE = new DBContentProviderFactory();
    }

    public static DBContentProviderFactory getInstance() {
        return DBContentProviderFactoryHolder.INSTANCE;
    }

    public static IDBContentProviderSupport getDefaultDBContentProvider(Context context, Class<?> ... entities) {
        DBContentProviderFactory dbContentProviderFactory = getInstance();
        return dbContentProviderFactory.getDbContentProvider(context, DBContentProviderFactory.Type.SQLite, entities);
    }

    public static enum Type {
        SQLite
        //in future add more db types
    }

    private final Object mLock = new Object();

    private final Map<Type, IDBContentProviderSupport> mProviders = new HashMap<Type, IDBContentProviderSupport>();

    public IDBContentProviderSupport getDbContentProvider(Context context, Type type, Class<?> ... entities) {
        synchronized (mLock) {
            if (mProviders.containsKey(type)) {
                return mProviders.get(type);
            }
            DBContentProviderSupport dbContentProviderSupport = null;
            if (type == Type.SQLite) {
                dbContentProviderSupport = new DBContentProviderSupport(context, new SQLiteSupport(), entities);
                mProviders.put(type, dbContentProviderSupport);
            }
            return dbContentProviderSupport;
        }
    }

}
