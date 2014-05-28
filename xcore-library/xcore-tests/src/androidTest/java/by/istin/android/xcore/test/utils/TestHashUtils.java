package by.istin.android.xcore.test.utils;

import android.test.ApplicationTestCase;
import android.util.Log;

import java.util.UUID;

import by.istin.android.xcore.ContextHolder;
import by.istin.android.xcore.CoreApplication;
import by.istin.android.xcore.XCoreHelper;
import by.istin.android.xcore.XCoreHelper.IAppServiceKey;
import by.istin.android.xcore.utils.AppUtils;
import by.istin.android.xcore.utils.HashUtils;

public class TestHashUtils extends ApplicationTestCase<CoreApplication>{

	public TestHashUtils() {
		super(CoreApplication.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		createApplication();
		super.setUp();
	}
	
	public void testOutOfMemory(){
        int maxValue = 1000;
        int prevValue = 0;
        for (int i = 0; i < maxValue; i++) {
            int nextValue = (int) (((double) i / (double) maxValue) * 100);
            if (prevValue != nextValue) {
                Log.d("TestHashUtils", nextValue + "%");
                prevValue = nextValue;
            }
            HashUtils.generateId(UUID.randomUUID().toString(), i);
        }
	}
	

}
