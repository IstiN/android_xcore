package by.istin.android.xcore.test.fragment;

import android.content.Intent;
import android.test.ActivityUnitTestCase;

public class TestXListFragment extends ActivityUnitTestCase<XListFragmentActivity>{
	
	public TestXListFragment(Class<XListFragmentActivity> activityClass) {
		super(activityClass);
	}
	
	public TestXListFragment() {
		super(XListFragmentActivity.class);
	}

	private XListFragmentActivity mActivity;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		startActivity(new Intent(getInstrumentation().getTargetContext(), XListFragmentActivity.class), null, null);
		mActivity = getActivity();
		
	}
	
	public void testActivity() throws Exception{
		assertTrue(mActivity == null);
	}
	
	
	
	

}
