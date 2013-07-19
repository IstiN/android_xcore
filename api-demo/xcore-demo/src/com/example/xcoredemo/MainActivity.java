package com.example.xcoredemo;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.widget.Toast;

import com.astuetz.viewpager.extensions.PagerSlidingTabStrip;
import com.example.xcoredemo.fragment.TestListFragment;

public class MainActivity extends FragmentActivity {

	private PagerSlidingTabStrip mTabs;
	private ViewPager mPager;
	private PagerAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mTabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		mPager = (ViewPager) findViewById(R.id.pager);
		mAdapter = new PagerAdapter(getSupportFragmentManager());
		mPager.setAdapter(mAdapter);
		mTabs.setViewPager(mPager);
//		Toast.makeText(this,
//				Environment.getExternalStorageDirectory().getAbsolutePath(),
//				Toast.LENGTH_LONG).show();
//		try {
//			FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/TestFile");
//			fos.write("This is text from TestFile".getBytes());
//			fos.close();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public class PagerAdapter extends FragmentStatePagerAdapter {

		private final String[] TITLES = { "Source", "Processor", "Fragment" };

		public PagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return TITLES[position];

		}

		@Override
		public Fragment getItem(int position) {
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
			if (id == null) {
				return null;
			} else {
				Bundle args = new Bundle();
				args.putInt(TestListFragment.EXTRA_KEY_ARRAY_ID, id);
				TestListFragment frag = new TestListFragment();
				frag.setArguments(args);
				return frag;
			}
		}

		@Override
		public int getCount() {
			return TITLES.length;
		}

	}

}
