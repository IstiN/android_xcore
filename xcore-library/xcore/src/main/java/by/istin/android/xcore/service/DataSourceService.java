/**
 * 
 */
package by.istin.android.xcore.service;

import android.content.Context;
import android.content.Intent;

import by.istin.android.xcore.source.DataSourceRequest;

/**
 * @author IstiN
 *
 */
public class DataSourceService extends AbstractExecutorService {

    public static void execute(Context context, DataSourceRequest dataSourceRequest, String processorKey, String dataSourceKey) {
        execute(context, dataSourceRequest, processorKey, dataSourceKey, DataSourceService.class);
    }

    public static void execute(Context context, DataSourceRequest dataSourceRequest, String processorKey, String dataSourceKey, StatusResultReceiver resultReceiver) {
        execute(context, dataSourceRequest, processorKey, dataSourceKey, resultReceiver, DataSourceService.class, true);
    }

    public static Intent createStartIntent(Context context, DataSourceRequest dataSourceRequest, String processorKey, String dataSourceKey, StatusResultReceiver resultReceiver) {
        return createStartIntent(context, dataSourceRequest, processorKey, dataSourceKey, resultReceiver, DataSourceService.class);
    }

}