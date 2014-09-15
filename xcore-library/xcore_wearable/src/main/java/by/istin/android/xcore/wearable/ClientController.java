package by.istin.android.xcore.wearable;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import by.istin.android.xcore.Core;
import by.istin.android.xcore.callable.ISuccess;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.utils.BytesUtils;
import by.istin.android.xcore.utils.Holder;
import by.istin.android.xcore.utils.Log;

/**
 * Created by IstiN on 14.09.2014.
 */
class ClientController {

    public static final String PUT_DATA_MAP_REQUEST_ID = "pdmr_id";

    static class ExecuteOperationStatus {

        static enum Status {
            NOT_STARTED, WAIT_RESULT;
        }

        private Core.IExecuteOperation mExecuteOperation;

        private Status mStatus = Status.NOT_STARTED;

        private long mPutRequestId;

        private boolean isSync = false;

        private Object mResult;

        ExecuteOperationStatus(Core.IExecuteOperation mExecuteOperation, boolean isSync) {
            this.mExecuteOperation = mExecuteOperation;
            this.isSync = isSync;
        }

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

        public void setPutRequestId(long mPutRequestId) {
            this.mPutRequestId = mPutRequestId;
        }

        public long getPutRequestId() {
            return mPutRequestId;
        }

        public boolean isSync() {
            return isSync;
        }

        public Object getResult() {
            return mResult;
        }

        public void setResult(Object mResult) {
            this.mResult = mResult;
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
            for (DataEvent event : dataEvents) {
                String lastPathSegment = event.getDataItem().getUri().getLastPathSegment();
                if (!lastPathSegment.equals(WearableContract.URI_RESULT_SEGMENT)) {
                    continue;
                }
                DataItem dataItem = event.getDataItem();
                DataMapItem dataMapItem = DataMapItem.fromDataItem(dataItem);
                final DataMap dataMap = dataMapItem.getDataMap();
                long id = dataMap.getLong(PUT_DATA_MAP_REQUEST_ID, -1l);
                Log.xd(this, "got result");
                if (id != -1) {
                    Log.xd(this, "lock start parsing synchronize lock");
                    synchronized (mLock) {
                        Log.xd(this, "inside start parsing synchronize lock");
                        ExecuteOperationStatus result = null;
                        Log.xd(this, "start iterate");
                        for (ExecuteOperationStatus executeOperationStatus : mExecuteOperations) {
                            long putRequestId = executeOperationStatus.getPutRequestId();
                            Log.xd(this, "putRequestId " + putRequestId);
                            if (putRequestId == id) {
                                result = executeOperationStatus;
                                ISuccess success = result.getExecuteOperation().getSuccess();
                                int resultType = dataMap.getInt(WearableContract.PARAM_RESULT_TYPE, WearableContract.TYPE_UNKNOWN);
                                if (resultType != WearableContract.TYPE_ASSET) {
                                    byte[] byteArray = dataMap.getByteArray(WearableContract.PARAM_RESULT);
                                    switch (resultType) {
                                        case WearableContract.TYPE_CONTENT_VALUES:
                                            ContentValues contentValues = BytesUtils.contentValuesFromByteArray(byteArray);
                                            if (success != null) {
                                                success.success(contentValues);
                                            }
                                            setSyncResult(executeOperationStatus, contentValues);
                                            break;
                                        case WearableContract.TYPE_CONTENT_VALUES_ARRAY:
                                            ContentValues[] arrayContentValues = BytesUtils.arrayContentValuesFromByteArray(byteArray);
                                            if (success != null) {
                                                success.success(arrayContentValues);
                                            }
                                            setSyncResult(executeOperationStatus, arrayContentValues);
                                            break;
                                        case WearableContract.TYPE_SERIALIZABLE:
                                            Serializable serializable = BytesUtils.serializableFromByteArray(byteArray);
                                            if (success != null) {
                                                success.success(serializable);
                                            }
                                            setSyncResult(executeOperationStatus, serializable);
                                            break;
                                        case WearableContract.TYPE_UNKNOWN:
                                            if (success != null) {
                                                success.success(null);
                                            }
                                            setSyncResult(executeOperationStatus, null);
                                            break;
                                    }
                                } else {
                                    Asset asset = dataMap.getAsset(WearableContract.PARAM_RESULT);
                                    GoogleApiClient googleClient = getGoogleClient();
                                    Object assetStream = null;
                                    if (googleClient.isConnected()) {
                                        assetStream = Wearable.DataApi.getFdForAsset(
                                                googleClient, asset).await().getInputStream();

                                    }
                                    if (success != null) {
                                        success.success(assetStream);
                                    }
                                    setSyncResult(executeOperationStatus, assetStream);
                                }
                                break;
                            }
                        }
                        if (result != null) {
                            mExecuteOperations.remove(result);
                        }
                        if (mExecuteOperations.isEmpty()) {
                            close();
                        }
                    }
                    Log.xd(this, "end locking");
                }
            }
        }

    };

    private void setSyncResult(ExecuteOperationStatus executeOperationStatus, Object object) {
        if (executeOperationStatus.isSync()) {
            Core.IExecuteOperation executeOperation = executeOperationStatus.getExecuteOperation();
            synchronized (executeOperation) {
                executeOperationStatus.setResult(object);
                executeOperation.notify();
            }
        }
    }

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
        Log.xd(this, "cancel synchronize lock");
        synchronized (mLock) {
            Log.xd(this, "inside cancel synchronize lock");
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
        Log.xd(this, "proceedErrorConnectionFailed synchronize lock");
        synchronized (mLock) {
            Log.xd(this, "inside proceedErrorConnectionFailed synchronize lock");
            //todo notify execute operations
            close();
        }
    }

    public void execute(Core.IExecuteOperation executeOperation) {
        Log.xd(this, "execute synchronize lock");
        synchronized (mLock) {
            Log.xd(this, "inside execute synchronize lock");
            ExecuteOperationStatus executeOperationStatus = new ExecuteOperationStatus(executeOperation);
            mExecuteOperations.add(executeOperationStatus);
            GoogleApiClient googleApiClient = getGoogleClient();
            if (!googleApiClient.isConnected()) {
                //TODO send error status
                return;
            }
        }
        proceed();
    }

    public Object executeSync(Core.IExecuteOperation executeOperation) throws Exception {
        Log.xd(this, "execute sync");
        ExecuteOperationStatus executeOperationStatus;
        Log.xd(this, "executeSync synchronize lock");
        synchronized (mLock) {
            Log.xd(this, "inside executeSync synchronize lock");
            executeOperationStatus = new ExecuteOperationStatus(executeOperation, true);
            mExecuteOperations.add(executeOperationStatus);
            GoogleApiClient googleApiClient = getGoogleClient();
            if (!googleApiClient.isConnected()) {
                Log.xd(this, "is not connected");
                //TODO send error status
                return null;
            }
            Log.xd(this, "is connected");
        }
        proceed();
        Log.xd(this, "start waiting");
        synchronized (executeOperation) {
            try {
                executeOperation.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return executeOperationStatus.getResult();

    }

    private void proceed() {
        Log.xd(this, "proceed synchronize lock");
        synchronized (mLock) {
            Log.xd(this, "inside proceed synchronize lock");
            if (mExecuteOperations.isEmpty()) {
                close();
                return;
            }
            GoogleApiClient googleClient = getGoogleClient();
            if (!googleClient.isConnected()) {
                return;
            }

            final Holder<ExecuteOperationStatus> executeOperationStatusHolder = new Holder<ExecuteOperationStatus>();
            Log.xd(this, "find new operations");
            for (ExecuteOperationStatus executeOperationStatus : mExecuteOperations) {
                if (executeOperationStatus.getStatus() != ExecuteOperationStatus.Status.WAIT_RESULT) {
                    executeOperationStatusHolder.set(executeOperationStatus);
                    executeOperationStatus.setStatus(ExecuteOperationStatus.Status.WAIT_RESULT);
                    break;
                }
            }
            if (executeOperationStatusHolder.isNull()) {
                Log.xd(this, "execute operations is null");
                //all runnables executed and waiting results
                return;
            }
            Log.xd(this, "execute new operations");

            final ExecuteOperationStatus executeOperationStatus = executeOperationStatusHolder.get();
            Core.IExecuteOperation executeOperation = executeOperationStatus.getExecuteOperation();
            PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(WearableContract.URI_EXECUTE);

            long putRequestId = initDataMapRequest(putDataMapRequest, executeOperation);
            executeOperationStatus.setPutRequestId(putRequestId);
            PutDataRequest request = putDataMapRequest.asPutDataRequest();
            Log.xd(this, "put dataapi request");
            Wearable.DataApi.putDataItem(googleClient, request)
                    .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                        @Override
                        public void onResult(DataApi.DataItemResult dataItemResult) {
                            Log.xd(this, "got request result");
                            if (!dataItemResult.getStatus().isSuccess()) {
                                Log.xd(this, "not success");
                                Log.xd(this, "not success synchronize lock");
                                synchronized (mLock) {
                                    Log.xd(this, "inside not success synchronize lock");
                                    proceedErrorStatus(dataItemResult.getStatus().getStatusCode(), executeOperationStatus.getExecuteOperation());
                                    mExecuteOperations.remove(executeOperationStatus);
                                    if (mExecuteOperations.isEmpty()) {
                                        close();
                                    }
                                    Log.xd(this, "delete");
                                }
                            }
                        }
                    });
        }
    }

    private long initDataMapRequest(PutDataMapRequest putDataMapRequest, Core.IExecuteOperation executeOperation) {
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
        long putRequestId = System.nanoTime();
        dataMap.putLong(PUT_DATA_MAP_REQUEST_ID, putRequestId);
        if (selectionArgs != null) {
            dataMap.putStringArray(WearableContract.PARAM_SELECTION_ARGS_KEY, selectionArgs);
        }
        if (resultQueryUri != null) {
            dataMap.putString(WearableContract.PARAM_RESULT_URI_KEY, resultQueryUri.toString());
        }
        return putRequestId;
    }

    private void proceedErrorStatus(int statusCode, Core.IExecuteOperation executeOperation) {
        //TODO send error code to the runnable and remove runnable from cache array
    }

    private void close() {
        Log.xd(this, "close synchronize lock");
        synchronized (mLock) {
            Log.xd(this, "inside synchronize lock");
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
