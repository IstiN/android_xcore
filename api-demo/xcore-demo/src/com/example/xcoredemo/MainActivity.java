package com.example.xcoredemo;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends FragmentActivity implements OnItemClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ListView list = (ListView) findViewById(R.id.main_lv);
		list.setAdapter(new ArrayAdapter<String>(this, R.layout.adapter_test_list,
				R.id.tv_test_name, getResources().getStringArray(
						R.array.categories)));
		list.setOnItemClickListener(this);
		try {
			FileOutputStream fos = new FileOutputStream(Environment
					.getExternalStorageDirectory().getAbsolutePath()
					+ "/TestFile");
			fos.write("This is text from TestFile".getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long l) {
		Integer id;
		switch (position) {
		case 0:
			id = R.array.sources;
			break;
		case 1:
			id = R.array.processors;
			break;
		case 2:
			id = R.array.fragments;
			break;
		default:
			id = null;
			break;
		}
		if (id != null) {
			Intent intent = new Intent(this, TestListActivity.class);
			intent.putExtra(TestListActivity.EXTRA_KEY_ARRAY_ID, id);
			startActivity(intent);
		}
	}

}
