package by.istin.android.xcore;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

public class XCoreHelper {

	public static interface IAppServiceKey {
		
		String getAppServiceKey();
		
	}

	private Map<String, IAppServiceKey> mAppService = new HashMap<String, IAppServiceKey>();
	
	public void onCreate(Context ctx) {
		ContextHolder.getInstance().setContext(ctx);
	}
	
	public void registerAppService(IAppServiceKey appService) {
		mAppService.put(appService.getAppServiceKey(), appService);
	}
	
	public Object getSystemService(String name) {
		if (mAppService.containsKey(name)) {
			return mAppService.get(name);
		}
		return null;
	}
}
