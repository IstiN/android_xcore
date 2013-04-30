package by.istin.android.xcore;

import android.content.Context;


/**
 * The Class ContextHolder.
 */
public final class ContextHolder {

	/** The instance. */
	private static ContextHolder instance;

	/** The context. */
	private Context context;

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
		if (instance == null) {
			instance = new ContextHolder();
		}
		return instance;
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
		return context;
	}

	/**
	 * Sets the context.
	 * 
	 * @param pContext
	 *            the new context
	 */
	public void setContext(final Context pContext) {
		this.context = pContext;
	}

}
