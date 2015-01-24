package by.istin.android.xcore;

import android.content.Context;


/**
 * The Class ContextHolder.
 */
public final class ContextHolder {

	/** The instance. */
	private static ContextHolder sInstance = new ContextHolder();

	/** The context. */
	private Context mContext;

	/**
	 * Instantiates a new context holder.
	 */
	private ContextHolder() {

	}

    public static Context get() {
        return sInstance.getContext();
    }

    /**
     * Sets the context.
     *
     * @param context
     *            the new context
     */
    public static void set(Context context) {
        sInstance.mContext = context;
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("Singleton");
	}

	/**
	 * Gets the context.
	 * 
	 * @return the context
	 */
	public Context getContext() {
		return mContext;
	}


}
