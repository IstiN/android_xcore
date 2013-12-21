package by.istin.android.xcore;

import android.content.Context;


/**
 * The Class ContextHolder.
 */
public final class ContextHolder {

	/** The instance. */
	private static ContextHolder sInstance;

	/** The context. */
	private Context mContext;

	/**
	 * Instantiates a new context holder.
	 */
	private ContextHolder() {

	}

	/**
	 * Gets the single instance of ContextHolder.
	 * 
	 * @return single instance of ContextHolder
	 */
	public static synchronized ContextHolder getInstance() {
		if (sInstance == null) {
            sInstance = new ContextHolder();
		}
		return sInstance;
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
