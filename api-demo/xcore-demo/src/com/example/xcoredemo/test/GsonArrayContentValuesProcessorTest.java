package com.example.xcoredemo.test;

import java.io.InputStream;

import android.content.ContentValues;
import by.istin.android.xcore.processor.impl.GsonArrayContentValuesProcessor;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.impl.http.HttpAndroidDataSource;

import com.example.xcoredemo.test.bo.TestEntity;

public class GsonArrayContentValuesProcessorTest implements IDemoTest {

	@Override
	public String test() {
		try {
			HttpAndroidDataSource httpAndroidDataSource = new HttpAndroidDataSource();
			DataSourceRequest dataSourceRequest = new DataSourceRequest(
					"https://dl.dropboxusercontent.com/u/16403954/xcore/json_array.json");
			InputStream inputStream = httpAndroidDataSource
					.getSource(dataSourceRequest);
			ContentValues[] contentValues = new GsonArrayContentValuesProcessor(
					TestEntity.class).execute(dataSourceRequest,
					httpAndroidDataSource, inputStream);
			inputStream.close();
			StringBuilder result = new StringBuilder();
			for (int i = 0; i<contentValues.length; i++){
				result.append("No " + i + ": ").append(contentValues[i].toString()).append(System.getProperty("line.separator"));
			}
			return result.toString();
		} catch (Exception e) {
			return null;

		}
	}

}
