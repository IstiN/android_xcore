package by.istin.android.xcore.processor.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.ContentValues;
import by.istin.android.xcore.gson.ContentValuesAdaper;
import by.istin.android.xcore.processor.IProcessor;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.IDataSource;
import by.istin.android.xcore.utils.IOUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class AbstractGsonProcessor<Result> implements IProcessor<Result>{

	private Class<?> clazz;
	
	private Class<?> resultClassName;
	
	private Gson gson;
	
	private ContentValuesAdaper contentValuesAdaper;
	
	public AbstractGsonProcessor(Class<?> clazz, Class<? extends Result> resultClassName) {
		super();
		this.clazz = clazz;
		this.resultClassName = resultClassName;
		GsonBuilder gsonBuilder = new GsonBuilder();
		contentValuesAdaper = new ContentValuesAdaper(clazz);
		gsonBuilder.registerTypeHierarchyAdapter(ContentValues.class, contentValuesAdaper);
		gson = gsonBuilder.create();
	}
	
	@Override
	public Result execute(DataSourceRequest dataSourceRequest, IDataSource dataSource, InputStream inputStream) throws Exception {
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader, 8192);
		try {
			return process(getGson(), bufferedReader);	
		} finally {
			IOUtils.close(inputStream);
			IOUtils.close(inputStreamReader);
			IOUtils.close(bufferedReader);
		}
	}
	
	protected Result process(Gson gson, BufferedReader bufferedReader) {
		return (Result) getGson().fromJson(bufferedReader, resultClassName);
	}
	

	public ContentValuesAdaper getContentValuesAdaper() {
		return contentValuesAdaper;
	}

	public void setContentValuesAdaper(ContentValuesAdaper contentValuesAdaper) {
		this.contentValuesAdaper = contentValuesAdaper;
	}
	
	public Class<?> getClazz() {
		return clazz;
	}

	public Gson getGson() {
		return gson;
	}
	
	
}
