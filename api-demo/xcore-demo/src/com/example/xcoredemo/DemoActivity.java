package com.example.xcoredemo;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xcoredemo.test.IDemoTest;

public class DemoActivity extends Activity {
	private static final String NOTHING_TO_RUN = "Nothing to run";
	public static final String MISTAKE = "This test doesn't exist, probably it was loaded by mistake. Sorry";
	public static final String EXTRA_TEST_NAME = "EXTRA_TEST_NAME";
	public static final String CLASS_NAME_TEST_PREFIX = "com.example.xcoredemo.test.";
	public static final String CLASS_NAME_TEST_POSTFIX = "Test";
	public static final String CODE_PATH_PREFIX = "com/example/xcoredemo/test/code/";
	public static final String CODE_PATH_POSTFIX = "Code";

	private IDemoTest mDemo;

	private TextView mTextViewCode;
	private TextView mTextViewResult;
	private ProgressBar mProgressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_demo);
		mTextViewCode = (TextView) findViewById(R.id.tv_demo_code);
		mTextViewResult = (TextView) findViewById(R.id.tv_demo_result);
		mProgressBar = (ProgressBar) findViewById(R.id.pb_demo);
		String name = getIntent().getStringExtra(EXTRA_TEST_NAME);
		try {
			@SuppressWarnings("rawtypes")
			Class c = Class.forName(CLASS_NAME_TEST_PREFIX + name
					+ CLASS_NAME_TEST_POSTFIX);
			mDemo = (IDemoTest) c.newInstance();
		} catch (Exception e) {
			mDemo = null;
		}
		InputStream is = getClassLoader().getResourceAsStream(
				CODE_PATH_PREFIX + name + CODE_PATH_POSTFIX);

		if (is != null) {
			String code;
			try {
				byte[] buffer = new byte[is.available()];
				is.read(buffer);
				code = new String(buffer);
			} catch (IOException e) {
				code = null;
			} finally {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
			mTextViewCode.setText(code);
		} else {
			mTextViewCode.setText(MISTAKE);
		}
	}

	public void run(View v) {
		if (mDemo != null) {
			new DemoTask().execute(mDemo);
		} else {
			Toast.makeText(this, NOTHING_TO_RUN, Toast.LENGTH_SHORT).show();
		}
	}

	private class DemoTask extends AsyncTask<IDemoTest, Void, String> {

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			mProgressBar.setVisibility(View.GONE);
			mTextViewResult.setText(result);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(IDemoTest... params) {
			return params[0].test();
		}

	}

}
