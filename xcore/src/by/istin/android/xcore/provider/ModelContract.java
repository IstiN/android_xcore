package by.istin.android.xcore.provider;

import android.content.Context;
import android.net.Uri;
import android.provider.BaseColumns;
import by.istin.android.xcore.ContextHolder;

public class ModelContract {

	private static final String AUTHORITY_TEMPLATE = "%s.ModelContentProvider";

	private static final String CONTENT_ID_TEMPLATE = "content://"
			+ "%s" + "/%s/%d";

	private static final String CONTENT_ALL_TEMPLATE = "content://"
			+ "%s" + "/%s?offset=%d&size=%d";
	
	private static final String CONTENT_TYPE_TEMPLATE = "vnd.android.cursor.dir/%s";

	public static final int DEFAULT_OFFSET = 0;
	
	public static final int DEFAULT_SIZE = 100;
	
	private ModelContract() {
	}

	public static final class ModelColumns implements BaseColumns {
		
		private ModelColumns() {
		}

	}

	public static String getAuthority(Context ctx) {
		return String.format(AUTHORITY_TEMPLATE, ctx.getPackageName());
	}
	
	public static Uri getUri(Class<?> clazz) {
		return getUri(clazz, DEFAULT_OFFSET, DEFAULT_SIZE);
	}
	
	public static Uri getUri(Class<?> clazz, int offset, int size) {
		return getUri(clazz.getCanonicalName(), offset, size);
	}
	
	public static Uri getUri(String modelName) {
		return getUri(modelName, DEFAULT_OFFSET, DEFAULT_SIZE);
	}
	
	public static Uri getUri(String modelName, int offset, int size) {
		return Uri.parse(String.format(CONTENT_ALL_TEMPLATE, getAuthority(ContextHolder.getInstance().getContext()), modelName, offset, size));
	}
	
	public static Uri getUri(Class<?> clazz, Long id) {
		return Uri.parse(String.format(CONTENT_ID_TEMPLATE, getAuthority(ContextHolder.getInstance().getContext()), clazz.getCanonicalName(), id));
	}
	
	public static String getContentType(Class<?> clazz) {
		return String.format(CONTENT_TYPE_TEMPLATE, clazz.getCanonicalName());
	}
	
	public static String getContentType(String modelName) {
		return String.format(CONTENT_TYPE_TEMPLATE, modelName);
	}

}
