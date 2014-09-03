package by.istin.android.xcore.service.manager;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;

import java.util.List;

import by.istin.android.xcore.provider.ModelContract;
import by.istin.android.xcore.service.DataSourceService;
import by.istin.android.xcore.service.RequestExecutor;
import by.istin.android.xcore.service.StatusResultReceiver;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.DataSourceRequestEntity;
import by.istin.android.xcore.utils.ContentUtils;
import by.istin.android.xcore.utils.Log;

public class DefaultRequestManager extends AbstractRequestManager {

    private static final String SERVICE_KEY = IRequestManager.APP_SERVICE_KEY + DataSourceService.class.getName();

    @Override
    public String getAppServiceKey() {
        return SERVICE_KEY;
    }

    @Override
    public void run(final Context context, String processorKey, String dataSourceKey, final RequestExecutor.ExecuteRunnable executeRunnable, final DataSourceRequest dataSourceRequest, final Bundle dataSourceRequestBundle, ResultReceiver resultReceiver) {
        executeRunnable.sendStatus(StatusResultReceiver.Status.START, dataSourceRequestBundle);
        try {
            execute(context, processorKey, dataSourceKey, dataSourceRequest, dataSourceRequestBundle, new Runnable() {
                @Override
                public void run() {
                    if (isExecuteJoinedRequestsSuccessful(context, executeRunnable, dataSourceRequest, dataSourceRequestBundle)) {
                        executeRunnable.sendStatus(StatusResultReceiver.Status.CACHED, dataSourceRequestBundle);
                    }
                }
            });
            if (isExecuteJoinedRequestsSuccessful(context, executeRunnable, dataSourceRequest, dataSourceRequestBundle)) {
                executeRunnable.sendStatus(StatusResultReceiver.Status.DONE, dataSourceRequestBundle);
                /*List<ContentValues> entities = ContentUtils.getEntities(context, DataSourceRequestEntity.class, DataSourceRequestEntity.DATA_SOURCE_KEY + " IS NULL OR "
                        + DataSourceRequestEntity.PROCESSOR_KEY + " IS NULL OR ("
                        + "? - " + DataSourceRequestEntity.LAST_UPDATE + ") > " + DataSourceRequestEntity.EXPIRATION, new String[]{
                        String.valueOf(System.currentTimeMillis())
                });
                if (entities != null) {
                    for (ContentValues contentValues : entities) {
                        Long expiration = contentValues.getAsLong(DataSourceRequestEntity.EXPIRATION);
                        Long lastUpdate = contentValues.getAsLong(DataSourceRequestEntity.LAST_UPDATE);
                        Log.xd(this, "deleted expired requests " + expiration);
                        Log.xd(this, "deleted expired requests " + lastUpdate);
                        Log.xd(this, "deleted expired requests " + System.currentTimeMillis());
                        Log.xd(this, "deleted expired requests " + System.nanoTime());
                        Log.xd(this, "deleted expired requests " + (System.currentTimeMillis() - lastUpdate > expiration));
                    }
                }*/
                int deleteRowCount = context.getContentResolver().delete(ModelContract.getUri(DataSourceRequestEntity.class),
                        DataSourceRequestEntity.DATA_SOURCE_KEY + " IS NULL OR "
                                + DataSourceRequestEntity.PROCESSOR_KEY + " IS NULL OR ("
                                + "? - " + DataSourceRequestEntity.LAST_UPDATE + ") > " + DataSourceRequestEntity.EXPIRATION, new String[]{
                                String.valueOf(System.currentTimeMillis())
                        });
                Log.xd(this, "deleted expired requests count " + deleteRowCount);

            }
        } catch (Exception e) {
            try {
                dataSourceRequestBundle.putSerializable(StatusResultReceiver.ERROR_KEY, e);
                executeRunnable.sendStatus(StatusResultReceiver.Status.ERROR, dataSourceRequestBundle);
            } catch (RuntimeException e1) {
                dataSourceRequestBundle.remove(StatusResultReceiver.ERROR_KEY);
                executeRunnable.sendStatus(StatusResultReceiver.Status.ERROR, dataSourceRequestBundle);
            }
        }
    }

    private boolean isExecuteJoinedRequestsSuccessful(Context context, final RequestExecutor.ExecuteRunnable parentRunnable, DataSourceRequest dataSourceRequest, Bundle statusBundle) {
        DataSourceRequest joinedRequest = dataSourceRequest.getJoinedRequest();
        if (joinedRequest != null) {
            ErrorRedirectExecuteRunnable redirectRunnable = new ErrorRedirectExecuteRunnable(parentRunnable);
            String joinedDataSource = dataSourceRequest.getJoinedDataSourceKey();
            String joinedProcessor = dataSourceRequest.getJoinedProcessorKey();
            run(context, joinedProcessor, joinedDataSource, redirectRunnable, joinedRequest, statusBundle, null);
            if (redirectRunnable.isError) {
                return false;
            }
        }
        return true;
    }

    private class ErrorRedirectExecuteRunnable extends RequestExecutor.ExecuteRunnable {

        private final RequestExecutor.ExecuteRunnable parentRunnable;

        private boolean isError = false;

        public ErrorRedirectExecuteRunnable(RequestExecutor.ExecuteRunnable parentRunnable) {
            super(new ResultReceiver(new Handler(Looper.getMainLooper())));
            this.parentRunnable = parentRunnable;
        }

        @Override
        public String createKey() {
            //used only like redirect to parent request
            return null;
        }

        @Override
        protected void onDone() {
            //used only like redirect to parent request
        }

        @Override
        public void run() {
            //used only like redirect to parent request
        }

        @Override
        public void sendStatus(StatusResultReceiver.Status status, Bundle bundle) {
            if (status == StatusResultReceiver.Status.ERROR) {
                parentRunnable.sendStatus(status, bundle);
                isError = true;
            }
        }

        public boolean isError() {
            return isError;
        }
    }
}
