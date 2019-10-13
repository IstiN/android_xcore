package by.istin.android.xcore.error;

import androidx.fragment.app.FragmentActivity;

import by.istin.android.xcore.XCoreHelper;
import by.istin.android.xcore.fragment.IDataSourceHelper;
import by.istin.android.xcore.source.DataSourceRequest;

/**
 * Created by IstiN on 14.7.13.
 */
public interface IErrorHandler extends XCoreHelper.IAppServiceKey {

    public static enum ErrorType {

        INTERNET, SERVER_UNAVAILABLE, DEVELOPER_ERROR, UNKNOWN, AUTHORIZATION

    }

    public static final String SYSTEM_SERVICE_KEY = "xcore:errorhandler";

    void onError(FragmentActivity activity, IDataSourceHelper dataSourceHelper, DataSourceRequest dataSourceRequest, Exception exception);

    ErrorHandler.ErrorType getErrorType(Exception exception);

    boolean isCanBeReSent(Exception exception);
}
