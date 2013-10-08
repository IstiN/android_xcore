package com.example.xcoredemo.test;

import android.database.Cursor;
import android.net.Uri;
import android.view.View;
import by.istin.android.xcore.fragment.XListFragment;
import by.istin.android.xcore.processor.impl.GsonArrayContentValuesProcessor;
import by.istin.android.xcore.provider.ModelContract;
import by.istin.android.xcore.source.DataSourceRequest;
import com.example.xcoredemo.R;
import com.example.xcoredemo.test.bo.TestEntity;

public class DemoXListFragmentJoinedRequest extends XListFragment {

	@Override
	public void onListItemClick(Cursor cursor, View v, int position, long id) {

	}

	@Override
	public int getViewLayout() {
		return R.layout.fragment_demox;
	}

	@Override
	public Uri getUri() {
		return ModelContract.getUri(TestEntity.class);
	}

	@Override
	public String getUrl() {
		return "https://dl.dropboxusercontent.com/s/3c0n0ijpjjorqih/jsonarray";
	}

	@Override
	public String getProcessorKey() {
		return new GsonArrayContentValuesProcessor(TestEntity.class)
				.getAppServiceKey();
	}

	@Override
	protected String[] getAdapterColumns() {
		return new String[] { TestEntity.STRING_VALUE };
	}

	@Override
	protected int[] getAdapterControlIds() {
		return new int[] { R.id.tv_id };
	}

	@Override
	protected int getAdapterLayout() {
		return R.layout.adapter_demox;
	}

    @Override
    public DataSourceRequest createDataSourceRequest(String url, Boolean isForceUpdate, String parentRequestUri) {
        DataSourceRequest dataSourceRequest = super.createDataSourceRequest(url, isForceUpdate, parentRequestUri);

        String requestDataUri1 = "https://dl.dropboxusercontent.com/s/slnep5t0p6yvjr1/jsonarray1";
        String requestDataUri2 = "https://dl.dropboxusercontent.com/s/xn8f9rw9034da2u/jsonarray2";

        DataSourceRequest joinedDataSourceRequestPage1 = createDataSourceRequest(isForceUpdate, requestDataUri1);

        DataSourceRequest joinedDataSourceRequestPage2 = createDataSourceRequest(isForceUpdate, requestDataUri2);

        joinedDataSourceRequestPage1.joinRequest(joinedDataSourceRequestPage2, getProcessorKey(), getDataSourceKey());
        dataSourceRequest.joinRequest(joinedDataSourceRequestPage1, getProcessorKey(), getDataSourceKey());
        return dataSourceRequest;
    }

    private DataSourceRequest createDataSourceRequest(Boolean isForceUpdate, String requestDataUri) {
        DataSourceRequest joinedDataSourceRequest = new DataSourceRequest(requestDataUri);
        joinedDataSourceRequest.setCacheable(isCacheable());
        joinedDataSourceRequest.setCacheExpiration(getCacheExpiration());
        joinedDataSourceRequest.setForceUpdateData(isForceUpdate);
        return joinedDataSourceRequest;
    }
}
