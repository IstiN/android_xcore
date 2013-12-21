package by.istin.android.xcore.sample.core.processor;

import android.content.ContentValues;
import android.content.Context;

import by.istin.android.xcore.processor.impl.GsonArrayContentValuesProcessor;
import by.istin.android.xcore.provider.ModelContract;
import by.istin.android.xcore.sample.core.model.SampleEntity;
import by.istin.android.xcore.source.DataSourceRequest;

/**
 * Created by IstiN on 13.11.13.
 */
public class SampleEntityProcessor extends GsonArrayContentValuesProcessor {

    public static final String APP_SERVICE_KEY = "core:sampleentity:processor";

    public SampleEntityProcessor() {
        super(SampleEntity.class);
    }

    @Override
    public String getAppServiceKey() {
        return APP_SERVICE_KEY;
    }

    @Override
    public void cache(Context context, DataSourceRequest dataSourceRequest, ContentValues[] result) {
        if (dataSourceRequest.getParam("page").equals("1")) {
            context.getContentResolver().delete(ModelContract.getUri(SampleEntity.class), null, null);
        }
        super.cache(context, dataSourceRequest, result);
    }
}
