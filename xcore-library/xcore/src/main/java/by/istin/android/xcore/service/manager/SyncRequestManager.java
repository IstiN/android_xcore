package by.istin.android.xcore.service.manager;

import java.util.concurrent.LinkedBlockingQueue;

import by.istin.android.xcore.service.RequestExecutor;
import by.istin.android.xcore.service.SyncDataSourceService;

public class SyncRequestManager extends DefaultRequestManager {

    private static final String SERVICE_KEY = IRequestManager.APP_SERVICE_KEY + SyncDataSourceService.class.getName();

    @Override
    public String getAppServiceKey() {
        return SERVICE_KEY;
    }

    @Override
    public RequestExecutor createExecutorService() {
        return new RequestExecutor(1, new LinkedBlockingQueue<Runnable>());
    }
}
