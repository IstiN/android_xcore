package by.istin.android.xcore.fragment;

import android.content.Context;

import by.istin.android.xcore.source.DataSourceRequest;

/**
 * Created by IstiN on 14.7.13.
 */
public interface IDataSourceHelper {

    void dataSourceExecute(final Context context, final DataSourceRequest dataSourceRequest);

}
