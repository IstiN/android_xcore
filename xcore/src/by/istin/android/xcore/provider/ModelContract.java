package by.istin.android.xcore.provider;

import android.content.Context;
import android.net.Uri;
import android.provider.BaseColumns;
import by.istin.android.xcore.ContextHolder;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.utils.StringUtil;

public class ModelContract {

	public static final String DATA_SOURCE_REQUEST_PARAM = "___dsr";

	private static final String AUTHORITY_TEMPLATE = "%s.ModelContentProvider";

	private static final String CONTENT_ID_TEMPLATE = "content://"
			+ "%s" + "/%s/%d";

	private static final String CONTENT_ALL_PAGINATED_TEMPLATE = "content://"
			+ "%s" + "/%s?offset=%d&size=%d";
	
	private static final String CONTENT_ALL_TEMPLATE = "content://"
			+ "%s" + "/%s";
	
	
	private static final String CONTENT_TYPE_TEMPLATE = "vnd.android.cursor.dir/%s";

	public static final int DEFAULT_OFFSET = 0;
	
	public static final int DEFAULT_SIZE = 100;

	public static final String SEGMENT_RAW_QUERY = "___srq";

	public static final String SQL_PARAM = "___sql";

	public static final String OBSERVER_URI_PARAM = "___ouri";
	
	public static final String SQL_QUERY_TEMPLATE = SEGMENT_RAW_QUERY+ "?"+SQL_PARAM + "=%s&"+OBSERVER_URI_PARAM + "=%s";
	
	private ModelContract() {
	}

	public static final class ModelColumns implements BaseColumns {
		
		private ModelColumns() {
		}

	}

	public static String getAuthority(Context ctx) {
		return String.format(AUTHORITY_TEMPLATE, ctx.getPackageName());
	}
	
	public static Uri getPaginatedUri(Class<?> clazz) {
		return getPaginatedUri(clazz, DEFAULT_OFFSET, DEFAULT_SIZE);
	}
	
	public static Uri getUri(Class<?> clazz) {
		return getUri(clazz.getCanonicalName());
	}
	
	public static Uri getPaginatedUri(Class<?> clazz, int offset, int size) {
		return getPaginatedUri(clazz.getCanonicalName(), offset, size);
	}
	
	public static Uri getPaginatedUri(String modelName) {
		return getPaginatedUri(modelName, DEFAULT_OFFSET, DEFAULT_SIZE);
	}
	
	public static Uri getPaginatedUri(String modelName, int offset, int size) {
		return Uri.parse(String.format(CONTENT_ALL_PAGINATED_TEMPLATE, getAuthority(ContextHolder.getInstance().getContext()), modelName, offset, size));
	}
	
	public static Uri getUri(String modelName) {
		return Uri.parse(String.format(CONTENT_ALL_TEMPLATE, getAuthority(ContextHolder.getInstance().getContext()), modelName));
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

	public static Uri getUri(DataSourceRequest dataSourceRequest, Class<?> clazz) {
		String uriParams = dataSourceRequest.toUriParams();
		Uri uri = getUri(clazz);
		String uriAsString = uri.toString();
		if (uriAsString.contains("?")) {
			uriAsString = uriAsString + "&";
		} else {
			uriAsString = uriAsString + "?";
		}
		return Uri.parse(uriAsString + DATA_SOURCE_REQUEST_PARAM + "="+StringUtil.encode(uriParams));
	}

	public static Uri getSQLQueryUri(String sql, Uri refreshUri) {
		return Uri.parse(String.format(CONTENT_ALL_TEMPLATE, getAuthority(ContextHolder.getInstance().getContext()),  String.format(SQL_QUERY_TEMPLATE, StringUtil.encode(sql), StringUtil.encode(refreshUri.toString()))));
	}

}
