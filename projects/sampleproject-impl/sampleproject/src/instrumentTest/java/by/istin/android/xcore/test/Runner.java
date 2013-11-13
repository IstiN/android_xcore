package by.istin.android.xcore.test;

import android.test.InstrumentationTestRunner;

/**
 * Created by IstiN on 3.11.13.
 */
public class Runner extends InstrumentationTestRunner {

    @Override
    public ClassLoader getLoader() {
        return Runner.class.getClassLoader();
    }

}
