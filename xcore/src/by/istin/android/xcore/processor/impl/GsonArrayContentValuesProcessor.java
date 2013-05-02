package by.istin.android.xcore.processor.impl;

import android.content.ContentValues;
import android.content.Context;
import by.istin.android.xcore.provider.ModelContract;
import by.istin.android.xcore.source.DataSourceRequest;

public class GsonArrayContentValuesProcessor extends AbstractGsonProcessor<ContentValues[]> {

	public GsonArrayContentValuesProcessor(Class<?> clazz) {
		super(clazz, ContentValues[].class);
	}

	@Override
	public String getAppServiceKey() {
		return "xcore:"+getClazz()+":array:processor";
	}

	@Override
	public void cache(Context context, DataSourceRequest dataSourceRequest, ContentValues[] result) {
		context.getContentResolver().bulkInsert(ModelContract.getUri(getClazz()), result);
	}
}