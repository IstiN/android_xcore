package com.example.xcoredemo.provider;

import com.example.xcoredemo.test.bo.TestEntity;

import by.istin.android.xcore.provider.ModelContentProvider;
import by.istin.android.xcore.source.DataSourceRequestEntity;

public class TestEntityProvider extends ModelContentProvider {

	@Override
	public Class<?>[] getDbEntities() {
		return new Class[]{TestEntity.class, DataSourceRequestEntity.class};
	}

}
