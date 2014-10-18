package by.istin.android.xcore.wearable;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.List;

import by.istin.android.xcore.Core;
import by.istin.android.xcore.service.AbstractExecutorService;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.utils.Holder;

/**
 * Created by IstiN on 14.09.2014.
 */
class ClientController {

    static class ExecuteOperationStatus {

        static enum Status {
            NOT_STARTED, WAIT_RESULT;
        }

        private Core.IExecuteOperation mExecuteOperation;

        private Status mStatus = Status.NOT_STARTED;

        ExecuteOperationStatus(Core.IExecuteOperation executeOperation) {
            this.mExecuteOperation = executeOperation;
        }

        public Core.IExecuteOperation getExecuteOperation() {
            return mExecuteOperation;
        }

        public Status getStatus() {
            return mStatus;
        }

        public void setStatus(Status status) {
            this.mStatus = status;
        }

    }

    private GoogleApiClient mGoogleApiClient;

    private final Object mLock = new Object();

    private Context mContext;

    private List<ExecuteOperationStatus> mExecuteOperations = new ArrayList<ExecuteOperationStatus>();

    ClientController(Context context) {
        this.mContext = context;
    }


    private GoogleApiClient.OnConnectionFailedListener mOnConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            proceedErrorConnectionFailed(connectionResult);
        }

    };

    private DataApi.DataListener mDataListener = new DataApi.DataListener() {

        @Override
        public void onDataChanged(DataEventBuffer dataEvents) {
            //TODO handle result from the app
        }

    };

    private GoogleApiClient.ConnectionCallbacks mConnectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {

        @Override
        public void onConnected(Bundle bundle) {
            Wearable.DataApi.addListener(mGoogleApiClient, mDataListener);
            proceed();
        }

        @Override
        public void onConnectionSuspended(int i) {
            proceedErrorConnectionSuspended(i);
        }
    };

    public void cancel(Core.IExecuteOperation<?> executeOperation) {
        synchronized (mLock) {
            mExecuteOperations.remove(executeOperation);
            if (mExecuteOperations.isEmpty()) {
                close();
            }
        }
    }

    private void proceedErrorConnectionSuspended(int i) {
        synchronized (mLock) {
            //todo notify execute operations
            close();
        }
    }

    private void proceedErrorConnectionFailed(ConnectionResult connectionResult) {
        synchronized (mLock) {
            //todo notify execute operations
            close();
        }
    }

    public void execute(Core.IExecuteOperation executeOperation) {
        synchronized (mLock) {
            mExecuteOperations.add(new ExecuteOperationStatus(executeOperation));
            GoogleApiClient googleApiClient = getGoogleClient();
            if (!googleApiClient.isConnected()) {
                return;
            }
        }
        proceed();
    }

    private void proceed() {
        synchronized (mLock) {
            if (mExecuteOperations.isEmpty()) {
                close();
                return;
            }
            GoogleApiClient googleClient = getGoogleClient();
            if (!googleClient.isConnected()) {
                return;
            }

            final Holder<ExecuteOperationStatus> executeOperationStatusHolder = new Holder<ExecuteOperationStatus>();
            for (ExecuteOperationStatus executeOperationStatus : mExecuteOperations) {
                if (executeOperationStatus.getStatus() != ExecuteOperationStatus.Status.WAIT_RESULT) {
                    executeOperationStatusHolder.set(executeOperationStatus);
                    executeOperationStatus.setStatus(ExecuteOperationStatus.Status.WAIT_RESULT);
                    break;
                }
            }
            if (executeOperationStatusHolder.isNull()) {
                //all runnables executed and waiting results
                return;
            }

            final ExecuteOperationStatus executeOperationStatus = executeOperationStatusHolder.get();
            Core.IExecuteOperation executeOperation = executeOperationStatus.getExecuteOperation();
            PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(WearableContract.URI);

            initDataMapRequest(putDataMapRequest, executeOperation);

            PutDataRequest request = putDataMapRequest.asPutDataRequest();
            Wearable.DataApi.putDataItem(googleClient, request)
                    .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                        @Override
                        public void onResult(DataApi.DataItemResult dataItemResult) {
                            if (!dataItemResult.getStatus().isSuccess()) {
                                synchronized (mLock) {
                                    proceedErrorStatus(dataItemResult.getStatus().getStatusCode(), executeOperationStatus.getExecuteOperation());
                                    mExecuteOperations.remove(executeOperationStatus);
                                    if (mExecuteOperations.isEmpty()) {
                                        close();
                                    }
                                }
                            } else {
                                //TODO remove this in future, only for testing purpose
                                mExecuteOperations.remove(executeOperationStatus);
                                if (mExecuteOperations.isEmpty()) {
                                    close();
                                }
                            }
                        }
                    });
        }
    }

    private void initDataMapRequest(PutDataMapRequest putDataMapRequest, Core.IExecuteOperation executeOperation) {
        DataSourceRequest dataSourceRequest = executeOperation.getDataSourceRequest();
        String processorKey = executeOperation.getProcessorKey();
        String dataSourceKey = executeOperation.getDataSourceKey();
        Uri resultQueryUri = executeOperation.getResultQueryUri();
        String[] selectionArgs = executeOperation.getSelectionArgs();

        DataMap dataMap = putDataMapRequest.getDataMap();
        dataMap.putString(WearableContract.PARAM_DATA_SOURCE_KEY, dataSourceKey);
        dataMap.putString(WearableContract.PARAM_PROCESSOR_KEY, processorKey);
        String dataSourceRequestAsString = dataSourceRequest.toUriParams();
        dataMap.putString(WearableContract.PARAM_DATA_SOURCE_REQUEST, dataSourceRequestAsString);
        dataMap.putLong("stub", System.currentTimeMillis());
        if (selectionArgs != null) {
            dataMap.putStringArray(WearableContract.PARAM_SELECTION_ARGS_KEY, selectionArgs);
        }
        if (resultQueryUri != null) {
            dataMap.putString(WearableContract.PARAM_URI_KEY, resultQueryUri.toString());
        }
    }

    private void proceedErrorStatus(int statusCode, Core.IExecuteOperation executeOperation) {
        //TODO send error code to the runnable and remove runnable from cache array
    }

    private void close() {
        synchronized (mLock) {
            if (mGoogleApiClient != null) {
                Wearable.DataApi.removeListener(mGoogleApiClient, mDataListener);
                mGoogleApiClient.disconnect();
                mExecuteOperations.clear();
                mGoogleApiClient = null;
            }
        }
    }

    private GoogleApiClient getGoogleClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                    .addApi(Wearable.API)
                    .addConnectionCallbacks(mConnectionCallbacks)
                    .addOnConnectionFailedListener(mOnConnectionFailedListener)
                    .build();
            mGoogleApiClient.connect();
        }
        return mGoogleApiClient;
    }
}
