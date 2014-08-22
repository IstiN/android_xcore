package by.istin.android.xcore.service.manager;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.ResultReceiver;

import java.io.Serializable;

import by.istin.android.xcore.processor.IProcessor;
import by.istin.android.xcore.service.RequestExecutor;
import by.istin.android.xcore.service.StatusResultReceiver;
import by.istin.android.xcore.service.assist.LIFOLinkedBlockingDeque;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.IDataSource;
import by.istin.android.xcore.utils.AppUtils;
import by.istin.android.xcore.utils.Log;

public abstract class AbstractRequestManager implements IRequestManager {

    private RequestExecutor mRequestExecutor;

    @Override
    public void stop(ResultReceiver resultReceiver) {
        Log.xd(this, "action stop");
        RequestExecutor requestExecutor = mRequestExecutor;
        if (requestExecutor != null) {
            requestExecutor.stop(resultReceiver);
            mRequestExecutor = null;
        } else {
            resultReceiver.send(0, null);
        }
    }

    @Override
    public RequestExecutor getRequestExecutor() {
        if (mRequestExecutor == null) {
            mRequestExecutor = createExecutorService();
        }
        return mRequestExecutor;
    }

    @Override
    public RequestExecutor createExecutorService() {
        return new RequestExecutor(RequestExecutor.DEFAULT_POOL_SIZE, new LIFOLinkedBlockingDeque<Runnable>());
    }

    @Override
    public void onHandleRequest(final Context context, final DataSourceRequest dataSourceRequest, final String processorKey, final String dataSourceKey, final StatusResultReceiver resultReceiver) {
        final Bundle bundle = new Bundle();
        dataSourceRequest.toBundle(bundle);
        if (resultReceiver != null) {
            resultReceiver.send(StatusResultReceiver.Status.ADD_TO_QUEUE.ordinal(), bundle);
        }
        final RequestExecutor requestExecutor = getRequestExecutor();
        requestExecutor.execute(new RequestExecutor.ExecuteRunnable(resultReceiver) {

            @Override
            public void run() {
                AbstractRequestManager.this.run(context, processorKey, dataSourceKey, this, dataSourceRequest, bundle, resultReceiver);
            }

            @Override
            public String createKey() {
                return dataSourceRequest.toUriParams();
            }

            @Override
            protected void onDone() {
                if (requestExecutor.isEmpty()) {
                    stop(null);
                    Log.xd(AbstractRequestManager.this, "stop from run");
                }
            }

        });
    }

    @Override
    public String getAppServiceKey() {
        return IRequestManager.APP_SERVICE_KEY;
    }

    @SuppressWarnings("unchecked")
    public static Object execute(Context context, boolean cacheable, String processorKey, String dataSourceKey, DataSourceRequest dataSourceRequest, Bundle bundle) throws Exception {
        final IProcessor processor = AppUtils.get(context, processorKey);
        final IDataSource dataSource = AppUtils.get(context, dataSourceKey);
        Object result = processor.execute(dataSourceRequest, dataSource, dataSource.getSource(dataSourceRequest));
        if (cacheable) {
            processor.cache(context, dataSourceRequest, result);
            if (bundle == null) {
                return result;
            }
            if (result instanceof Parcelable) {
                bundle.putParcelable(StatusResultReceiver.RESULT_KEY, (Parcelable) result);
            } else if (result instanceof Parcelable[]) {
                bundle.putParcelableArray(StatusResultReceiver.RESULT_KEY, (Parcelable[]) result);
            } else if (result instanceof Serializable) {
                bundle.putSerializable(StatusResultReceiver.RESULT_KEY, (Serializable) result);
            }
        } else {
            if (bundle == null) {
                return result;
            }
            if (result instanceof Parcelable) {
                bundle.putParcelable(StatusResultReceiver.RESULT_KEY, (Parcelable) result);
            } else if (result instanceof Parcelable[]) {
                bundle.putParcelableArray(StatusResultReceiver.RESULT_KEY, (Parcelable[]) result);
            } else if (result instanceof Serializable) {
                bundle.putSerializable(StatusResultReceiver.RESULT_KEY, (Serializable) result);
            }
        }
        return result;
    }
}
