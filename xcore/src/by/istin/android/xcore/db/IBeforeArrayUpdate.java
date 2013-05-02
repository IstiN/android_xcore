package by.istin.android.xcore.db;

import by.istin.android.xcore.source.DataSourceRequest;
import android.content.ContentValues;

public interface IBeforeArrayUpdate {

	void onBeforeListUpdate(DataSourceRequest dataSourceRequest, int position, ContentValues contentValues);
	
}
