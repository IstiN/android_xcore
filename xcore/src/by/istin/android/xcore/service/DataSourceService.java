/**
 * 
 */
package by.istin.android.xcore.service;

import java.io.Serializable;
import java.util.ArrayList;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.ResultReceiver;
import by.istin.android.xcore.processor.IProcessor;
import by.istin.android.xcore.provider.ModelContract;
import by.istin.android.xcore.service.RequestExecutor.ExecuteRunnable;
import by.istin.android.xcore.service.StatusResultReceiver.Status;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.DataSourceRequestEntity;
import by.istin.android.xcore.source.IDataSource;
import by.istin.android.xcore.utils.AppUtils;

/**
 * @author IstiN
 *
 */
public class DataSourceService extends Service {

	private static final String DATASOURCE_KEY = "datasourceKey";
	private static final String PROCESSOR_KEY = "processorKey";
	private static final String RESULT_RECEIVER = "resultReceiver";
	private final RequestExecutor requestExecutor = new RequestExecutor();
	
	/* (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return new ServiceBinder<DataSourceService>(this);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		onHandleIntent(intent);
		return super.onStartCommand(intent, flags, startId);
	}
	
	public static void execute(Context context, DataSourceRequest dataSourceRequest, String processorKey, String datasourceKey) {
		execute(context, dataSourceRequest, processorKey, datasourceKey, null);
	}
	
	public static void execute(Context context, DataSourceRequest dataSourceRequest, String processorKey, String datasourceKey, StatusResultReceiver resultReceiver) {
		Intent intent = createStartIntent(context, dataSourceRequest, processorKey, datasourceKey, resultReceiver);
		context.startService(intent);
	}
	
	public static Intent createStartIntent(Context context, DataSourceRequest dataSourceRequest, String processorKey, String datasourceKey, StatusResultReceiver resultReceiver) {
		Intent intent = new Intent(context, DataSourceService.class);
		intent.putExtra(DATASOURCE_KEY, datasourceKey);
		intent.putExtra(PROCESSOR_KEY, processorKey);
		intent.putExtra(RESULT_RECEIVER, resultReceiver);
		dataSourceRequest.toIntent(intent);
		return intent;
	}
	
	protected void onHandleIntent(Intent intent) {
		final DataSourceRequest dataSourceRequest = DataSourceRequest.fromIntent(intent);
		final ResultReceiver resultReceiver = intent.getParcelableExtra(RESULT_RECEIVER);
		final String processorKey = intent.getStringExtra(PROCESSOR_KEY);
		final IProcessor processor = (IProcessor) AppUtils.get(this, processorKey);
		final String datasourceKey = intent.getStringExtra(DATASOURCE_KEY);
		final IDataSource datasource = (IDataSource) AppUtils.get(this, datasourceKey);
		requestExecutor.execute(new ExecuteRunnable() {
			
			@Override
			public void run() {
				Bundle bundle = new Bundle();
				dataSourceRequest.toBundle(bundle);
				sendStatus(StatusResultReceiver.Status.START, resultReceiver, bundle);
				boolean isCacheble = dataSourceRequest.isCacheable();
				boolean isForceUpdateData = dataSourceRequest.isForceUpdateData();
				ContentValues contentValues = DataSourceRequestEntity.prepare(dataSourceRequest);
				Long requestId = contentValues.getAsLong(DataSourceRequestEntity.ID);
				if (isCacheble && !isForceUpdateData) {
					Cursor cursor = getContentResolver().query(ModelContract.getUri(DataSourceRequestEntity.class, requestId), null, null, null, null);
					try {
						if (cursor == null || !cursor.moveToFirst()) {
							getContentResolver().insert(ModelContract.getUri(DataSourceRequestEntity.class), contentValues);
						} else {
							ContentValues storedRequest = new ContentValues();
							DatabaseUtils.cursorRowToContentValues(cursor, storedRequest);
							Long expiration = storedRequest.getAsLong(DataSourceRequestEntity.EXPIRATION);
							Long lastUpdate = storedRequest.getAsLong(DataSourceRequestEntity.LAST_UPDATE);
							if (System.currentTimeMillis() - expiration < lastUpdate) {
								sendStatus(StatusResultReceiver.Status.CACHED, resultReceiver, bundle);
								return;
							} else {
								getContentResolver().insert(ModelContract.getUri(DataSourceRequestEntity.class), contentValues);	
							}
						}
					} finally {
						if (cursor != null) {
							cursor.close();
						}
					}
				}
				try {
					Object result = processor.execute(dataSourceRequest, datasource, datasource.getSource(dataSourceRequest));
					if (isCacheble) {
						processor.cache(DataSourceService.this, dataSourceRequest, result);
					} else {
						if (result instanceof Parcelable) {
							bundle.putParcelable(StatusResultReceiver.RESULT_KEY, (Parcelable)result);
						} else if (result instanceof Parcelable[]) {
							bundle.putParcelableArray(StatusResultReceiver.RESULT_KEY, (Parcelable[])result);
						} else if (result instanceof Serializable) {
							bundle.putSerializable(StatusResultReceiver.RESULT_KEY, (Serializable)result);
						} else if (result instanceof ArrayList<?>) {
							bundle.putParcelableArrayList(StatusResultReceiver.RESULT_KEY, (ArrayList<? extends Parcelable>)result);
						}
					}
					sendStatus(StatusResultReceiver.Status.DONE, resultReceiver, bundle);
				} catch (Exception e) {
					getContentResolver().delete(ModelContract.getUri(DataSourceRequestEntity.class, requestId), null, null);
					bundle.putSerializable(StatusResultReceiver.ERROR_KEY, e);
					sendStatus(StatusResultReceiver.Status.ERROR, resultReceiver, bundle);	
				}				
			}
			
			@Override
			public String createKey() {
				return dataSourceRequest.toUriParams();
			}
			
		});
	}

	private void sendStatus(Status status, ResultReceiver resultReceiver, Bundle bundle) {
		if (resultReceiver != null) {
			resultReceiver.send(status.ordinal(), bundle);
		}
	}

}
