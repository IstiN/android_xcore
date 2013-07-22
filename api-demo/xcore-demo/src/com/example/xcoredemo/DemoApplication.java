package com.example.xcoredemo;

import by.istin.android.xcore.CoreApplication;
import by.istin.android.xcore.processor.impl.GsonArrayContentValuesProcessor;
import by.istin.android.xcore.source.impl.http.HttpAndroidDataSource;

import com.example.xcoredemo.test.bo.TestEntity;

public class DemoApplication extends CoreApplication {
	@Override
	public void onCreate() {
		super.onCreate();
		registerAppService(new GsonArrayContentValuesProcessor(TestEntity.class));
		registerAppService(new HttpAndroidDataSource());
	}
}
