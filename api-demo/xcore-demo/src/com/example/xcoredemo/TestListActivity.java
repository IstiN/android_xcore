package com.example.xcoredemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TestListActivity extends Activity implements OnItemClickListener {
	public static final String EXTRA_KEY_ARRAY_ID = "EXTRA_KEY_ARRAY_ID";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_list);
		ListView list = (ListView) findViewById(R.id.test_lv);
		list.setAdapter(new ArrayAdapter<String>(this, R.layout.adapter_test_list,
				R.id.tv_test_name, getResources().getStringArray(
						getIntent().getIntExtra(EXTRA_KEY_ARRAY_ID, 0))));
		list.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long l) {
			Intent intent = new Intent(this, DemoActivity.class);
			intent.putExtra(DemoActivity.EXTRA_TEST_NAME, (String) adapter.getItemAtPosition(position));
			startActivity(intent);
	}
	
	

}
