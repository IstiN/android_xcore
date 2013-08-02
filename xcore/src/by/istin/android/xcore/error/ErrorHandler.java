package by.istin.android.xcore.error;

import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import by.istin.android.xcore.fragment.IDataSourceHelper;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.impl.http.exception.IOStatusException;
import by.istin.android.xcore.ui.DialogBuilder;
import by.istin.android.xcore.utils.StringUtil;

/**
 * Created by IstiN on 14.7.13.
 */
public class ErrorHandler implements IErrorHandler {

    @Override
    public String getAppServiceKey() {
        return SYSTEM_SERVICE_KEY;
    }

    public static enum ErrorType {

        INTERNET, SERVER_UNAVAILABLE, DEVELOPER_ERROR;

    }

    private class ErrorInfo {

        FragmentActivity fragmentActivity;

        IDataSourceHelper dataSourceHelper;

        DataSourceRequest dataSourceRequest;

        @Override
        public int hashCode() {
            return dataSourceHelper.hashCode()+dataSourceRequest.hashCode()+fragmentActivity.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            ErrorInfo errorInfo = (ErrorInfo) o;
            return errorInfo.dataSourceRequest.equals(dataSourceRequest) &&
                    errorInfo.dataSourceHelper.equals(dataSourceRequest) &&
                    errorInfo.fragmentActivity.equals(fragmentActivity);
        }
    }

    private String mInternetErrorMessage;

    private String mServiceUnavailableMessage;

    private String mErrorDialogTitle;

    private Map<ErrorType, Set<ErrorInfo>> mErrorTypeMap = Collections.synchronizedMap(new HashMap<ErrorType, Set<ErrorInfo>>());

    private Map<ErrorType, Boolean> mErrorTypeDialog = Collections.synchronizedMap(new HashMap<ErrorType, Boolean>());

    public ErrorHandler(String errorDialogTitle, String internetErrorMessage, String serviceUnavailableMessage) {
        mInternetErrorMessage = internetErrorMessage;
        mServiceUnavailableMessage = serviceUnavailableMessage;
        mErrorDialogTitle = errorDialogTitle;
    }

    @Override
    public void onError(FragmentActivity activity,
                        IDataSourceHelper dataSourceHelper,
                        DataSourceRequest dataSourceRequest,
                        Exception exception) {
        ErrorType type;
        if (exception instanceof IOStatusException) {
            type = ErrorType.SERVER_UNAVAILABLE;
        } else if (exception instanceof IOException) {
            type = ErrorType.INTERNET;
        } else {
            type = ErrorType.DEVELOPER_ERROR;
        }
        Set<ErrorInfo> errorInfos = mErrorTypeMap.get(type);
        if (errorInfos == null) {
            errorInfos = Collections.synchronizedSet(new HashSet<ErrorInfo>());
            mErrorTypeMap.put(type, errorInfos);
        }
        ErrorInfo errorInfo = new ErrorInfo();
        errorInfo.dataSourceHelper = dataSourceHelper;
        errorInfo.dataSourceRequest = dataSourceRequest;
        errorInfo.fragmentActivity = activity;
        if (!errorInfos.contains(errorInfo)) {
            errorInfos.add(errorInfo);
        }

        if (!mErrorTypeDialog.containsKey(type)) {
            mErrorTypeDialog.put(type, true);
            String message = null;
            switch (type) {
                case INTERNET:
                    message = mInternetErrorMessage;
                    break;
                case SERVER_UNAVAILABLE:
                    message = mServiceUnavailableMessage;
                    break;
                case DEVELOPER_ERROR:
                    message = "Developer error, check logcat for more details";
                    break;
            }
            final ErrorType finalType = type;
            DialogBuilder.confirm(activity,
                    mErrorDialogTitle,
                    message,
                    StringUtil.getStringResource("repeat"),
                    StringUtil.getStringResource("cancel"),
                    new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mErrorTypeMap.remove(finalType);
                            mErrorTypeDialog.remove(finalType);
                        }
                    },
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Set<ErrorInfo> infos = mErrorTypeMap.get(finalType);
                            if (infos != null) {
                                for (ErrorInfo info : infos) {
                                    info.dataSourceHelper.dataSourceExecute(info.fragmentActivity, info.dataSourceRequest);
                                }
                            }
                            mErrorTypeMap.remove(finalType);
                            mErrorTypeDialog.remove(finalType);
                        }
                    });
        }
    }

}
