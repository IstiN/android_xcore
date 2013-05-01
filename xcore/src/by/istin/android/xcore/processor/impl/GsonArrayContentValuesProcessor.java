package by.istin.android.xcore.processor.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.ContentValues;
import android.content.Context;
import by.istin.android.xcore.gson.ContentValuesAdaper;
import by.istin.android.xcore.processor.IProcessor;
import by.istin.android.xcore.provider.ModelContract;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.IDataSource;
import by.istin.android.xcore.utils.IOUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonArrayContentValuesProcessor implements IProcessor<ContentValues[]> {

	private Class<?> clazz;
	
	private Gson gson;
	
	public GsonArrayContentValuesProcessor(Class<?> clazz) {
		super();
		this.clazz = clazz;
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeHierarchyAdapter(ContentValues.class, new ContentValuesAdaper(clazz));
		gson = gsonBuilder.create();
	}

	@Override
	public String getAppServiceKey() {
		return "xcore:"+clazz+":array:processor";
	}

	@Override
	public ContentValues[] execute(DataSourceRequest dataSourceRequest, IDataSource dataSource, InputStream inputStream) throws Exception {
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader, 8192);
		try {
			return process(gson, bufferedReader);	
		} finally {
			IOUtils.close(inputStream);
			IOUtils.close(inputStreamReader);
			IOUtils.close(bufferedReader);
		}
	}

	protected ContentValues[] process(Gson gson, BufferedReader bufferedReader) {
		return gson.fromJson(bufferedReader, ContentValues[].class);
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	@Override
	public void cache(Context context, DataSourceRequest dataSourceRequest, ContentValues[] result) {
		context.getContentResolver().bulkInsert(ModelContract.getUri(clazz), result);
	}
}