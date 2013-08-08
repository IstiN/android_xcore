/**
 * 
 */
package by.istin.android.xcore.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * @author IstiN
 *
 */
public abstract class StatusResultReceiver extends ResultReceiver {

	public static final String ERROR_KEY = "xcore:error_key";
	
	public static final String RESULT_KEY = "xcore:result_key";
	
	public static enum Status {
		ADD_TO_QUEUE, START, CACHED, ERROR, DONE;
	}
	
	public StatusResultReceiver(Handler handler) {
		super(handler);
	}

	@Override
	protected void onReceiveResult(int resultCode, Bundle resultData) {
		Status status = Status.values()[resultCode];
		switch (status) {
		case ADD_TO_QUEUE:
			onAddToQueue(resultData);
			break;
		case START:
			onStart(resultData);
			break;
		case CACHED:
			onCached(resultData);
			break;
		case DONE:
			onDone(resultData);
			break;
		case ERROR:
			onError((Exception)resultData.getSerializable(ERROR_KEY));
			break;

		default:
			break;
		}
		super.onReceiveResult(resultCode, resultData);
	}

	protected void onCached(Bundle resultData) {
		
	}

	protected void onAddToQueue(Bundle resultData) {

	}

	public abstract void onStart(Bundle resultData);
	
	public abstract void onDone(Bundle resultData);
	
	public abstract void onError(Exception exception);
	
}
