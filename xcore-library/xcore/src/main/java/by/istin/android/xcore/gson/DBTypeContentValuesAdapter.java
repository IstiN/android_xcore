package by.istin.android.xcore.gson;

import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.db.impl.DBHelper;
import by.istin.android.xcore.provider.IDBContentProviderSupport;
import by.istin.android.xcore.source.DataSourceRequest;

public class DBTypeContentValuesAdapter extends AbstractDBContentValuesAdapter<TypeContentValues> {

    public DBTypeContentValuesAdapter(Class<?> contentValuesClass, DataSourceRequest dataSourceRequest, IDBContentProviderSupport dbContentProvider) {
        super(contentValuesClass, dataSourceRequest, dbContentProvider);
    }

    public DBTypeContentValuesAdapter(Class<?> contentValuesClass, DataSourceRequest dataSourceRequest, IDBConnection dbConnection, DBHelper dbHelper) {
        super(contentValuesClass, dataSourceRequest, dbConnection, dbHelper);
    }

    public DBTypeContentValuesAdapter(Class<?> contentValuesClass, AbstractDBContentValuesAdapter dbContentValuesAdapter) {
        super(contentValuesClass, dbContentValuesAdapter);
    }
}