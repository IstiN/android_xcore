package com.example.xcoredemo.test;

import java.io.InputStream;

import android.content.ContentValues;
import by.istin.android.xcore.processor.impl.GsonContentValuesProcessor;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.impl.http.HttpAndroidDataSource;

import com.example.xcoredemo.test.bo.TestEntity;

public class GsonContentValuesProcessorTest implements IDemoTest {

	@Override
	public String test() {
		try {
			HttpAndroidDataSource httpAndroidDataSource = new HttpAndroidDataSource();
			DataSourceRequest dataSourceRequest = new DataSourceRequest(
					"https://dl.dropboxusercontent.com/u/16403954/xcore/json_object.json");
			InputStream inputStream = httpAndroidDataSource
					.getSource(dataSourceRequest);
			ContentValues contentValues = new GsonContentValuesProcessor(
					TestEntity.class).execute(dataSourceRequest,
					httpAndroidDataSource, inputStream);
			inputStream.close();
			return contentValues.toString();
		} catch (Exception e) {
			return null;
		}
	}
}
