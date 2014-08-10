package by.istin.android.xcore.service.manager;

import android.content.Context;
import android.os.Bundle;
import android.os.ResultReceiver;

import by.istin.android.xcore.XCoreHelper;
import by.istin.android.xcore.service.RequestExecutor;
import by.istin.android.xcore.service.StatusResultReceiver;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.utils.AppUtils;

public interface IRequestManager extends XCoreHelper.IAppServiceKey {

    public static final String APP_SERVICE_KEY = "xcore:request:manager";

    void stop(ResultReceiver resultReceiver);

    RequestExecutor getRequestExecutor();

    RequestExecutor createExecutorService();

    void onHandleRequest(Context context, DataSourceRequest dataSourceRequest, String processorKey, String dataSourceKey, StatusResultReceiver resultReceiver);

    public void run(Context context, String processorKey, String dataSourceKey, RequestExecutor.ExecuteRunnable executeRunnable, DataSourceRequest dataSourceRequest, Bundle dataSourceRequestBundle, ResultReceiver resultReceiver);

    public class Impl {

        public static void register(XCoreHelper coreHelper) {
            coreHelper.registerAppService(new DefaultRequestManager());
            coreHelper.registerAppService(new SyncRequestManager());
        }

        public static IRequestManager get(Class<?> serviceClass, Context context) {
            return AppUtils.get(context, APP_SERVICE_KEY + serviceClass.getName());
        }

    }
}
