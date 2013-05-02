package by.istin.android.xcore.db;

import android.content.ContentValues;
import by.istin.android.xcore.source.DataSourceRequest;

public interface IBeforeUpdate {

	void onBeforeUpdate(DataSourceRequest dataSourceRequest, ContentValues contentValues);
	
}
