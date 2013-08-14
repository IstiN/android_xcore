package by.istin.android.xcore;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import by.istin.android.xcore.callable.ISuccess;
import by.istin.android.xcore.error.IErrorHandler;
import by.istin.android.xcore.service.DataSourceService;
import by.istin.android.xcore.service.StatusResultReceiver;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.utils.AppUtils;
import by.istin.android.xcore.utils.CursorUtils;

/**
 * Created by IstiN on 14.8.13.
 */
public class Core implements XCoreHelper.IAppServiceKey {

    public static final String APP_SERVICE_KEY = "xcore:common:core";

    public static interface IExecuteOperation<Result> {

        DataSourceRequest getDataSourceRequest();

        String getProcessorKey();

        String getDataSourceKey();

        Uri getResultQueryUri();

        String getRequestUri();

        Activity getActivity();

        ISuccess<Result> getSuccess();

    }

    public static class ExecuteOperationBuilder<Result> {

        private DataSourceRequest mDataSourceRequest;

        private String mProcessorKey;

        private String mDataSourceKey;

        private Uri mResultQueryUri;

        private String mRequestUri;

        private Activity mActivity;

        private ISuccess<Result> mSuccess;

        public ExecuteOperationBuilder setDataSourceRequest(DataSourceRequest pDataSourceRequest) {
            this.mDataSourceRequest = pDataSourceRequest;
            return this;
        }

        public ExecuteOperationBuilder setProcessorKey(String pProcessorKey) {
            this.mProcessorKey = pProcessorKey;
            return this;
        }

        public ExecuteOperationBuilder setDataSourceKey(String pDataSourceKey) {
            this.mDataSourceKey = pDataSourceKey;
            return this;
        }

        public ExecuteOperationBuilder setResultQueryUri(Uri pResultQueryUri) {
            this.mResultQueryUri = pResultQueryUri;
            return this;
        }

        public ExecuteOperationBuilder setRequestUri(String pRequestUri) {
            this.mRequestUri = pRequestUri;
            return this;
        }

        public ExecuteOperationBuilder setActivity(Activity pActivity) {
            this.mActivity = pActivity;
            return this;
        }

        public ExecuteOperationBuilder setSuccess(ISuccess<Result> pSuccess) {
            this.mSuccess = pSuccess;
            return this;
        }

        public IExecuteOperation<Result> build() {

            return new IExecuteOperation<Result>() {
                @Override
                public DataSourceRequest getDataSourceRequest() {
                    return mDataSourceRequest;
                }

                @Override
                public String getProcessorKey() {
                    return mProcessorKey;
                }

                @Override
                public String getDataSourceKey() {
                    return mDataSourceKey;
                }

                @Override
                public Uri getResultQueryUri() {
                    return mResultQueryUri;
                }

                @Override
                public String getRequestUri() {
                    return mRequestUri;
                }

                @Override
                public Activity getActivity() {
                    return mActivity;
                }

                @Override
                public ISuccess<Result> getSuccess() {
                    return mSuccess;
                }
            };
        }
    }

    private Context mContext;

    private ExecutorService mExecutor = Executors.newFixedThreadPool(3);

    private Handler mHandler = new Handler(Looper.getMainLooper());

    public Core(Context context) {
        mContext = context;
    }

    public static Core get(Context context) {
        return (Core) AppUtils.get(context, APP_SERVICE_KEY);
    }

    public void execute(final IExecuteOperation executeOperation) {
        String processorKey = executeOperation.getProcessorKey();
        final DataSourceRequest dataSourceRequest = executeOperation.getDataSourceRequest();
        DataSourceService.execute(mContext, dataSourceRequest, processorKey, executeOperation.getDataSourceKey(), new StatusResultReceiver(mHandler) {

            @Override
            public void onStart(Bundle resultData) {

            }

            @Override
            public void onDone(Bundle resultData) {
                final Uri uri = executeOperation.getResultQueryUri();
                if (uri == null) {
                    sendResult(resultData, executeOperation);
                    return;
                }
                mExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        final Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);
                        if (cursor != null) {
                            cursor.getCount();
                        }
                        if (!sendResult(cursor, executeOperation)) {
                            CursorUtils.close(cursor);
                        }
                    }
                });
            }

            @Override
            public void onError(Exception exception) {
                exception.printStackTrace();
                IErrorHandler errorHandler = (IErrorHandler) AppUtils.get(mContext, IErrorHandler.SYSTEM_SERVICE_KEY);
                Activity activity = executeOperation.getActivity();
                if (errorHandler != null && activity != null) {
                    //TODO errorHandler.onError(activity, XListFragment.this, dataSourceRequest, exception);
                } else {
                    Toast.makeText(activity, exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean sendResult(final Object result, IExecuteOperation executeOperation) {
        final ISuccess success = executeOperation.getSuccess();
        if (success != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    success.success(result);
                }
            });
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getAppServiceKey() {
        return APP_SERVICE_KEY;
    }
}
