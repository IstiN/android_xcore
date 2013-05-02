package by.istin.android.xcore.db;

import android.content.ContentValues;
import by.istin.android.xcore.source.DataSourceRequest;

public interface IMerge {

	void merge(DataSourceRequest dataSourceRequest, ContentValues oldValues, ContentValues newValues);
	
}
