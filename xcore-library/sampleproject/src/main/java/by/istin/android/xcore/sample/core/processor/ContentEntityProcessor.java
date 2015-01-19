package by.istin.android.xcore.sample.core.processor;

import android.content.ContentValues;

import java.util.List;

import by.istin.android.xcore.ContextHolder;
import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.db.impl.DBHelper;
import by.istin.android.xcore.processor.impl.AbstractGsonBatchProcessor;
import by.istin.android.xcore.provider.IDBContentProviderSupport;
import by.istin.android.xcore.sample.core.model.Content;
import by.istin.android.xcore.sample.core.model.SampleEntity;
import by.istin.android.xcore.source.DataSourceRequest;

/**
 * Created by IstiN on 13.11.13.
 */
public class ContentEntityProcessor extends AbstractGsonBatchProcessor<ContentEntityProcessor.Response> {

    public static class Response {

        public static class Data {
            private List<ContentValues> updates;
        }

        private Data data;

    }

    public static final String APP_SERVICE_KEY = "core:advancedentity:processor";

    public ContentEntityProcessor(IDBContentProviderSupport contentProviderSupport) {
        super(Content.class, ContentEntityProcessor.Response.class, contentProviderSupport);
    }

    @Override
    public String getAppServiceKey() {
        return APP_SERVICE_KEY;
    }

    @Override
    protected void onStartProcessing(DataSourceRequest dataSourceRequest, IDBConnection dbConnection) {
        super.onStartProcessing(dataSourceRequest, dbConnection);
        dbConnection.delete(DBHelper.getTableName(SampleEntity.class), null, null);
    }

    @Override
    protected void onProcessingFinish(DataSourceRequest dataSourceRequest, Response response) throws Exception {
        super.onProcessingFinish(dataSourceRequest, response);
        notifyChange(ContextHolder.get(), Content.class);
    }

    @Override
    protected int getListBufferSize() {
        return 0;
    }
}
