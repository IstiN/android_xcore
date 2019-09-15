package by.istin.android.xcore;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;


/**
 * The Class ContextHolder.
 */
public final class ContextHolder {

    /**
     * Instantiates a new context holder.
     */
    private ContextHolder() {

    }

    /**
     * Gets Application context from XCoreHelper
     *
     * @return context of application
     */
    public static Context get() {
        return XCoreHelper.get().getContext();
    }


    public static Activity getActivity(Context context) {
        if (context == null) {
            return null;
        } else if (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            } else {
                return getActivity(((ContextWrapper) context).getBaseContext());
            }
        }
        return null;
    }


}
