package by.istin.android.xcore.issues.issue12.processor;

import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.issues.issue12.model.DayEntity;
import by.istin.android.xcore.issues.issue12.response.DaysResponse;
import by.istin.android.xcore.processor.impl.AbstractGsonBatchProcessor;
import by.istin.android.xcore.provider.IDBContentProviderSupport;
import by.istin.android.xcore.provider.ModelContract;
import by.istin.android.xcore.source.DataSourceRequest;

/**
 * Created by IstiN on 13.11.13.
 */
public class DaysBatchProcessor extends AbstractGsonBatchProcessor<DaysResponse> {

    public static final String APP_SERVICE_KEY = "core:daysbatch:processor";

    public DaysBatchProcessor(IDBContentProviderSupport contentProviderSupport) {
        super(DayEntity.class, DaysResponse.class, contentProviderSupport);
    }

    @Override
    public String getAppServiceKey() {
        return APP_SERVICE_KEY;
    }

    @Override
    protected void onStartProcessing(DataSourceRequest dataSourceRequest, IDBConnection dbConnection) {
        getHolderContext().getContentResolver().delete(ModelContract.getUri(DayEntity.class), null, null);
        super.onStartProcessing(dataSourceRequest, dbConnection);
    }

}
