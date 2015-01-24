package by.istin.android.xcore.test.utils;

import android.test.ApplicationTestCase;

import by.istin.android.xcore.ContextHolder;
import by.istin.android.xcore.CoreApplication;
import by.istin.android.xcore.XCoreHelper;
import by.istin.android.xcore.XCoreHelper.IAppServiceKey;
import by.istin.android.xcore.utils.AppUtils;

public class TestAppUtils extends ApplicationTestCase<CoreApplication> {

    public TestAppUtils() {
        super(CoreApplication.class);
    }

    @Override
    protected void setUp() throws Exception {
        createApplication();
        super.setUp();
    }

    public void testContextNull() {
        try {
            AppUtils.get(null, "test");
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }
    }

    public void testNameNull() {
        try {
            AppUtils.get(ContextHolder.get(), null);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }
    }

    public void testWrongKey() {
        try {
            AppUtils.get(ContextHolder.get(), "notExistingAppKeyUIUIUIUIUIIUIUIUI");
        } catch (Exception e) {
            assertTrue(e instanceof IllegalStateException);
        }
    }

    public void testRightKey() {
        XCoreHelper.get(ContextHolder.get()).registerAppService(new TestAppService());
        Object service = AppUtils.get(ContextHolder.get(), "TestAppService");
        assertTrue(service instanceof TestAppService);
    }

    private class TestAppService implements IAppServiceKey {

        @Override
        public String getAppServiceKey() {
            return "TestAppService";
        }

    }

}
