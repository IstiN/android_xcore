package by.istin.android.xcore.processor;

import android.content.ContentValues;

import by.istin.android.xcore.ContextHolder;
import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.db.impl.DBHelper;
import by.istin.android.xcore.model.SimpleEntity;
import by.istin.android.xcore.processor.impl.AbstractGsonBatchProcessor;
import by.istin.android.xcore.provider.IDBContentProviderSupport;
import by.istin.android.xcore.provider.ModelContract;
import by.istin.android.xcore.source.DataSourceRequest;

/**
 * Created by IstiN on 13.11.13.
 */
public class SimpleEntityBatchProcessor extends AbstractGsonBatchProcessor<ContentValues[]> {

    public static final String APP_SERVICE_KEY = "core:simpleentitybatch:processor";

    public SimpleEntityBatchProcessor(IDBContentProviderSupport contentProviderSupport) {
        super(SimpleEntity.class, ContentValues[].class, contentProviderSupport);
    }

    @Override
    public String getAppServiceKey() {
        return APP_SERVICE_KEY;
    }

    @Override
    protected void onStartProcessing(DataSourceRequest dataSourceRequest, IDBConnection dbConnection) {
        if (dataSourceRequest.getParam("page").equals("1")) {
            dbConnection.delete(DBHelper.getTableName(SimpleEntity.class), null, null);
        }
        super.onStartProcessing(dataSourceRequest, dbConnection);
    }


    @Override
    protected void onProcessingFinish(DataSourceRequest dataSourceRequest, ContentValues[] contentValueses) throws Exception {
        super.onProcessingFinish(dataSourceRequest, contentValueses);
        notifyChange(ContextHolder.get(), SimpleEntity.class);
    }
}
