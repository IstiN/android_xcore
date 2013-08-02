package com.example.xcoredemo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.xcoredemo.DemoActivity;
import com.example.xcoredemo.R;

public class TestListFragment extends ListFragment {
	public static final String EXTRA_KEY_ARRAY_ID = "EXTRA_KEY_ARRAY_ID";
	

	String[] names;
	ArrayAdapter<String> mAdapter;

	public TestListFragment() {
		super();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (getArguments() != null) {
			names = getResources().getStringArray(
					getArguments().getInt(EXTRA_KEY_ARRAY_ID));
		}
		mAdapter = new ArrayAdapter<String>(getActivity(),
				R.layout.adapter_test_list, R.id.tv_test_name, names);
		setListAdapter(mAdapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		if (position > -1 && position < names.length && names[position] != null){
				Intent intent = new Intent(getActivity(), DemoActivity.class);
				intent.putExtra(DemoActivity.EXTRA_TEST_NAME, names[position]);
				startActivity(intent);
		}
	}

}
