/**
 * 
 */
package by.istin.android.xcore.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * @author IstiN
 *
 */
public class DataSourceService extends Service {

	/* (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent arg0) {
		return new ServiceBinder<DataSourceService>(this);
	}

}
