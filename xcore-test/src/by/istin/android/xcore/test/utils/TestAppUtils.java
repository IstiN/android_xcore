package by.istin.android.xcore.test.utils;

import android.test.ApplicationTestCase;
import by.istin.android.xcore.CoreApplication;
import by.istin.android.xcore.utils.AppUtils;

public class TestAppUtils extends ApplicationTestCase<CoreApplication>{

	public TestAppUtils() {
		super(CoreApplication.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		createApplication();
		super.setUp();
	}
	
	public void testContextNull(){
		try{
			AppUtils.get(null, "test");
		}
		catch(Exception e){
			assertTrue(e instanceof IllegalArgumentException);
		}
	}
	
	public void testNameNull(){
		try{
			AppUtils.get(getApplication(), "test");
		}
		catch(Exception e){
			assertTrue(e instanceof IllegalArgumentException);
		}
	}

}
