package by.istin.android.xcore.processor;

import android.content.ContentValues;

import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.model.SimpleEntityWithSubJson;
import by.istin.android.xcore.processor.impl.AbstractGsonBatchProcessor;
import by.istin.android.xcore.provider.IDBContentProviderSupport;
import by.istin.android.xcore.provider.ModelContract;
import by.istin.android.xcore.source.DataSourceRequest;

/**
 * Created by IstiN on 13.11.13.
 */
public class SimpleEntityWithSubJsonBatchProcessor extends AbstractGsonBatchProcessor<ContentValues[]> {

    public static final String APP_SERVICE_KEY = "core:simpleentitywithsubjsonbatch:processor";

    public SimpleEntityWithSubJsonBatchProcessor(IDBContentProviderSupport contentProviderSupport) {
        super(SimpleEntityWithSubJson.class, ContentValues[].class, contentProviderSupport);
    }

    @Override
    public String getAppServiceKey() {
        return APP_SERVICE_KEY;
    }

    @Override
    protected void onStartProcessing(DataSourceRequest dataSourceRequest, IDBConnection dbConnection) {
        if (dataSourceRequest.getParam("page").equals("1")) {
            getHolderContext().getContentResolver().delete(ModelContract.getUri(SimpleEntityWithSubJson.class), null, null);
        }
        super.onStartProcessing(dataSourceRequest, dbConnection);
    }

}
