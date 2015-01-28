package by.istin.android.xcore;

import android.app.Application;

import java.util.List;

import by.istin.android.xcore.XCoreHelper.IAppServiceKey;
import by.istin.android.xcore.plugin.IFragmentPlugin;
import by.istin.android.xcore.provider.IDBContentProviderSupport;
import by.istin.android.xcore.provider.impl.DBContentProviderFactory;
import by.istin.android.xcore.utils.Log;


public abstract class CoreApplication extends Application {

    private XCoreHelper mXCoreHelper;

    public CoreApplication() {
        super();
        this.mXCoreHelper = XCoreHelper.get();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.xd(this, "xCoreHelper onCreate");
        this.mXCoreHelper.onCreate(this, getModules());
    }

    public abstract List<Class<? extends XCoreHelper.Module>> getModules();

    public void registerAppService(IAppServiceKey appService) {
        mXCoreHelper.registerAppService(appService);
    }

    public void addPlugin(IFragmentPlugin listFragmentPlugin) {
        mXCoreHelper.addPlugin(listFragmentPlugin);
    }

    public IDBContentProviderSupport getDefaultDBContentProvider(Class<?>[] entities) {
        return DBContentProviderFactory.getDefaultDBContentProvider(this, entities);
    }

    @Override
    public Object getSystemService(String name) {
        Object object = mXCoreHelper.getSystemService(name);
        if (object != null) {
            return object;
        }
        return super.getSystemService(name);
    }

}