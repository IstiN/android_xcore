package by.istin.android.xcore.error;

import android.support.v4.app.FragmentActivity;

import by.istin.android.xcore.XCoreHelper;
import by.istin.android.xcore.fragment.IDataSourceHelper;
import by.istin.android.xcore.source.DataSourceRequest;

/**
 * Created by IstiN on 14.7.13.
 */
public interface IErrorHanler extends XCoreHelper.IAppServiceKey {

    public static final String SYSTEM_SERVICE_KEY = "xcore:errorhandler";

    void onError(FragmentActivity activity, IDataSourceHelper dataSourceHelper, DataSourceRequest dataSourceRequest, Exception exception);
}
