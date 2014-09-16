package by.istin.android.xcore.wearable;

import android.content.Context;
import android.os.Bundle;

import by.istin.android.xcore.Core;
import by.istin.android.xcore.callable.ISuccess;
import by.istin.android.xcore.utils.AppUtils;
import by.istin.android.xcore.utils.Holder;
import by.istin.android.xcore.utils.Log;

/**
 * Created by IstiN on 14.09.2014.
 */
public class CoreWearable extends Core {

    public static final String APP_SERVICE_KEY = "xcore:wearable:core";

    private ClientController mClientController;

    public CoreWearable(Context context) {
        super(context);
        mClientController = new ClientController(context);
    }

    public static CoreWearable get(Context context) {
        return (CoreWearable) AppUtils.get(context, APP_SERVICE_KEY);
    }

    @Override
    public String getAppServiceKey() {
        return APP_SERVICE_KEY;
    }

    @Override
    public Object executeSync(IExecuteOperation<?> executeOperation) throws Exception {
        Log.xd(this, "executeSync");
        final Object lock = new Object();
        final Holder<Object> resultHolder = new Holder<Object>();
        ExecuteOperationBuilder executeOperationBuilder = new ExecuteOperationBuilder(executeOperation);
        final ISuccess success = executeOperation.getSuccess();
        final SimpleDataSourceServiceListener dataSourceListener = executeOperation.getDataSourceListener();
        executeOperationBuilder.setDataSourceServiceListener(new SimpleDataSourceServiceListener() {

            @Override
            public void onStart(Bundle resultData) {
                Log.xd(this, "executeSync");
                if (dataSourceListener != null) {
                    dataSourceListener.onStart(resultData);
                }
            }

            @Override
            public void onError(Exception exception) {
                Log.xd(this, "onError");
                if (dataSourceListener != null) {
                    dataSourceListener.onError(exception);
                }
                synchronized (lock) {
                    Log.xd(this, "onError:notify");
                    lock.notify();
                }
            }

            @Override
            public void onCached(Bundle resultData) {
                Log.xd(this, "onCached");
                if (dataSourceListener != null) {
                    dataSourceListener.onCached(resultData);
                }
                synchronized (lock) {
                    Log.xd(this, "onCached:notify");
                    lock.notify();
                }
            }

            @Override
            public void onDone(Bundle resultData) {
                Log.xd(this, "onDone");
                if (dataSourceListener != null) {
                    dataSourceListener.onDone(resultData);
                }
            }
        });
        executeOperationBuilder.setSuccess(new ISuccess() {
            @Override
            public void success(Object o) {
                Log.xd(this, "success");
                if (success != null) {
                    success.success(o);
                }
                resultHolder.set(o);
                synchronized (lock) {
                    Log.xd(this, "success:notify");
                    lock.notify();
                }
            }
        });
        Log.xd(this, "execute:sync");
        mClientController.execute(executeOperationBuilder.build());
        synchronized (lock) {
            try {
                Log.xd(this, "execute:wait");
                lock.wait();
                Log.xd(this, "execute:wait:finish");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        Log.xd(this, "execute:return result");
        return resultHolder.get();
    }

    @Override
    public void execute(IExecuteOperation<?> executeOperation) {
        mClientController.execute(executeOperation);
    }

    public void cancel(IExecuteOperation<?> executeOperation) {
        mClientController.cancel(executeOperation);
    }

}
