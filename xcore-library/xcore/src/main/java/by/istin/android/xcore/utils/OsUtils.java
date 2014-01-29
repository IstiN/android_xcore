package by.istin.android.xcore.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Uladzimir_Klyshevich on 1/29/14.
 */
public class OsUtils {

    private static final String SELECT_RUNTIME_PROPERTY = "persist.sys.dalvik.vm.lib";

    private static final String LIB_DALVIK = "libdvm.so";

    private static final String LIB_ART = "libart.so";

    private static final String LIB_ART_D = "libartd.so";

    private static volatile Boolean sIsDalvik = null;

    public static boolean isDalvik() {
        if (sIsDalvik == null) {
            sIsDalvik = !isArt();
        }
        return sIsDalvik;
    }

    private static boolean isArt() {
        try {
            Class<?> systemProperties = Class.forName("android.os.SystemProperties");
            try {
                Method get = systemProperties.getMethod("get",
                        String.class, String.class);
                if (get == null) {
                    //WTF?
                    return false;
                }
                try {
                    final String value = (String)get.invoke(
                            systemProperties, SELECT_RUNTIME_PROPERTY,
                        /* Assuming default is */"Dalvik");
                    if (LIB_DALVIK.equals(value)) {
                        return false;
                    } else if (LIB_ART.equals(value)) {
                        return true;
                    } else if (LIB_ART_D.equals(value)) {
                        return true;
                    }

                    return false;
                } catch (IllegalAccessException e) {
                    return false;
                } catch (IllegalArgumentException e) {
                    return false;
                } catch (InvocationTargetException e) {
                    return false;
                }
            } catch (NoSuchMethodException e) {
                return false;
            }
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
