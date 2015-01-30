package by.istin.android.xcore.test.utils;

import java.io.IOException;
import java.io.InputStream;

import by.istin.android.xcore.utils.IOUtils;

import android.app.Application;
import android.content.res.AssetManager;
import android.test.ApplicationTestCase;

public class TestIOUtils extends ApplicationTestCase<Application> {

    public TestIOUtils() {
        super(Application.class);
    }

    @Override
    protected void setUp() throws Exception {
        createApplication();
        super.setUp();
    }

    public void testClose() {
        AssetManager am = getContext().getAssets();
        InputStream is = null;
        try {
            is = am.open("test.txt");
            assertTrue(is.available() > 0);
        } catch (IOException e) {
            IOUtils.close(is);
        }
        IOUtils.close(is);
        assertNull(is);
    }
}
