package by.istin.android.xcore.test;

import by.istin.android.xcore.utils.StringUtil;
import android.test.AndroidTestCase;

public class TestStringUtils extends AndroidTestCase {

	private static final String SQL = "SELECT SOMETHING + 1 FROM SOMETHING";
	
	
	public void testPluses() throws Exception {
		String value = StringUtil.encode(SQL);
		String result = StringUtil.decode(value);
		assertEquals(result, SQL);
	}
}
