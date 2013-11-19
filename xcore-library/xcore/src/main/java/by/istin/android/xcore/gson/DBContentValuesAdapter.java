package by.istin.android.xcore.gson;

import android.content.ContentValues;

import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.db.impl.DBHelper;
import by.istin.android.xcore.provider.IDBContentProviderSupport;
import by.istin.android.xcore.source.DataSourceRequest;

public class DBContentValuesAdapter extends AbstractDBContentValuesAdapter<ContentValues> {

    public DBContentValuesAdapter(Class<?> contentValuesClass, DataSourceRequest dataSourceRequest, IDBContentProviderSupport dbContentProvider) {
        super(contentValuesClass, dataSourceRequest, dbContentProvider);
    }

    public DBContentValuesAdapter(Class<?> contentValuesClass, DataSourceRequest dataSourceRequest, IDBConnection dbConnection, DBHelper dbHelper) {
        super(contentValuesClass, dataSourceRequest, dbConnection, dbHelper);
    }

    public DBContentValuesAdapter(Class<?> contentValuesClass, AbstractDBContentValuesAdapter dbContentValuesAdapter) {
        super(contentValuesClass, dbContentValuesAdapter);
    }
}