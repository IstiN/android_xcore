package by.istin.android.xcore.wearable;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import by.istin.android.xcore.Core;
import by.istin.android.xcore.utils.AppUtils;

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
        return mClientController.executeSync(executeOperation);
    }

    @Override
    public void execute(IExecuteOperation<?> executeOperation) {
        mClientController.execute(executeOperation);
    }

    public void cancel(IExecuteOperation<?> executeOperation) {
        mClientController.cancel(executeOperation);
    }

}
