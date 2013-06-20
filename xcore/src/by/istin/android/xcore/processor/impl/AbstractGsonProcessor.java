package by.istin.android.xcore.processor.impl;

import android.content.ContentValues;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import by.istin.android.xcore.gson.ContentValuesAdapter;
import by.istin.android.xcore.processor.IProcessor;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.IDataSource;
import by.istin.android.xcore.utils.IOUtils;

public abstract class AbstractGsonProcessor<Result> implements IProcessor<Result>{

	private Class<?> clazz;
	
	private Class<? extends Result> resultClassName;
	
	private Gson gson;
	
	private ContentValuesAdapter contentValuesAdapter;
	
	public AbstractGsonProcessor(Class<?> clazz, Class<? extends Result> resultClassName) {
		super();
		this.clazz = clazz;
		this.resultClassName = resultClassName;
		contentValuesAdapter = new ContentValuesAdapter(clazz);
		gson = createGsonWithContentValuesAdapter(contentValuesAdapter);
	}
	
	public static Gson createGsonWithContentValuesAdapter(Class<?> clazz) {
		return createGsonWithContentValuesAdapter(new ContentValuesAdapter(clazz));
	}
	
	public static Gson createGsonWithContentValuesAdapter(ContentValuesAdapter contentValuesAdaper) {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeHierarchyAdapter(ContentValues.class, contentValuesAdaper);
		return gsonBuilder.create();
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
	

	public ContentValuesAdapter getContentValuesAdapter() {
		return contentValuesAdapter;
	}

	public void setContentValuesAdapter(ContentValuesAdapter contentValuesAdaper) {
		this.contentValuesAdapter = contentValuesAdaper;
	}
	
	public Class<?> getClazz() {
		return clazz;
	}

	public Gson getGson() {
		return gson;
	}
	
	
}
