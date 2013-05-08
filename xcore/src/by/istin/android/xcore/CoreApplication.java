/**
 * 
 */
package by.istin.android.xcore;

import android.app.Application;
import by.istin.android.xcore.XCoreHelper.IAppServiceKey;

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

	@Override
	public Object getSystemService(String name) {
		Object object = mXCoreHelper.getSystemService(name);
		if (object != null) {
			return object;
		}
		return super.getSystemService(name);
	}
	
	
	
}
