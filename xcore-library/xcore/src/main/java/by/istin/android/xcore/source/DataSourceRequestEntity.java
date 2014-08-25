package by.istin.android.xcore.source;

import android.content.ContentValues;
import android.provider.BaseColumns;

import by.istin.android.xcore.annotations.dbLong;
import by.istin.android.xcore.annotations.dbString;
import by.istin.android.xcore.utils.HashUtils;

public class DataSourceRequestEntity implements BaseColumns {

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
	public static final String PROCESSOR_KEY = "processor_key";

	@dbString
	public static final String DATA_SOURCE_KEY = "data_source_key";

	public static ContentValues prepare(DataSourceRequest dataSourceRequest, String processorKey, String dataSourceKey) {
		ContentValues contentValues = new ContentValues();
        String uriParams = dataSourceRequest.toUriParams();
		contentValues.put(ID, generateId(dataSourceRequest));
		contentValues.put(LAST_UPDATE, System.currentTimeMillis());
		contentValues.put(EXPIRATION, dataSourceRequest.getCacheExpiration());
		contentValues.put(URI, dataSourceRequest.getUri());
        contentValues.put(URI_PARAM, uriParams);
		contentValues.put(PARENT_URI, dataSourceRequest.getRequestParentUri());
		contentValues.put(PROCESSOR_KEY, processorKey);
		contentValues.put(DATA_SOURCE_KEY, dataSourceKey);
		return contentValues;
	}

    public static long generateId(DataSourceRequest dataSourceRequest) {
        return HashUtils.generateId(dataSourceRequest.getUri());
    }

}