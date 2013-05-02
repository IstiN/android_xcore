package by.istin.android.xcore.db;

import android.content.ContentValues;

public interface IMerge {

	void merge(ContentValues oldValues, ContentValues newValues);
	
}
