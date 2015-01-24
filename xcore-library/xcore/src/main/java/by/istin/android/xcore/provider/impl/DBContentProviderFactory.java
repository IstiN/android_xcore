package by.istin.android.xcore.provider.impl;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import by.istin.android.xcore.db.impl.sqlite.SQLiteSupport;
import by.istin.android.xcore.provider.IDBContentProviderSupport;

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
        return dbContentProviderFactory.getDbContentProvider(context, context.getPackageName(), entities);
    }

    private final Object mLock = new Object();

    private final Map<String, IDBContentProviderSupport> mProviders = new HashMap<>();

    public IDBContentProviderSupport getDbContentProvider(Context context, String name, Class<?> ... entities) {
        IDBContentProviderSupport dbContentProviderSupport = mProviders.get(name);
        if (dbContentProviderSupport == null) {
            synchronized (mLock) {
                dbContentProviderSupport = mProviders.get(name);
                if (dbContentProviderSupport != null) {
                    return dbContentProviderSupport;
                }
                dbContentProviderSupport = new DBContentProviderSupport(context, new SQLiteSupport(name), entities);
                mProviders.put(name, dbContentProviderSupport);
                return dbContentProviderSupport;
            }
        }
        return dbContentProviderSupport;
    }

}
