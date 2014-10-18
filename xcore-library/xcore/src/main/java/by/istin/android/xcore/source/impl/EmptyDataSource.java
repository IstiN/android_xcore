/**
 * 
 */
package by.istin.android.xcore.source.impl;

import java.io.IOException;

import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.IDataSource;
import by.istin.android.xcore.utils.Holder;


/**
 *
 * @author Uladzimir_Klyshevich
 * 
 */
public class EmptyDataSource implements IDataSource<Object> {

	public static final String APP_SERVICE_KEY = "xcore:emptydatasource";

	@Override
	public Object getSource(DataSourceRequest dataSourceRequest, Holder<Boolean> isCached) throws IOException {
		return null;
	}

	@Override
	public String getAppServiceKey() {
		return APP_SERVICE_KEY;
	}
	
}
