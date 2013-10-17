package com.example.xcoredemo.provider;

import by.istin.android.xcore.provider.DBContentProvider;
import com.example.xcoredemo.test.bo.Tag;
import com.example.xcoredemo.test.bo.TestEntity;

public class TestEntityProvider extends DBContentProvider {

	@Override
	public Class<?>[] getEntities() {
		return new Class[]{TestEntity.class, Tag.class};
	}

}
