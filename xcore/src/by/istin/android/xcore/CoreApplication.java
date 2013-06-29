/**
 * 
 */
package by.istin.android.xcore;

import android.app.Application;
import by.istin.android.xcore.XCoreHelper.IAppServiceKey;
import by.istin.android.xcore.plugin.IXListFragmentPlugin;

/**
 * @author Uladzimir_Klyshevich
 *
 */
public class CoreApplication extends Application {

	private XCoreHelper mXCoreHelper;
	
	@Override
	public void onCreate() {
		mXCoreHelper = new XCoreHelper();
		mXCoreHelper.onCreate(this);
		super.onCreate();
	}

	public void registerAppService(IAppServiceKey appService) {
		mXCoreHelper.registerAppService(appService);
	}

    public void addPlugin(IXListFragmentPlugin listFragmentPlugin) {
        mXCoreHelper.addPlugin(listFragmentPlugin);
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