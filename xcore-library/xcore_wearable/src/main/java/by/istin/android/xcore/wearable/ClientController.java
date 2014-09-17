package by.istin.android.xcore.wearable;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

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

import java.io.IOException;
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
    public static final int TIMEOUT = 20000;

    private Handler mHandler;

    enum Status {
        NOT_STARTED, WAIT_RESULT, DONE, ERROR;
    }

    class ExecuteOperationStatus implements Runnable {

        @Override
        public void run() {
            synchronized (mLock) {
                if (mStatus == Status.WAIT_RESULT) {
                    setStatus(Status.ERROR);
                    sendError(mExecuteOperation.getDataSourceListener(), new IOException("timeout"));
                    mExecuteOperations.remove(this);
                    if (mExecuteOperations.isEmpty()) {
                        close();
                    }
                }
            }
        }

        private Core.IExecuteOperation mExecuteOperation;

        private Status mStatus = Status.NOT_STARTED;

        private long mPutRequestId;

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
            if (status == Status.WAIT_RESULT) {
                mHandler.postDelayed(this, TIMEOUT);
            } else {
                mHandler.removeCallbacks(this);
            }
        }

        public void setPutRequestId(long mPutRequestId) {
            this.mPutRequestId = mPutRequestId;
        }

        public long getPutRequestId() {
            return mPutRequestId;
        }

    }

    private GoogleApiClient mGoogleApiClient;

    private final Object mLock = new Object();

    private Context mContext;

    private List<ExecuteOperationStatus> mExecuteOperations = new ArrayList<ExecuteOperationStatus>();

    ClientController(Context context) {
        this.mContext = context;
        this.mHandler = new Handler(Looper.getMainLooper());
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
                                executeOperationStatus.setStatus(Status.DONE);
                                result = executeOperationStatus;
                                ISuccess success = result.getExecuteOperation().getSuccess();
                                int resultType = dataMap.getInt(WearableContract.PARAM_RESULT_TYPE, WearableContract.TYPE_UNKNOWN);
                                if (resultType != WearableContract.TYPE_ASSET) {
                                    Log.xd(this, "result is not asset " + resultType);
                                    byte[] byteArray = dataMap.getByteArray(WearableContract.PARAM_RESULT);
                                    switch (resultType) {
                                        case WearableContract.TYPE_CONTENT_VALUES:
                                            ContentValues contentValues = BytesUtils.contentValuesFromByteArray(byteArray);
                                            if (success != null) {
                                                success.success(contentValues);
                                            }
                                            break;
                                        case WearableContract.TYPE_CONTENT_VALUES_ARRAY:
                                            ContentValues[] arrayContentValues = BytesUtils.arrayContentValuesFromByteArray(byteArray);
                                            if (success != null) {
                                                success.success(arrayContentValues);
                                            }
                                            break;
                                        case WearableContract.TYPE_SERIALIZABLE:
                                            Serializable serializable = BytesUtils.serializableFromByteArray(byteArray);
                                            if (success != null) {
                                                success.success(serializable);
                                            }
                                            break;
                                        case WearableContract.TYPE_UNKNOWN:
                                            if (success != null) {
                                                success.success(null);
                                            }
                                            break;
                                    }
                                } else {
                                    Log.xd(this, "result is asset");
                                    Asset asset = dataMap.getAsset(WearableContract.PARAM_RESULT);
                                    Log.xd(this, "asset got " + asset);
                                    GoogleApiClient googleClient = getGoogleClient();
                                    Object assetStream = null;
                                    if (googleClient.isConnected()) {
                                        Log.xd(this, "try got asset stream client is connected " + asset);
                                        assetStream = Wearable.DataApi.getFdForAsset(
                                                googleClient, asset).await().getInputStream();
                                        Log.xd(this, "asset stream got");
                                    } else {
                                        Log.xd(this, "client disconnected, throw error ?");
                                    }
                                    if (success != null) {
                                        Log.xd(this, "return asset stream");
                                        success.success(assetStream);
                                    }
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
                    Log.xd(this, "finish iterate sync block");
                }
            }
        }

    };

    private GoogleApiClient.ConnectionCallbacks mConnectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {

        @Override
        public void onConnected(Bundle bundle) {
            Wearable.DataApi.addListener(getGoogleClient(), mDataListener);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.xd(this, "execute start proceed from onConnected");
                    proceed();
                }
            });
        }

        @Override
        public void onConnectionSuspended(final int i) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    proceedErrorConnectionSuspended(i);
                }
            });
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
            Log.xd(this, "finish cancel sync block");
        }
    }

    private void proceedErrorConnectionSuspended(int i) {
        synchronized (mLock) {
            IOException e = new IOException("error connection suspended: " + i);
            for (ExecuteOperationStatus executeOperationStatus : mExecuteOperations) {
                executeOperationStatus.setStatus(Status.ERROR);
                sendError(executeOperationStatus.getExecuteOperation().getDataSourceListener(), e);
            }
            close();
        }
    }

    private void proceedErrorConnectionFailed(ConnectionResult connectionResult) {
        Log.xd(this, "proceedErrorConnectionFailed synchronize lock");
        synchronized (mLock) {
            Log.xd(this, "inside proceedErrorConnectionFailed synchronize lock");
            IOException e = new IOException("error connection failed: " + connectionResult);
            for (ExecuteOperationStatus executeOperationStatus : mExecuteOperations) {
                executeOperationStatus.setStatus(Status.ERROR);
                sendError(executeOperationStatus.getExecuteOperation().getDataSourceListener(), e);
            }
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
                return;
            }
            Log.xd(this, "finish execute sync block");
        }
        Log.xd(this, "execute start proceed from execute");
        proceed();
    }

    private void sendError(Core.SimpleDataSourceServiceListener dataSourceListener, IOException e) {
        if (dataSourceListener != null) {
            dataSourceListener.onError(e);
        }
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
                if (executeOperationStatus.getStatus() != Status.WAIT_RESULT) {
                    executeOperationStatusHolder.set(executeOperationStatus);
                    executeOperationStatus.setStatus(Status.WAIT_RESULT);
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
            final Core.IExecuteOperation executeOperation = executeOperationStatus.getExecuteOperation();
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
                                    executeOperationStatus.setStatus(Status.ERROR);
                                    proceedErrorStatus(dataItemResult.getStatus().getStatusCode(), executeOperationStatus.getExecuteOperation());
                                    mExecuteOperations.remove(executeOperationStatus);
                                    if (mExecuteOperations.isEmpty()) {
                                        close();
                                    }
                                    Log.xd(this, "finish delete with error sync block");
                                }
                            }
                        }
                    });
            Log.xd(this, "finish proceed sync block");
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
        sendError(executeOperation.getDataSourceListener(), new IOException("error result status: " + statusCode));
    }

    private void close() {
        if (mGoogleApiClient != null) {
            Wearable.DataApi.removeListener(mGoogleApiClient, mDataListener);
            mGoogleApiClient.disconnect();
            mExecuteOperations.clear();
            mGoogleApiClient = null;
        }
        Log.xd(this, "closed");
    }

    private GoogleApiClient getGoogleClient() {
        if (mGoogleApiClient == null) {
            Log.xd(this, "client is null - will be created");
            mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                    .addApi(Wearable.API)
                    .addConnectionCallbacks(mConnectionCallbacks)
                    .addOnConnectionFailedListener(mOnConnectionFailedListener)
                    .build();
            Log.xd(this, "client created - will be connect");
            mGoogleApiClient.connect();
            Log.xd(this, "connected and return");
        } else {
            Log.xd(this, "return existing client");
        }
        return mGoogleApiClient;
    }
}
