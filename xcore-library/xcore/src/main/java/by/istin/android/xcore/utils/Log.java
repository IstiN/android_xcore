package by.istin.android.xcore.utils;

import android.content.Context;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Log wrapper class, that enable Log if build variant is DEBUG.
 */
public final class Log {

    private static final String TIME_ACTION = "time_action";

    private static boolean sIsDebug = false;

    /**
     * Map for store action and start time of these actions.
     */
    private static final Map<String, Long> sActionStorage = new ConcurrentHashMap<>();

    private Log() {

    }

    public static boolean isDebug() {
        return sIsDebug;
    }

    public static synchronized void init(Context context) {
        String packageName = context.getPackageName();
        Class<?> buildConfigClass = ReflectUtils.classForName(packageName + ".BuildConfig");
        try {
            Field debug = buildConfigClass.getField("DEBUG");
            sIsDebug = (boolean) debug.get(null);
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
            if (message instanceof HttpUriRequest) {
                android.util.Log.d(tag.getClass().getSimpleName(), "==============================");
                if (message instanceof HttpEntityEnclosingRequestBase) {
                    HttpEntityEnclosingRequestBase request = (HttpEntityEnclosingRequestBase) message;
                    android.util.Log.d(tag.getClass().getSimpleName(), "-" + request.getMethod() + ":" + request.getURI());
                    android.util.Log.d(tag.getClass().getSimpleName(), "-HEADERS:");
                    Header[] allHeaders = request.getAllHeaders();
                    for (Header header : allHeaders) {
                        android.util.Log.d(tag.getClass().getSimpleName(), header.getName() + ":" + header.getValue());
                    }
                    HttpEntity entity = request.getEntity();
                    try {
                        if (entity == null) {
                            android.util.Log.d(tag.getClass().getSimpleName(), "-HTTP_ENTITY:");
                        } else {
                            android.util.Log.d(tag.getClass().getSimpleName(), "-HTTP_ENTITY:" + EntityUtils.toString(entity));
                        }
                    } catch (ParseException e) {
                        android.util.Log.d(tag.getClass().getSimpleName(), "-HTTP_ENTITY: parse exception " + e.getMessage());
                    } catch (IOException e) {
                        android.util.Log.d(tag.getClass().getSimpleName(), "-HTTP_ENTITY: io exception " + e.getMessage());
                    } catch (UnsupportedOperationException e) {
                        android.util.Log.d(tag.getClass().getSimpleName(), "-HTTP_ENTITY: unsupported exception " + e.getMessage());
                    }
                } else if (message instanceof HttpRequestBase) {
                    HttpRequestBase httpRequestBase = (HttpRequestBase) message;
                    android.util.Log.d(tag.getClass().getSimpleName(), "-" + httpRequestBase.getMethod() + ":" + httpRequestBase.getURI());
                    android.util.Log.d(tag.getClass().getSimpleName(), "-HEADERS:");
                    Header[] allHeaders = httpRequestBase.getAllHeaders();
                    for (Header header : allHeaders) {
                        android.util.Log.d(tag.getClass().getSimpleName(), header.getName() + ":" + header.getValue());
                    }
                } else {
                    android.util.Log.d(tag.getClass().getSimpleName(), String.valueOf(message));
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
            if (sActionStorage.get(actionName) != null) {
                resultTime = System.currentTimeMillis() - sActionStorage.get(actionName);
                d(TIME_ACTION, actionName + ":" + resultTime);
            }
            sActionStorage.remove(actionName);
        }
        return resultTime;
    }
}
