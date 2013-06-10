package by.istin.android.xcore.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import by.istin.android.xcore.ContextHolder;
import by.istin.android.xcore.utils.BytesUtils;

public class PreferenceHelper {
	
	private static final String SETTINGS = "settings";
	
	public static void set(String key, boolean value) {
		Context ctx = ContextHolder.getInstance().getContext();
		Editor editor = ctx.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE)
				.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
	
	public static boolean getBoolean(String key, boolean def) {
		Context ctx = ContextHolder.getInstance().getContext();
		SharedPreferences savedSession = ctx.getSharedPreferences(SETTINGS,
				Context.MODE_PRIVATE);
		return savedSession.getBoolean(key, def);
	}
	
	public static void set(String key, int value) {
		Context ctx = ContextHolder.getInstance().getContext();
		Editor editor = ctx.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE)
				.edit();
		editor.putInt(key, value);
		editor.commit();
	}
	
	public static int getInt(String key, int def) {
		Context ctx = ContextHolder.getInstance().getContext();
		SharedPreferences savedSession = ctx.getSharedPreferences(SETTINGS,
				Context.MODE_PRIVATE);
		return savedSession.getInt(key, def);
	}
	
	public static void set(String key, long value) {
		Context ctx = ContextHolder.getInstance().getContext();
		Editor editor = ctx.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE)
				.edit();
		editor.putLong(key, value);
		editor.commit();
	}
	
	public static void clear() {
		Editor editor = ContextHolder.getInstance().getContext().getSharedPreferences(SETTINGS, Context.MODE_PRIVATE)
				.edit();
		editor.clear();
		editor.commit();
	}
	
	public static long getLong(String key, long def) {
		Context ctx = ContextHolder.getInstance().getContext();
		SharedPreferences savedSession = ctx.getSharedPreferences(SETTINGS,
				Context.MODE_PRIVATE);
		return savedSession.getLong(key, def);
	}
	
	public static void set(String key, float value) {
		Context ctx = ContextHolder.getInstance().getContext();
		Editor editor = ctx.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE)
				.edit();
		editor.putFloat(key, value);
		editor.commit();
	}
	
	public static float getFloat(String key, float def) {
		Context ctx = ContextHolder.getInstance().getContext();
		SharedPreferences savedSession = ctx.getSharedPreferences(SETTINGS,
				Context.MODE_PRIVATE);
		return savedSession.getFloat(key, def);
	}
	
	public static void set(String key, String value) {
		Context ctx = ContextHolder.getInstance().getContext();
		Editor editor = ctx.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE)
				.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	public static String getString(String key, String def) {
		Context ctx = ContextHolder.getInstance().getContext();
		SharedPreferences savedSession = ctx.getSharedPreferences(SETTINGS,
				Context.MODE_PRIVATE);
		return savedSession.getString(key, def);
	}
	
	public static void set(String key, Uri value) {
		set(key, value.toString());
	}
	
	public static Uri getUri(String key, Uri def) {
		String value = getString(key, null);
		if (value == null) {
			return def;
		}
		return Uri.parse(value);
	}

	public static void set(String key, byte[] byteArray) {
		Context ctx = ContextHolder.getInstance().getContext();
		Editor editor = ctx.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE)
				.edit();
		if (byteArray != null) {
			editor.putString(key, new String(byteArray));
		} else {
			editor.putString(key, null);
		}
		editor.commit();		
	}
	
	public static byte[] getByteArray(String key, byte[] defValue) {
		Context ctx = ContextHolder.getInstance().getContext();
		SharedPreferences savedSession = ctx.getSharedPreferences(SETTINGS,
				Context.MODE_PRIVATE);
		String value = savedSession.getString(key, null);
		if (value == null) {
			return defValue;
		}
		return value.getBytes();
	}
	
	public static void set(String key, Bundle bundle) {
		byte[] byteArray = BytesUtils.toByteArray(bundle);
		set(key, byteArray);
	}
	
	public static Bundle getBundle(String key, Bundle bundle) {
		Context ctx = ContextHolder.getInstance().getContext();
		SharedPreferences savedSession = ctx.getSharedPreferences(SETTINGS,
				Context.MODE_PRIVATE);
		String value = savedSession.getString(key, null);
		if (value == null) {
			return bundle;
		}
		return BytesUtils.bundleFromByteArray(value.getBytes());
	}
}