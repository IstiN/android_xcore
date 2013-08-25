package by.istin.android.xcore.source;

import android.content.ContentValues;
import android.provider.BaseColumns;

import by.istin.android.xcore.annotations.dbBoolean;
import by.istin.android.xcore.annotations.dbLong;
import by.istin.android.xcore.annotations.dbString;
import by.istin.android.xcore.utils.HashUtils;

public class SyncDataSourceRequestEntity implements BaseColumns {

	@dbLong
	public static final String ID = _ID;
	
	@dbLong
	public static final String LAST_UPDATE = "last_update";
	
	@dbLong
	public static final String EXPIRATION = "expiration";
	
	@dbString
	public static final String URI = "uri";

	@dbString
	public static final String URI_PARAM = "uri_param";

	@dbString
	public static final String PARENT_URI = "parent_uri";

	@dbString
	public static final String DATASOURCE_KEY = "datasource_key";

	@dbString
	public static final String PROCESSOR_KEY = "processor_key";

	@dbBoolean
	public static final String IS_ERROR = "is_error";

	@dbLong
	public static final String LAST_CHANGED = "last_changed";

	public static ContentValues prepare(DataSourceRequest dataSourceRequest) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(ID, HashUtils.generateId(dataSourceRequest.getUri()));
		contentValues.put(LAST_UPDATE, System.currentTimeMillis());
		contentValues.put(EXPIRATION, dataSourceRequest.getCacheExpiration());
		contentValues.put(URI, dataSourceRequest.getUri());
		contentValues.put(URI_PARAM, dataSourceRequest.toUriParams());
		contentValues.put(PARENT_URI, dataSourceRequest.getRequestParentUri());
		contentValues.put(IS_ERROR, false);
		contentValues.put(LAST_CHANGED, System.currentTimeMillis());
		return contentValues;
	}
	
}