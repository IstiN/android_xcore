package by.istin.android.xcore.preference;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import by.istin.android.xcore.ContextHolder;
import by.istin.android.xcore.utils.BytesUtils;
import by.istin.android.xcore.utils.StringUtil;

public class PreferenceHelper {

    private static final String SETTINGS = "settings";
    public static final String STRING_ARRAY_DELIM = "====DELIM====";

    private static Preferences sPreferences;

    static {
        sPreferences = Preferences.Impl.newInstance(ContextHolder.get().getSharedPreferences(SETTINGS, Context.MODE_PRIVATE));
        sPreferences.initAsync();
    }

    public static Preferences editor() {
        return preferences();
    }

    public static Preferences preferences() {
        return sPreferences;
    }

    public static void set(String key, boolean value) {
        Preferences editor = editor();
        editor.set(key, value);
    }

    public static boolean getBoolean(String key, boolean def) {
        return preferences().getBoolean(key, def);
    }

    public static void set(String key, int value) {
        Preferences editor = editor();
        editor.set(key, value);
    }

    public static int getInt(String key, int def) {
        return preferences().getInt(key, def);
    }

    public static void set(String key, long value) {
        Preferences editor = editor();
        editor.set(key, value);
    }

    public static void clear() {
        Preferences editor = editor();
        editor.clear();
    }

    public static long getLong(String key, long def) {
        return preferences().getLong(key, def);
    }

    public static void set(String key, float value) {
        Preferences editor = editor();
        editor.set(key, value);
    }

    public static float getFloat(String key, float def) {
        return preferences().getFloat(key, def);
    }

    public static void set(String key, String value) {
        Preferences editor = editor();
        editor.set(key, value);
    }

    public static String getString(String key, String def) {
        return preferences().getString(key, def);
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
        Preferences editor = editor();
        if (byteArray != null) {
            editor.set(key, StringUtil.newString(byteArray));
        } else {
            editor.set(key, (String)null);
        }
    }

    public static byte[] getByteArray(String key, byte[] defValue) {
        String value = preferences().getString(key, null);
        if (value == null) {
            return defValue;
        }
        return StringUtil.getBytes(value);
    }

    public static void set(String key, Bundle bundle) {
        byte[] byteArray = BytesUtils.toByteArray(bundle);
        set(key, byteArray);
    }

    public static Bundle getBundle(String key, Bundle bundle) {
        String value = preferences().getString(key, null);
        if (value == null) {
            return bundle;
        }
        return BytesUtils.bundleFromByteArray(StringUtil.getBytes(value));
    }

    public static String[] getStringArray(String key, String[] defValue) {
        String value = getString(key, null);
        if (value == null) {
            return defValue;
        }
        return value.split(STRING_ARRAY_DELIM);
    }

    public static void set(String key, String[] value) {
        if (value == null) {
            set(key, (String) null);
        } else {
            set(key, StringUtil.join(STRING_ARRAY_DELIM, false, value));
        }
    }

}