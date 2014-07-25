package by.istin.android.xcore.processor.impl;

import android.content.ContentValues;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

import by.istin.android.xcore.gson.AbstractValuesAdapter;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.IDataSource;
import by.istin.android.xcore.utils.IOUtils;
import by.istin.android.xcore.utils.ReflectUtils;

public abstract class AbstractGsonProcessor<Result> extends AbstractGsonDBProcessor<Result, InputStream>{

	private final Class<?> clazz;
	
	private final Class<? extends Result> resultClassName;
	
	private final Gson gson;
	
	private AbstractValuesAdapter contentValuesAdapter;

    public AbstractGsonProcessor(Class<? extends Result> resultClassName) {
        this(null, resultClassName, null);
    }

    public AbstractGsonProcessor(Class<?> clazz, Class<? extends Result> resultClassName) {
        this(clazz, resultClassName, new AbstractValuesAdapter<ContentValues>(clazz) {

            @Override
            protected void proceedSubEntities(Type type, JsonDeserializationContext jsonDeserializationContext, ContentValues contentValues, ReflectUtils.XField field, String fieldValue, JsonArray jsonArray) {

            }

            @Override
            protected void proceedSubEntity(Type type, JsonDeserializationContext jsonDeserializationContext, ContentValues contentValues, ReflectUtils.XField field, String fieldValue, Class<?> clazz, JsonObject subEntityJsonObject) {

            }

            @Override
            protected ContentValues proceed(ContentValues parent, int position, ContentValues contentValues) {
                return contentValues;
            }
        });
    }

	public AbstractGsonProcessor(Class<?> clazz, Class<? extends Result> resultClassName, AbstractValuesAdapter contentValuesAdapter) {
		super();
		this.clazz = clazz;
		this.resultClassName = resultClassName;
        if (clazz == null || contentValuesAdapter == null) {
            gson = new GsonBuilder().create();
        } else {
		    this.contentValuesAdapter = contentValuesAdapter;
		    gson = createGsonWithContentValuesAdapter(getListBufferSize(), contentValuesAdapter);
        }
	}


	@Override
	public Result execute(DataSourceRequest dataSourceRequest, IDataSource<InputStream> dataSource, InputStream inputStream) throws Exception {
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader, 8192);
		try {
			return process(dataSourceRequest, getGson(), bufferedReader);
		} catch (JsonIOException exception){
            throw new IOException(exception);
        } finally {
			IOUtils.close(inputStream);
			IOUtils.close(inputStreamReader);
			IOUtils.close(bufferedReader);
		}
	}
	
	protected Result process(DataSourceRequest dataSourceRequest,Gson gson, BufferedReader bufferedReader) {
		return getGson().fromJson(bufferedReader, resultClassName);
	}
	

	public AbstractValuesAdapter getContentValuesAdapter() {
		return contentValuesAdapter;
	}

	public void setContentValuesAdapter(AbstractValuesAdapter contentValuesAdaper) {
		this.contentValuesAdapter = contentValuesAdaper;
	}
	
	public Class<?> getClazz() {
		return clazz;
	}

	public Gson getGson() {
		return gson;
	}

    protected int getListBufferSize() {
        return -1;
    }
}
