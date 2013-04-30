package by.istin.android.xcore.processor.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.ContentValues;
import by.istin.android.xcore.gson.ContentValuesAdaper;
import by.istin.android.xcore.processor.IProcessor;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.IDataSource;

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
		return gson.fromJson(new BufferedReader(new InputStreamReader(inputStream), 8192), ContentValues[].class);
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

}