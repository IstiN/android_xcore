package by.istin.android.xcore.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

/**
 * 
 */
public class ManifestMetadataUtils {

	private static Object readKey(String keyName, Context context) {
		try {
			ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			Bundle bundle = appInfo.metaData;
			if (bundle == null) {
				return null;
			}
			Object value = bundle.get(keyName);
			return value;
		} catch (NameNotFoundException ex) {
			//return null if key not found in the manifest
			return null;
		}
	}

	public static String getString(Context context, String keyName) {
		return (String) readKey(keyName, context);
	}

	public static int getInt(Context context, String keyName) {
		return (Integer) readKey(keyName, context);
	}

	public static Boolean getBoolean(Context context, String keyName) {
		return (Boolean) readKey(keyName, context);
	}

	public static Object get(Context context, String keyName) {
		return readKey(keyName, context);
	}

}
