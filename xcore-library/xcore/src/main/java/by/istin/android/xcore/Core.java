package by.istin.android.xcore;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.fragment.app.FragmentActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import by.istin.android.xcore.callable.ISuccess;
import by.istin.android.xcore.fragment.XListFragment;
import by.istin.android.xcore.model.CursorModel;
import by.istin.android.xcore.provider.ModelContract;
import by.istin.android.xcore.service.DataSourceService;
import by.istin.android.xcore.service.StatusResultReceiver;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.utils.AppUtils;
import by.istin.android.xcore.utils.CursorUtils;
import by.istin.android.xcore.utils.Holder;

/**
 * Created by IstiN on 14.8.13.
 */
public class Core implements XCoreHelper.IAppServiceKey {

    public static final String APP_SERVICE_KEY = "xcore:common:core";

    public static interface ICursorConverter<Result> {

        Result convert(Cursor source, IExecuteOperation executeOperation);

    }

    public static abstract class SimpleDataSourceServiceListener {

        public void onStart(Bundle resultData) {

        }

        public abstract void onDone(Bundle resultData);

        public void onError(Exception exception) {

        }

        public void onCached(Bundle resultData) {

        }

    }

    public static interface IExecuteOperation<Result> {

        DataSourceRequest getDataSourceRequest();

        String getProcessorKey();

        String getDataSourceKey();

        Uri getResultQueryUri();

        String getSelection();

        String getOrder();

        String[] getProjection();

        String[] getSelectionArgs();

        Activity getActivity();

        ISuccess<Result> getSuccess();

        CursorModel.CursorModelCreator getCursorModelCreator();

        SimpleDataSourceServiceListener getDataSourceListener();

        ICursorConverter<Result> getCursorConverter();
    }

    public static class ExecuteOperationBuilder<Result> {

        private DataSourceRequest mDataSourceRequest;

        private String mProcessorKey;

        private String mDataSourceKey;

        private Uri mResultQueryUri;

        private Activity mActivity;

        private ISuccess<Result> mSuccess;

        private String[] mSelectionArgs;

        private String mSelection;

        private String mOrder;

        private String[] mProjection;

        private ICursorConverter<Result> mCursorConverter;

        private CursorModel.CursorModelCreator mCursorModelCreator;

        private Core.SimpleDataSourceServiceListener mDataSourceServiceListener;

        private Core mCore;

        public ExecuteOperationBuilder(Core core) {
            super();
            mCore = core;
        }

        public ExecuteOperationBuilder() {

        }

        public ExecuteOperationBuilder(IExecuteOperation executeOperation) {
            mDataSourceRequest = executeOperation.getDataSourceRequest();
            mProcessorKey = executeOperation.getProcessorKey();
            mDataSourceKey = executeOperation.getDataSourceKey();
            mResultQueryUri = executeOperation.getResultQueryUri();
            mActivity = executeOperation.getActivity();
            mSuccess = executeOperation.getSuccess();
            mSelectionArgs = executeOperation.getSelectionArgs();
            mCursorModelCreator = executeOperation.getCursorModelCreator();
            mDataSourceServiceListener = executeOperation.getDataSourceListener();
            mProjection = executeOperation.getProjection();
            mSelection = executeOperation.getSelection();
            mOrder = executeOperation.getOrder();
            mCursorConverter = executeOperation.getCursorConverter();
        }

        public ExecuteOperationBuilder(FragmentActivity activity, XListFragment fragment) {
            setDataSourceKey(fragment.getDataSourceKey())
                    .setProcessorKey(fragment.getProcessorKey())
                    .setActivity(activity)
                    .setCursorModelCreator(fragment.getCursorModelCreator())
                    .setResultQueryUri(fragment.getUri())
                    .setSelectionArgs(fragment.getSelectionArgs())
                    .setDataSourceRequest(fragment.createDataSourceRequest(fragment.getUrl(), fragment.isForceUpdateData(), null));
        }

        public ExecuteOperationBuilder<Result> setDataSourceRequest(DataSourceRequest pDataSourceRequest) {
            this.mDataSourceRequest = pDataSourceRequest;
            return this;
        }

        public ExecuteOperationBuilder<Result> setProcessorKey(String pProcessorKey) {
            this.mProcessorKey = pProcessorKey;
            return this;
        }

        public ExecuteOperationBuilder<Result> setDataSourceKey(String pDataSourceKey) {
            this.mDataSourceKey = pDataSourceKey;
            return this;
        }

        public ExecuteOperationBuilder<Result> setResultQueryUri(Uri pResultQueryUri) {
            this.mResultQueryUri = pResultQueryUri;
            return this;
        }

        public ExecuteOperationBuilder<Result> setActivity(Activity pActivity) {
            this.mActivity = pActivity;
            return this;
        }

        public ExecuteOperationBuilder<Result> setSuccess(ISuccess<Result> pSuccess) {
            this.mSuccess = pSuccess;
            return this;
        }

        public ExecuteOperationBuilder<Result> setSelectionArgs(String[] selectionArgs) {
            if (selectionArgs != null) {
                this.mSelectionArgs = selectionArgs.clone();
            } else {
                this.mSelectionArgs = null;
            }
            return this;
        }

        public ExecuteOperationBuilder<Result> setSelection(String selection) {
            this.mSelection = selection;
            return this;
        }

        public ExecuteOperationBuilder<Result> setOrder(String order) {
            this.mOrder = order;
            return this;
        }

        public ExecuteOperationBuilder<Result> setCursorConverter(ICursorConverter<Result> cursorConverter) {
            this.mCursorConverter = cursorConverter;
            return this;
        }

        public ExecuteOperationBuilder<Result> setProjection(String[] projection) {
            if (projection != null) {
                this.mProjection = projection.clone();
            } else {
                this.mProjection = null;
            }
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
                public String getSelection() {
                    return mSelection;
                }

                @Override
                public String getOrder() {
                    return mOrder;
                }

                @Override
                public String[] getProjection() {
                    return mProjection;
                }

                @Override
                public String[] getSelectionArgs() {
                    return mSelectionArgs;
                }

                @Override
                public Activity getActivity() {
                    return mActivity;
                }

                @Override
                public ISuccess<Result> getSuccess() {
                    return mSuccess;
                }

                @Override
                public CursorModel.CursorModelCreator getCursorModelCreator() {
                    return mCursorModelCreator;
                }

                @Override
                public SimpleDataSourceServiceListener getDataSourceListener() {
                    return mDataSourceServiceListener;
                }

                @Override
                public ICursorConverter<Result> getCursorConverter() {
                    return mCursorConverter;
                }
            };
        }

        public ExecuteOperationBuilder<Result> setResultSqlQuery(String resultSqlQuery) {
            setResultQueryUri(ModelContract.getSQLQueryUri(resultSqlQuery, null));
            return this;
        }

        public ExecuteOperationBuilder<Result> setResultSqlQuery(String resultSqlQuery, String[] args) {
            setResultSqlQuery(resultSqlQuery);
            setSelectionArgs(args);
            return this;
        }

        public ExecuteOperationBuilder<Result> setCursorModelCreator(CursorModel.CursorModelCreator cursorModelCreator) {
            this.mCursorModelCreator = cursorModelCreator;
            return this;
        }

        public ExecuteOperationBuilder<Result> setDataSourceServiceListener(SimpleDataSourceServiceListener dataSourceServiceListener) {
            this.mDataSourceServiceListener = dataSourceServiceListener;
            return this;
        }

        public void execute() {
            mCore.execute(build());
        }

        public Object executeSync() throws Exception {
            return mCore.executeSync(build());
        }
    }

    private final Context mContext;

    private final ExecutorService mExecutor = Executors.newFixedThreadPool(3);

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public Core(Context context) {
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    public static Core get(Context context) {
        return (Core) AppUtils.get(context, APP_SERVICE_KEY);
    }

    public static <Result> ExecuteOperationBuilder<Result> with(Context context) {
        Core core = get(context);
        ExecuteOperationBuilder<Result> operationBuilder = new ExecuteOperationBuilder<>(core);
        if (context instanceof Activity) {
            operationBuilder.setActivity((Activity) context);
        }
        return operationBuilder;
    }

    public Object executeSync(final IExecuteOperation<?> executeOperation) throws Exception {
        final SimpleDataSourceServiceListener dataSourceListener = executeOperation.getDataSourceListener();
        Bundle bundle = new Bundle();
        sendOnStartEvent(bundle, dataSourceListener);
        DataSourceRequest dataSourceRequest = executeOperation.getDataSourceRequest();
        try {
            Object result = DataSourceService.execute(ContextHolder.get(), executeOperation.getProcessorKey(), executeOperation.getDataSourceKey(), dataSourceRequest, bundle);
            if (dataSourceListener != null) {
                dataSourceListener.onDone(bundle);
            }
            final Uri uri = executeOperation.getResultQueryUri();
            if (uri == null) {
                sendResult(bundle, result, executeOperation, false, false);
                return result;
            }
            final Cursor cursor = mContext.getContentResolver().query(uri, null, null, executeOperation.getSelectionArgs(), null);
            if (cursor != null) {
                cursor.getCount();
            }
            if (!sendResult(bundle, cursor, executeOperation, false, false)) {
                CursorUtils.close(cursor);
            }
            return result;
        } catch (Exception e) {
            sendOnErrorEvent(e, dataSourceListener);
            throw e;
        }
    }

    public void execute(final IExecuteOperation<?> executeOperation) {
        String processorKey = executeOperation.getProcessorKey();
        final DataSourceRequest dataSourceRequest = executeOperation.getDataSourceRequest();
        final SimpleDataSourceServiceListener dataSourceListener = executeOperation.getDataSourceListener();
        DataSourceService.execute(mContext, dataSourceRequest, processorKey, executeOperation.getDataSourceKey(), new StatusResultReceiver(mHandler) {

            @Override
            public void onStart(Bundle resultData) {
                sendOnStartEvent(resultData, dataSourceListener);
            }

            @Override
            public void onDone(final Bundle resultData) {
                proceed(resultData, executeOperation, false);
            }

            private void proceed(final Bundle resultData, final IExecuteOperation executeOperation, final boolean isCached) {
                final Uri uri = executeOperation.getResultQueryUri();
                if (uri == null) {
                    if (!sendResult(resultData, resultData.get(StatusResultReceiver.RESULT_KEY), executeOperation, true, isCached)) {
                        if (dataSourceListener != null) {
                            if (isCached) {
                                dataSourceListener.onCached(resultData);
                            } else {
                                dataSourceListener.onDone(resultData);
                            }
                        }
                    }
                    return;
                }
                mExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        final Cursor cursor = mContext.getContentResolver().query(uri, executeOperation.getProjection(), executeOperation.getSelection(), executeOperation.getSelectionArgs(), executeOperation.getOrder());
                        if (cursor != null) {
                            cursor.getCount();
                        }
                        if (!sendResult(resultData, cursor, executeOperation, true, isCached)) {
                            CursorUtils.close(cursor);
                            if (dataSourceListener != null) {
                                if (isCached) {
                                    dataSourceListener.onCached(resultData);
                                } else {
                                    dataSourceListener.onDone(resultData);
                                }
                            }
                        }
                    }
                });
            }

            @Override
            protected void onCached(Bundle resultData) {
                super.onCached(resultData);
                proceed(resultData, executeOperation, true);
            }

            @Override
            public void onError(Exception exception) {
                sendOnErrorEvent(exception, dataSourceListener);
            }
        });
    }

    private void sendOnErrorEvent(Exception exception, SimpleDataSourceServiceListener dataSourceListener) {
        exception.printStackTrace();
        if (dataSourceListener != null) {
            dataSourceListener.onError(exception);
        }
    }

    private void sendOnStartEvent(Bundle resultData, SimpleDataSourceServiceListener dataSourceListener) {
        if (dataSourceListener != null) {
            dataSourceListener.onStart(resultData);
        }
    }

    private boolean sendResult(final Bundle resultData, Object result, final IExecuteOperation<?> executeOperation, boolean inHandler, final boolean isCached) {
        final Holder<Object> resultHolder = new Holder<>(result);
        if (result instanceof Cursor) {
            CursorModel.CursorModelCreator cursorModelCreator = executeOperation.getCursorModelCreator();
            ICursorConverter cursorConverter = executeOperation.getCursorConverter();
            if (cursorModelCreator != null) {
                CursorModel cursorModel = cursorModelCreator.create((Cursor) result);
                cursorModel.doInBackground(ContextHolder.get());
                if (cursorConverter != null) {
                    resultHolder.set(cursorConverter.convert(cursorModel, executeOperation));
                    CursorUtils.close(cursorModel);
                } else {
                    resultHolder.set(cursorModel);
                }
            } else if (cursorConverter != null) {
                resultHolder.set(cursorConverter.convert((Cursor) result, executeOperation));
                CursorUtils.close((Cursor) result);
            }
        }
        final ISuccess success = executeOperation.getSuccess();
        if (success != null) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Core.this.sendResult(executeOperation, resultHolder.get(), success, resultData, isCached);
                }
            };
            if (inHandler) {
                mHandler.post(runnable);
            } else {
                Core.this.sendResult(executeOperation, resultHolder.get(), success, resultData, isCached);
            }
            return true;
        } else {
            return false;
        }
    }

    private void sendResult(IExecuteOperation<?> executeOperation, Object result, ISuccess success, Bundle resultData, boolean isCached) {
        success.success(result);
        SimpleDataSourceServiceListener dataSourceListener = executeOperation.getDataSourceListener();
        if (dataSourceListener != null) {
            if (isCached) {
                dataSourceListener.onCached(resultData);
            } else {
                dataSourceListener.onDone(resultData);
            }
        }
    }

    @Override
    public String getAppServiceKey() {
        return APP_SERVICE_KEY;
    }
}
