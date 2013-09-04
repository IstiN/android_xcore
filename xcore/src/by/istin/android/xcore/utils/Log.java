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
import java.util.HashMap;


public class Log {

	private static final String TIME_ACTION = "time_action";

	private static final String COMMA = ",";

	private static final String MANIFEST_METADATA_LOG_KEY = "log";

	public enum Level {
		INFO, DEBUG, ERROR, OFF
	}
	
	public static Level[] level = new Level[]{Level.INFO, Level.DEBUG, Level.ERROR};
	
	public static boolean isOff = false;
	
	public static synchronized void init(Context context) {
		String logLevel = ManifestMetadataUtils.getString(context, MANIFEST_METADATA_LOG_KEY);
		if (logLevel == null || logLevel.length() == 0) {
			return;
		}
		String[] logValues = logLevel.split(COMMA);
		if (logValues != null && logValues.length != 0) {
			Level[] levels = new Level[logValues.length];
			for (int i = 0; i < logValues.length; i++) {
				Level l = Level.valueOf(logValues[i].trim().toUpperCase());
				if (l == Level.OFF) {
					isOff = true;
					break;
				}
				levels[i] = l;
			}
			if (!isOff) {
				level = levels;
			}
		}
	}
	
	private static boolean need(Level lev) {
		if (isOff) {
			return false;
		}
		for (Level l : level) {
			if (l == lev) {
				return true;
			}
		}
		return false;
	}
	
	public static void i(String tag, Object message) {
		if (need(Level.INFO)) {
			android.util.Log.i(tag, String.valueOf(message));
		}
	}
	
	public static void xi(Object tag, Object message) {
		if (need(Level.INFO)) {
			android.util.Log.i(tag.getClass().getSimpleName(), String.valueOf(message));
		}
	}
	
	public static void d(String tag, Object message) {
		if (need(Level.DEBUG)) {
			android.util.Log.d(tag, String.valueOf(message));
		}
	}
	
	public static void xd(Object tag, Object message) {
		if (need(Level.DEBUG)) {
			if (message instanceof HttpUriRequest) {
				android.util.Log.d(tag.getClass().getSimpleName(), "==============================");
				if (message instanceof HttpEntityEnclosingRequestBase) {
					HttpEntityEnclosingRequestBase request = (HttpEntityEnclosingRequestBase) message;
					android.util.Log.d(tag.getClass().getSimpleName(), "-" + request.getMethod()+":"+request.getURI());
					android.util.Log.d(tag.getClass().getSimpleName(), "-HEADERS:");
					Header[] allHeaders = request.getAllHeaders();
					for (Header header : allHeaders) {
						android.util.Log.d(tag.getClass().getSimpleName(), header.getName() + ":" + header.getValue());						
					}
					HttpEntity entity = request.getEntity();
					try {
						android.util.Log.d(tag.getClass().getSimpleName(), "-HTTP_ENTITY:" + EntityUtils.toString(entity));
					} catch (ParseException e) {
						android.util.Log.d(tag.getClass().getSimpleName(), "-HTTP_ENTITY: parse exception " + e.getMessage());
					} catch (IOException e) {
						android.util.Log.d(tag.getClass().getSimpleName(), "-HTTP_ENTITY: io exception " + e.getMessage());
					} catch (UnsupportedOperationException e) {
						android.util.Log.d(tag.getClass().getSimpleName(), "-HTTP_ENTITY: unsupported exception " + e.getMessage());
					}
				} else if (message instanceof HttpRequestBase) {
					HttpRequestBase httpRequestBase = (HttpRequestBase)message;
					android.util.Log.d(tag.getClass().getSimpleName(), "-" + httpRequestBase.getMethod()+":"+httpRequestBase.getURI());
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
		if (need(Level.ERROR)|| !isOff) {
			android.util.Log.e(tag, String.valueOf(message));
		}
	}
	
	public static void xe(Object tag, Object message) {
		if (need(Level.ERROR) || !isOff) {
			android.util.Log.e(tag.getClass().getSimpleName(), String.valueOf(message));
		}
	}
	
	public static void e(String tag, String message, Throwable e) {
		if (need(Level.ERROR) || !isOff) {
			android.util.Log.e(tag, String.valueOf(message), e);
		}
	}
	
	public static void xe(Object tag, String message, Throwable e) {
		if (need(Level.ERROR) || !isOff) {
			android.util.Log.e(tag.getClass().getSimpleName(), String.valueOf(message), e);
		}
	}
	
	private static HashMap<String, Long> sActionStorage = new HashMap<String, Long>();
	
	public static synchronized void startAction(String actionName) {
		if (need(Level.DEBUG)) {
			sActionStorage.put(actionName, System.currentTimeMillis());
		}
	}
	
	public static synchronized void endAction(String actionName) {
		if (need(Level.DEBUG)) {
			if (sActionStorage.get(actionName) != null) {
				d(TIME_ACTION, actionName + ":" + (System.currentTimeMillis() - sActionStorage.get(actionName)));
			}
			sActionStorage.remove(actionName);
		}
	}
}
