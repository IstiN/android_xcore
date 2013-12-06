package by.istin.android.xcore.processor;

import android.content.ContentValues;
import android.content.Context;

import by.istin.android.xcore.model.SimpleEntity;
import by.istin.android.xcore.processor.impl.GsonArrayContentValuesProcessor;
import by.istin.android.xcore.provider.ModelContract;
import by.istin.android.xcore.source.DataSourceRequest;

/**
 * Created by IstiN on 13.11.13.
 */
public class SimpleEntityProcessor extends GsonArrayContentValuesProcessor {

    public static final String APP_SERVICE_KEY = "core:simpleentity:processor";

    public SimpleEntityProcessor() {
        super(SimpleEntity.class);
    }

    @Override
    public String getAppServiceKey() {
        return APP_SERVICE_KEY;
    }

    @Override
    public void cache(Context context, DataSourceRequest dataSourceRequest, ContentValues[] result) {
        if (dataSourceRequest.getParam("page").equals("1")) {
            context.getContentResolver().delete(ModelContract.getUri(SimpleEntity.class), null, null);
        }
        super.cache(context, dataSourceRequest, result);
    }
}
