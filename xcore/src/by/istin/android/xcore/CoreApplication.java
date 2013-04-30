/**
 * 
 */
package by.istin.android.xcore;

import java.util.HashMap;
import java.util.Map;

import android.app.Application;

/**
 * @author Uladzimir_Klyshevich
 *
 */
public class CoreApplication extends Application {

	public static interface IAppServiceKey {
		
		String getAppServiceKey();
		
	}
	
	private Map<String, IAppServiceKey> mAppService = new HashMap<String, IAppServiceKey>();
	
	public void registerAppService(IAppServiceKey appService) {
		mAppService.put(appService.getAppServiceKey(), appService);
	}

	@Override
	public Object getSystemService(String name) {
		if (mAppService.containsKey(name)) {
			return mAppService.get(name);
		}
		return super.getSystemService(name);
	}
	
	
	
}
