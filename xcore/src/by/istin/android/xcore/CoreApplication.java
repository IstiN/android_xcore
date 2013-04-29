/**
 * 
 */
package by.istin.android.xcore;

import java.util.HashMap;
import java.util.Map;

import android.app.Application;
import by.istin.android.xcore.source.IDataSource;

/**
 * @author Uladzimir_Klyshevich
 *
 */
public class CoreApplication extends Application {

	private Map<String, IDataSource> mDataSources = new HashMap<String, IDataSource>();
	
	public void registerDataSource(String dataSourceKey, IDataSource dataSource) {
		mDataSources.put(dataSourceKey, dataSource);
	}
	
}
