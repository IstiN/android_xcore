package by.istin.android.xcore.processor;

import android.content.ContentValues;

import java.util.List;

import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.db.impl.DBHelper;
import by.istin.android.xcore.model.SuperBigTestEntity;
import by.istin.android.xcore.processor.impl.AbstractGsonBatchProcessor;
import by.istin.android.xcore.provider.IDBContentProviderSupport;
import by.istin.android.xcore.source.DataSourceRequest;

/**
 * Created by IstiN on 13.11.13.
 */
public class SuperBigEntityBatchProcessor extends AbstractGsonBatchProcessor<SuperBigEntityBatchProcessor.Response> {

    public static class Response {

        public List<ContentValues> listings;

    }

    public static final String APP_SERVICE_KEY = "core:superbigtestentity:processor";

    public SuperBigEntityBatchProcessor(IDBContentProviderSupport contentProviderSupport) {
        super(SuperBigTestEntity.class, SuperBigEntityBatchProcessor.Response.class, contentProviderSupport);
    }

    @Override
    protected void onStartProcessing(DataSourceRequest dataSourceRequest, IDBConnection dbConnection) {
        super.onStartProcessing(dataSourceRequest, dbConnection);
        dbConnection.delete(DBHelper.getTableName(SuperBigTestEntity.class), null, null);
    }

    protected int getListBufferSize() {
        return 0;
    }

    @Override
    public String getAppServiceKey() {
        return APP_SERVICE_KEY;
    }

}
