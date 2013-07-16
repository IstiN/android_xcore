package com.example.xcoredemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;

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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public class PagerAdapter extends FragmentStatePagerAdapter {

		private final String[] TITLES = { "Source", "Processor" };

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
