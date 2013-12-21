/**
 * 
 */
package by.istin.android.xcore;

import android.app.Application;

import by.istin.android.xcore.XCoreHelper.IAppServiceKey;
import by.istin.android.xcore.plugin.IXListFragmentPlugin;
import by.istin.android.xcore.provider.IDBContentProviderSupport;
import by.istin.android.xcore.provider.impl.DBContentProviderFactory;

/**
 * @author Uladzimir_Klyshevich
 *
 */
public class CoreApplication extends Application {

	private XCoreHelper mXCoreHelper;

    //KitKat workaround
    private volatile Object mLock = new Object();

	@Override
	public void onCreate() {
        synchronized (mLock) {
            mXCoreHelper = new XCoreHelper();
            mXCoreHelper.onCreate(this);
            super.onCreate();
        }
	}

	public void registerAppService(IAppServiceKey appService) {
		mXCoreHelper.registerAppService(appService);
	}

    public void addPlugin(IXListFragmentPlugin listFragmentPlugin) {
        mXCoreHelper.addPlugin(listFragmentPlugin);
    }

    public IDBContentProviderSupport getDefaultDBContentProvider(Class<?>[] entities) {
        return DBContentProviderFactory.getDefaultDBContentProvider(this, entities);
    }

	@Override
	public Object getSystemService(String name) {
        synchronized (mLock) {
            if (mXCoreHelper == null) {
                onCreate();
            }
        }
		Object object = mXCoreHelper.getSystemService(name);
		if (object != null) {
			return object;
		}
		return super.getSystemService(name);
	}

}