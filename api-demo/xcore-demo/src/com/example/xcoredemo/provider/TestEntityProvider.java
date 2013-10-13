package com.example.xcoredemo.provider;

import by.istin.android.xcore.provider.ModelContentProvider;

import com.example.xcoredemo.test.bo.Tag;
import com.example.xcoredemo.test.bo.TestEntity;

public class TestEntityProvider extends ModelContentProvider {

	@Override
	public Class<?>[] getDbEntities() {
		return new Class[]{TestEntity.class, Tag.class};
	}

}
