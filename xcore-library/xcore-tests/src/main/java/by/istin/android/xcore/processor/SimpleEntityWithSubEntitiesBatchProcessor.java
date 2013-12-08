package by.istin.android.xcore.processor;

import android.content.ContentValues;

import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.model.SimpleEntityWithParent;
import by.istin.android.xcore.model.SimpleEntityWithParent1;
import by.istin.android.xcore.model.SimpleEntityWithParent2;
import by.istin.android.xcore.model.SimpleEntityWithSubEntities;
import by.istin.android.xcore.model.SimpleEntityWithSubEntity;
import by.istin.android.xcore.processor.impl.AbstractGsonBatchProcessor;
import by.istin.android.xcore.provider.IDBContentProviderSupport;
import by.istin.android.xcore.provider.ModelContract;
import by.istin.android.xcore.source.DataSourceRequest;

/**
 * Created by IstiN on 13.11.13.
 */
public class SimpleEntityWithSubEntitiesBatchProcessor extends AbstractGsonBatchProcessor<ContentValues[]> {

    public static final String APP_SERVICE_KEY = "core:simpleentitywithsubentitiesbatch:processor";

    public SimpleEntityWithSubEntitiesBatchProcessor(IDBContentProviderSupport contentProviderSupport) {
        super(SimpleEntityWithSubEntities.class, ContentValues[].class, contentProviderSupport);
    }

    @Override
    public String getAppServiceKey() {
        return APP_SERVICE_KEY;
    }

    @Override
    protected void onStartProcessing(DataSourceRequest dataSourceRequest, IDBConnection dbConnection) {
        if (dataSourceRequest.getParam("page").equals("1")) {
            getHolderContext().getContentResolver().delete(ModelContract.getUri(SimpleEntityWithParent1.class), null, null);
            getHolderContext().getContentResolver().delete(ModelContract.getUri(SimpleEntityWithParent2.class), null, null);
            getHolderContext().getContentResolver().delete(ModelContract.getUri(SimpleEntityWithSubEntities.class), null, null);
        }
        super.onStartProcessing(dataSourceRequest, dbConnection);
    }

}
