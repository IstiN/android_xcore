package by.istin.android.xcore.processor.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import by.istin.android.xcore.gson.ContentValuesAdapter;
import by.istin.android.xcore.processor.IProcessor;
import by.istin.android.xcore.provider.ModelContract;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.IDataSource;
import by.istin.android.xcore.utils.IOUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class AbstractGsonProcessor<Result> implements IProcessor<Result, InputStream>{

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

    public static void clearEntity(Context context, DataSourceRequest dataSourceRequest, Class<?> clazz) {
        clearEntity(context, dataSourceRequest, clazz, false);
    }

    public static void clearEntity(Context context, DataSourceRequest dataSourceRequest, Class<?> clazz, boolean withNotify) {
        clearEntity(context, dataSourceRequest, clazz, null, null, withNotify);
    }

    public static void clearEntity(Context context, DataSourceRequest dataSourceRequest, Class<?> clazz, String selection, String[] selectionArgs, boolean withNotify) {
        Uri deleteUrl = null;
        if (withNotify) {
            deleteUrl = ModelContract.getUri(clazz);
        } else {
            deleteUrl = new ModelContract.UriBuilder(clazz).notNotifyChanges().build();;
        }
        context.getContentResolver().delete(ModelContract.getUri(dataSourceRequest, deleteUrl), selection, selectionArgs);
    }

    public static void bulkInsert(Context context, DataSourceRequest dataSourceRequest, Class<?> clazz, ContentValues[] result) {
        bulkInsert(context, dataSourceRequest, clazz, result, true);
    }
    public static void bulkInsert(Context context, DataSourceRequest dataSourceRequest, Class<?> clazz, ContentValues[] result, boolean withNotify) {
        Uri uri = null;
        if (withNotify) {
            uri = ModelContract.getUri(dataSourceRequest, clazz);
        } else {
            uri = new ModelContract.UriBuilder(ModelContract.getUri(dataSourceRequest, clazz)).notNotifyChanges().build();
        }

        int rows = context.getContentResolver().bulkInsert(uri, result);
        if (rows == 0) {
            context.getContentResolver().notifyChange(ModelContract.getUri(clazz), null);
        }
    }
	
	
}
