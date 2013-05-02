package by.istin.android.xcore.processor.impl;

import android.content.ContentValues;
import android.content.Context;
import by.istin.android.xcore.provider.ModelContract;
import by.istin.android.xcore.source.DataSourceRequest;

public class GsonContentValuesProcessor extends AbstractGsonProcessor<ContentValues> {

	public GsonContentValuesProcessor(Class<?> clazz) {
		super(clazz, ContentValues.class);
	}

	@Override
	public String getAppServiceKey() {
		return "xcore:"+getClazz()+":processor";
	}

	@Override
	public void cache(Context context, DataSourceRequest dataSourceRequest, ContentValues result) {
		context.getContentResolver().insert(ModelContract.getUri(getClazz()), result);
	}

}