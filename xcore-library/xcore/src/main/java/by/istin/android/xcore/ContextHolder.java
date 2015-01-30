package by.istin.android.xcore;

import android.content.Context;


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

}
