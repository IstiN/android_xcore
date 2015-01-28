package by.istin.android.xcore.provider.impl;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import by.istin.android.xcore.db.IDBSupport;
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
        String name = context.getPackageName();
        return dbContentProviderFactory.getDbContentProvider(context, name, new SQLiteSupport(name), entities);
    }

    private final Object mLock = new Object();

    private final Map<String, IDBContentProviderSupport> mProviders = new HashMap<>();

    public IDBContentProviderSupport getDbContentProvider(Context context, String name, IDBSupport dbSupport, Class<?> ... entities) {
        IDBContentProviderSupport dbContentProviderSupport = mProviders.get(name);
        if (dbContentProviderSupport == null) {
            synchronized (mLock) {
                return registerContentProvider(context, name, dbSupport, entities);
            }
        }
        return dbContentProviderSupport;
    }

    private IDBContentProviderSupport registerContentProvider(Context context, String name, IDBSupport dbSupport, Class<?>[] entities) {
        IDBContentProviderSupport dbContentProviderSupport;
        dbContentProviderSupport = mProviders.get(name);
        if (dbContentProviderSupport != null) {
            return dbContentProviderSupport;
        }
        dbContentProviderSupport = new DBContentProviderSupport(context, dbSupport, entities);
        mProviders.put(name, dbContentProviderSupport);
        return dbContentProviderSupport;
    }

}
