package by.istin.android.xcore;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import by.istin.android.xcore.db.impl.sqlite.SQLiteSupport;
import by.istin.android.xcore.plugin.IFragmentPlugin;
import by.istin.android.xcore.processor.impl.EmptyProcessor;
import by.istin.android.xcore.provider.IDBContentProviderSupport;
import by.istin.android.xcore.provider.impl.DBContentProviderSupport;
import by.istin.android.xcore.service.manager.IRequestManager;
import by.istin.android.xcore.source.DataSourceRequestEntity;
import by.istin.android.xcore.source.SyncDataSourceRequestEntity;
import by.istin.android.xcore.source.impl.EmptyDataSource;
import by.istin.android.xcore.utils.AppUtils;
import by.istin.android.xcore.utils.Log;
import by.istin.android.xcore.utils.ReflectUtils;

public class XCoreHelper {

    public static final String SYSTEM_SERVICE_KEY = "core:xcorehelper";
    public static final String APP_SERVICE_CONTENT_PROVIDER_PREFIX = "xcore:contentprovider:";
    public static final String REQUESTS_CONTENT_PROVIDER = "requests";
    public static final Class[] REQUESTS_ENTITIES = new Class[]{DataSourceRequestEntity.class, SyncDataSourceRequestEntity.class};

    private XCoreHelper() {
    }

    private static class XCoreHelperHolder {
        private static final XCoreHelper INSTANCE = new XCoreHelper();
    }

    public static XCoreHelper get() {
        return XCoreHelperHolder.INSTANCE;
    }

    public static XCoreHelper get(Context context) {
        return (XCoreHelper) AppUtils.get(context, SYSTEM_SERVICE_KEY);
    }

    public static interface IAppServiceKey {

        String getAppServiceKey();

    }


    public static interface Module {

        void onCreate(Context context, XCoreHelper coreHelper);

    }

    public static abstract class BaseModule implements Module {

        private XCoreHelper mCoreHelper;

        @Override
        public void onCreate(Context context, XCoreHelper coreHelper) {
            mCoreHelper = coreHelper;
            onCreate(context);
            mCoreHelper = null;
        }

        protected abstract void onCreate(Context context);

        protected void registerAppService(XCoreHelper.IAppServiceKey appServiceKey) {
            mCoreHelper.registerAppService(appServiceKey);
        }

        protected IDBContentProviderSupport registerContentProvider(Class<?>[] entities) {
            return mCoreHelper.registerContentProvider(entities);
        }

        protected void addPlugin(IFragmentPlugin fragmentPlugin) {
            mCoreHelper.addPlugin(fragmentPlugin);
        }

    }

    private final Map<String, IAppServiceKey> mAppServices = new HashMap<>();

    private List<IFragmentPlugin> mListFragmentPlugins;

    public List<IFragmentPlugin> getListFragmentPlugins() {
        return mListFragmentPlugins;
    }

    private boolean isOnCreateCalled = false;

    private boolean isModulesCreated = false;

    private final Object mLock = new Object();

    private Context mContext;

    private List<Class<? extends Module>> mModules;

    /**
     * Uses only in ContextHolder
     *
     * @return Context of current application
     */
    protected Context getContext() {
        if (!isOnCreateCalled) {
            throw new IllegalStateException("XCoreHelper onCreate did not call");
        }
        return mContext;
    }

    public void addPlugin(IFragmentPlugin listFragmentPlugin) {
        if (mListFragmentPlugins == null) {
            mListFragmentPlugins = new ArrayList<>();
        }
        mListFragmentPlugins.add(listFragmentPlugin);
    }

    public boolean isOnCreateCalled() {
        return isOnCreateCalled;
    }

    public void onCreate(Context ctx, List<Class<? extends XCoreHelper.Module>> modules, Class<?> clazz) {
        if (isOnCreateCalled) {
            return;
        } else {
            isOnCreateCalled = true;
        }
        mContext = ctx;
        Log.init(ctx, clazz);
        IRequestManager.Impl.register(this);
        registerAppService(new Core(ctx));
        registerAppService(new EmptyProcessor());
        registerAppService(new EmptyDataSource());
        registerContentProvider(REQUESTS_CONTENT_PROVIDER, REQUESTS_ENTITIES);
        mModules = modules;
    }

    public IDBContentProviderSupport getRequestsContentProvider() {
        return getContentProvider(REQUESTS_CONTENT_PROVIDER);
    }

    public IDBContentProviderSupport registerContentProvider(Class<?>[] entities) {
        return registerContentProvider(mContext.getPackageName(), entities);
    }

    public IDBContentProviderSupport registerContentProvider(final String name, Class<?>[] entities) {
        DBContentProviderSupport dbContentProviderSupport = new DBContentProviderSupport(mContext, new SQLiteSupport(name), entities) {
            @Override
            public String getAppServiceKey() {
                return getContentProviderKey(name);
            }
        };
        registerAppService(dbContentProviderSupport);
        return dbContentProviderSupport;
    }

    public static String getContentProviderKey(String name) {
        return APP_SERVICE_CONTENT_PROVIDER_PREFIX + name;
    }

    public IDBContentProviderSupport getContentProvider() {
        return getContentProvider(mContext.getPackageName());
    }

    public IDBContentProviderSupport getContentProvider(String name) {
        return AppUtils.get(mContext, getContentProviderKey(name));
    }

    public void registerAppService(IAppServiceKey appService) {
        mAppServices.put(appService.getAppServiceKey(), appService);
    }

    public Object getSystemService(String name) {
        if (!isModulesCreated) {
            synchronized (mLock) {
                if (!isModulesCreated) {
                    //kk and lollipop fix for appops key
                    if (mModules == null) {
                        return null;
                    }
                    isModulesCreated = true;
                    for (Class<? extends Module> moduleClass : mModules) {
                        Module module = ReflectUtils.newInstance(moduleClass);
                        module.onCreate(mContext, this);
                    }
                }
            }
        }
        if (!isOnCreateCalled) {
            throw new IllegalStateException("XCoreHelper onCreate did not call");
        }
        if (name.equals(SYSTEM_SERVICE_KEY)) {
            return this;
        }
        if (mAppServices.containsKey(name)) {
            return mAppServices.get(name);
        }
        return null;
    }
}
