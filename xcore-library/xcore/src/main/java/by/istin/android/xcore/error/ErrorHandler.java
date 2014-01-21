package by.istin.android.xcore.error;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import by.istin.android.xcore.fragment.IDataSourceHelper;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.impl.http.exception.IOStatusException;
import by.istin.android.xcore.ui.DialogBuilder;
import by.istin.android.xcore.utils.Log;
import by.istin.android.xcore.utils.StringUtil;

/**
 * Created by IstiN on 14.7.13.
 */
public class ErrorHandler implements IErrorHandler {

    private String mDeveloperErrorMessage;

    private class ErrorInfo {

        private FragmentActivity mFragmentActivity;

        private IDataSourceHelper mDataSourceHelper;

        private DataSourceRequest mDataSourceRequest;

        @Override
        public int hashCode() {
            return mDataSourceHelper.hashCode()+ mDataSourceRequest.hashCode()+ mFragmentActivity.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            ErrorInfo errorInfo = (ErrorInfo) o;
            return errorInfo.mDataSourceRequest.equals(mDataSourceRequest) &&
                    errorInfo.mDataSourceHelper.equals(mDataSourceRequest) &&
                    errorInfo.mFragmentActivity.equals(mFragmentActivity);
        }
    }

    private String mInternetErrorMessage;

    private String mServiceUnavailableMessage;

    private String mErrorDialogTitle;

    private String mDeveloperEmail;

    private Map<ErrorType, Set<ErrorInfo>> mErrorTypeMap = Collections.synchronizedMap(new HashMap<ErrorType, Set<ErrorInfo>>());

    private Map<ErrorType, Boolean> mErrorTypeDialog = Collections.synchronizedMap(new HashMap<ErrorType, Boolean>());

    @Override
    public String getAppServiceKey() {
        return SYSTEM_SERVICE_KEY;
    }

    public ErrorHandler(String errorDialogTitle, String internetErrorMessage, String serviceUnavailableMessage, String developerErrorMessage, String developerEmail) {
        mInternetErrorMessage = internetErrorMessage;
        mServiceUnavailableMessage = serviceUnavailableMessage;
        mErrorDialogTitle = errorDialogTitle;
        mDeveloperEmail = developerEmail;
        mDeveloperErrorMessage = developerErrorMessage;
    }

    @Override
    public void onError(final FragmentActivity activity,
                        IDataSourceHelper dataSourceHelper,
                        final DataSourceRequest dataSourceRequest,
                        final Exception exception) {
        ErrorType type = getErrorType(exception);
        Set<ErrorInfo> errorInfos = mErrorTypeMap.get(type);
        if (errorInfos == null) {
            errorInfos = Collections.synchronizedSet(new HashSet<ErrorInfo>());
            mErrorTypeMap.put(type, errorInfos);
        }
        ErrorInfo errorInfo = new ErrorInfo();
        errorInfo.mDataSourceHelper = dataSourceHelper;
        errorInfo.mDataSourceRequest = dataSourceRequest;
        errorInfo.mFragmentActivity = activity;
        if (!errorInfos.contains(errorInfo)) {
            errorInfos.add(errorInfo);
        }
        final ErrorType finalType = type;
        Runnable clearRunnable = new Runnable() {

            @Override
            public void run() {
                mErrorTypeMap.remove(finalType);
                mErrorTypeDialog.remove(finalType);
            }

        };

        if (!mErrorTypeDialog.containsKey(type)) {
            mErrorTypeDialog.put(type, true);
            switch (type) {
                case INTERNET:
                    handle(activity, dataSourceRequest, exception, type, mInternetErrorMessage, clearRunnable);
                    return;
                case SERVER_UNAVAILABLE:
                    handle(activity, dataSourceRequest, exception, type, mServiceUnavailableMessage, clearRunnable);
                    return;
                case UNKNOWN:
                    onUnknownError(activity, dataSourceRequest, exception, type, clearRunnable);
                    return;
                case DEVELOPER_ERROR:
                    onDeveloperError(activity, dataSourceRequest, exception, type, mDeveloperErrorMessage, clearRunnable);
                    return;
            }
        }
    }

    protected void handle(final FragmentActivity activity, final DataSourceRequest dataSourceRequest, final Exception exception, final ErrorType type, String message, final Runnable clearErrorHashRunnable) {
        DialogBuilder.confirm(activity,
                mErrorDialogTitle,
                message,
                StringUtil.getStringResource("repeat"),
                StringUtil.getStringResource("cancel"),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        clearErrorHashRunnable.run();
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Set<ErrorInfo> infos = mErrorTypeMap.get(type);
                        if (infos != null) {
                            for (ErrorInfo info : infos) {
                                info.mDataSourceHelper.dataSourceExecute(info.mFragmentActivity, info.mDataSourceRequest);
                            }
                        }
                        clearErrorHashRunnable.run();
                    }
                }
        );
    }

    protected void onUnknownError(final FragmentActivity activity, final DataSourceRequest dataSourceRequest, final Exception exception, ErrorType type, Runnable clearRunnable) {
        StringBuilder builder = buildExceptionInfo(exception, dataSourceRequest);
        Log.xe(activity, type + " type exception ", exception);
        Log.xe(activity, builder.toString());
        clearRunnable.run();
    }

    protected void onDeveloperError(final FragmentActivity activity, final DataSourceRequest dataSourceRequest, final Exception exception, ErrorType type, String message, final Runnable clearRunnable) {
        DialogBuilder.confirm(activity,
                mErrorDialogTitle,
                message,
                StringUtil.getStringResource("send"),
                StringUtil.getStringResource("cancel"),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        clearRunnable.run();
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        StringBuilder builder = buildExceptionInfo(exception, dataSourceRequest);
                        Intent sendEmailIntent = getSendEmailIntent(mDeveloperEmail, null, activity.getPackageName() + ":error", builder.toString(), null);
                        try {
                            activity.startActivity(sendEmailIntent);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(activity, "Please, install mail client", Toast.LENGTH_SHORT).show();
                        }
                        clearRunnable.run();
                    }
                }
        );
    }

    protected static StringBuilder buildExceptionInfo(Exception exception, DataSourceRequest dataSourceRequest) {
        String body = joinStackTrace(exception);
        StringBuilder builder = new StringBuilder();
        builder.append(body);
        builder.append("\n================================");
        if (dataSourceRequest != null) {
            builder.append("\nUri:" + dataSourceRequest.getUri());
            builder.append("\nCacheExpiration:" + dataSourceRequest.getCacheExpiration());
            builder.append("\nRequestParentUri:" + dataSourceRequest.getRequestParentUri());
            builder.append("\nUriParams:" + dataSourceRequest.toUriParams());
        }
        return builder;
    }

    @Override
    public ErrorType getErrorType(Exception exception) {
        ErrorType type;
        if (exception instanceof IOStatusException) {
            type = ErrorType.SERVER_UNAVAILABLE;
        } else if (exception instanceof IOException) {
            type = ErrorType.INTERNET;
        } else {
            type = ErrorType.DEVELOPER_ERROR;
        }
        return type;
    }

    @Override
    public boolean isCanBeReSent(Exception exception) {
        ErrorType errorType = getErrorType(exception);
        return errorType != ErrorType.DEVELOPER_ERROR && errorType != ErrorType.UNKNOWN;
    }

    public static String joinStackTrace(Throwable e) {
        StringWriter writer = null;
        try {
            writer = new StringWriter();
            joinStackTrace(e, writer);
            return writer.toString();
        }
        finally {
            if (writer != null)
                try {
                    writer.close();
                } catch (IOException e1) {
                    // ignore
                }
        }
    }

    public static void joinStackTrace(Throwable e, StringWriter writer) {
        PrintWriter printer = null;
        try {
            printer = new PrintWriter(writer);

            while (e != null) {

                printer.println(e);
                StackTraceElement[] trace = e.getStackTrace();
                for (int i = 0; i < trace.length; i++)
                    printer.println("\tat " + trace[i]);

                e = e.getCause();
                if (e != null)
                    printer.println("Caused by:\r\n");
            }
        }
        finally {
            if (printer != null)
                printer.close();
        }
    }

    public static Intent getSendEmailIntent(String mailTo, String mailCC,
                                            String subject, CharSequence body, File attachment) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        // intent.setType("text/plain");
        intent.setType("message/rfc822");
        if (mailTo == null) {
            mailTo = "";
        }
        intent.setData(Uri.parse("mailto:" + mailTo));
        if (!StringUtil.isEmpty(mailCC)) {
            intent.putExtra(Intent.EXTRA_CC, new String[] { mailCC });
        }
        if (!StringUtil.isEmpty(subject)) {
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        }
        if (!StringUtil.isEmpty(body)) {
            intent.putExtra(Intent.EXTRA_TEXT, body);
        }
        if (attachment != null) {
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(attachment));
        }
        return intent;
    }
}
