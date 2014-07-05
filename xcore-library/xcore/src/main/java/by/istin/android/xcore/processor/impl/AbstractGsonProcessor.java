package by.istin.android.xcore.processor.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import by.istin.android.xcore.gson.ContentValuesAdapter;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.IDataSource;
import by.istin.android.xcore.utils.IOUtils;

public abstract class AbstractGsonProcessor<Result> extends AbstractGsonDBProcessor<Result, InputStream>{

	private final Class<?> clazz;
	
	private final Class<? extends Result> resultClassName;
	
	private final Gson gson;
	
	private ContentValuesAdapter contentValuesAdapter;

    public AbstractGsonProcessor(Class<? extends Result> resultClassName) {
        this(null, resultClassName, null);
    }

    public AbstractGsonProcessor(Class<?> clazz, Class<? extends Result> resultClassName) {
        this(clazz, resultClassName, new ContentValuesAdapter(clazz));
    }
	public AbstractGsonProcessor(Class<?> clazz, Class<? extends Result> resultClassName, ContentValuesAdapter contentValuesAdapter) {
		super();
		this.clazz = clazz;
		this.resultClassName = resultClassName;
        if (clazz == null) {
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
			return process(getGson(), bufferedReader);	
		} catch (JsonIOException exception){
            throw new IOException(exception);
        } finally {
			IOUtils.close(inputStream);
			IOUtils.close(inputStreamReader);
			IOUtils.close(bufferedReader);
		}
	}
	
	protected Result process(Gson gson, BufferedReader bufferedReader) {
		return getGson().fromJson(bufferedReader, resultClassName);
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

    protected int getListBufferSize() {
        return -1;
    }
}
