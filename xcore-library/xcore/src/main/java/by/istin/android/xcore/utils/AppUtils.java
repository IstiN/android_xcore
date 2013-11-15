package by.istin.android.xcore.utils;

import android.content.Context;

public class AppUtils {
	
	public static Object get(Context context, String name) {
		if (context == null || name == null){
			throw new IllegalArgumentException("Context and key must not be null");
		}
		Object systemService = context.getSystemService(name);
		if (systemService == null) {
			context = context.getApplicationContext();
			systemService = context.getSystemService(name);
		}
		if (systemService == null) {
			throw new IllegalStateException(name + " not available");
		}
		return systemService;
	}

}
