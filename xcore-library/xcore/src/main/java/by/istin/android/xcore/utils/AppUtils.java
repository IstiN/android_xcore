package by.istin.android.xcore.utils;

import android.content.Context;


public class AppUtils {

    public static <T> T get(Context context, String name) {
        if (context == null || name == null) {
            throw new IllegalArgumentException("Context and key must not be null");
        }
        T systemService = (T) context.getSystemService(name);
        if (systemService == null) {
            context = context.getApplicationContext();
            systemService = (T) context.getSystemService(name);
        }
        if (systemService == null) {
            throw new IllegalStateException(name + " not available");
        }
        return systemService;
    }

}
