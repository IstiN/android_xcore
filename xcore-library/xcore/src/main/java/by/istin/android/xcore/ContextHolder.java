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

	/**
	 * Gets the single instance of ContextHolder.
	 * @deprecated use get method
	 * @return single instance of ContextHolder
	 */
    @Deprecated()
	public static ContextHolder getInstance() {
		return sInstance;
	}

    public static Context get() {
        return sInstance.getContext();
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

	/**
	 * Sets the context.
	 * 
	 * @param pContext
	 *            the new context
	 */
	public void setContext(final Context pContext) {
		this.mContext = pContext;
	}

}
