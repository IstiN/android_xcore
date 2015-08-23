package by.istin.android.xcore.utils;

import android.content.Context;


import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import by.istin.android.xcore.source.impl.http.HttpRequest;

/**
 * Log wrapper class, that enable Log if build variant is DEBUG.
 */
public final class Log {

    private static final String TIME_ACTION = "time_action";

    private static boolean sIsDebug = false;

    private static int sVersionCode;

    private static String sVersionName;

    /**
     * Map for store action and start time of these actions.
     */
    private static final Map<String, Long> sActionStorage = new ConcurrentHashMap<>();

    private Log() {

    }

    public static boolean isDebug() {
        return sIsDebug;
    }

    public static int getVersionCode() {
        return sVersionCode;
    }

    public static String getVersionName() {
        return sVersionName;
    }

    public static synchronized void init(Context context, Class<?> clazz) {
        if (clazz == null) {
            String packageName = context.getPackageName();
            clazz = ReflectUtils.classForName(packageName + ".BuildConfig");
        }
        try {
            Field debug = clazz.getField("DEBUG");
            sIsDebug = (boolean) debug.get(null);
            sVersionCode = (int) clazz.getField("VERSION_CODE").get(null);
            sVersionName = (String) clazz.getField("VERSION_NAME").get(null);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static void off(boolean isOff) {
        sIsDebug = isOff;
    }


    public static void i(String tag, Object message) {
        if (sIsDebug) {
            android.util.Log.i(tag, String.valueOf(message));
        }
    }

    public static void xi(Object tag, Object message) {
        if (sIsDebug) {
            android.util.Log.i(tag.getClass().getSimpleName(), String.valueOf(message));
        }
    }

    public static void d(String tag, Object message) {
        if (sIsDebug) {
            android.util.Log.d(tag, String.valueOf(message));
        }
    }

    public static void xd(Object tag, Object message) {
        if (sIsDebug) {
            if (message instanceof HttpRequest) {
                android.util.Log.d(tag.getClass().getSimpleName(), "==============================");
                HttpRequest httpRequest = (HttpRequest) message;
                android.util.Log.d(tag.getClass().getSimpleName(), "-" + httpRequest.toString());
                android.util.Log.d(tag.getClass().getSimpleName(), "-HEADERS:");

                Map<String, List<String>> headers = httpRequest.headers();
                if (headers != null && !headers.isEmpty()) {
                    Set<String> strings = headers.keySet();
                    for (String key : strings) {
                        android.util.Log.d(tag.getClass().getSimpleName(), key + ":" + StringUtil.joinAll(",", headers.get(key)));
                    }
                }
                android.util.Log.d(tag.getClass().getSimpleName(), "==============================");
            } else {
                android.util.Log.d(tag.getClass().getSimpleName(), String.valueOf(message));
            }
        }
    }

    public static void e(String tag, Object message) {
        if (sIsDebug) {
            android.util.Log.e(tag, String.valueOf(message));
        }
    }

    public static void w(String tag, Object message) {
        if (sIsDebug) {
            android.util.Log.w(tag, String.valueOf(message));
        }
    }

    public static void xe(Object tag, Object message) {
        if (sIsDebug) {
            android.util.Log.e(tag.getClass().getSimpleName(), String.valueOf(message));
        }
    }

    public static void e(String tag, String message, Throwable e) {
        if (sIsDebug) {
            android.util.Log.e(tag, String.valueOf(message), e);
        }
    }

    public static void xe(Object tag, String message, Throwable e) {
        if (sIsDebug) {
            android.util.Log.e(tag.getClass().getSimpleName(), String.valueOf(message), e);
        }
    }

    public static void startAction(String actionName) {
        startAction(actionName, true);
    }

    public static void startAction(String actionName, boolean isCheckLevel) {
        if (sIsDebug || isCheckLevel) {
            sActionStorage.put(actionName, System.currentTimeMillis());
        }
    }

    public static long endAction(String actionName) {
        return endAction(actionName, true);
    }


    public static long endAction(String actionName, boolean isCheckLevel) {
        long resultTime = 0l;
        if (sIsDebug || isCheckLevel) {
            Long startTime = sActionStorage.get(actionName);
            if (startTime != null) {
                resultTime = System.currentTimeMillis() - startTime;
                d(TIME_ACTION, actionName + ":" + resultTime);
            }
            sActionStorage.remove(actionName);
        }
        return resultTime;
    }
}
