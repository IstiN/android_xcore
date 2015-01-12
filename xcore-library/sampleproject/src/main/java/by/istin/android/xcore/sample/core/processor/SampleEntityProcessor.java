package by.istin.android.xcore.sample.core.processor;

import android.content.ContentValues;

import by.istin.android.xcore.ContextHolder;
import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.db.impl.DBHelper;
import by.istin.android.xcore.processor.impl.AbstractGsonBatchProcessor;
import by.istin.android.xcore.provider.IDBContentProviderSupport;
import by.istin.android.xcore.sample.core.model.SampleEntity;
import by.istin.android.xcore.source.DataSourceRequest;

/**
 * Created by IstiN on 13.11.13.
 */
public class SampleEntityProcessor extends AbstractGsonBatchProcessor<ContentValues[]> {

    public static final String APP_SERVICE_KEY = "core:sampleentity:processor";

    public SampleEntityProcessor(IDBContentProviderSupport contentProviderSupport) {
        super(SampleEntity.class, ContentValues[].class, contentProviderSupport);
    }

    @Override
    public String getAppServiceKey() {
        return APP_SERVICE_KEY;
    }

    @Override
    protected void onStartProcessing(DataSourceRequest dataSourceRequest, IDBConnection dbConnection) {
        super.onStartProcessing(dataSourceRequest, dbConnection);
        if (dataSourceRequest.getParam("page").equals("1")) {
            dbConnection.delete(DBHelper.getTableName(SampleEntity.class), null, null);
        }
    }

    @Override
    protected void onProcessingFinish(DataSourceRequest dataSourceRequest, ContentValues[] contentValueses) throws Exception {
        super.onProcessingFinish(dataSourceRequest, contentValueses);
        notifyChange(ContextHolder.get(), SampleEntity.class);
    }
}
