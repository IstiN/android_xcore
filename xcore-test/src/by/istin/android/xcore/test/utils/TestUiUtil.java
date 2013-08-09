package by.istin.android.xcore.test.utils;

import android.test.ApplicationTestCase;
import by.istin.android.xcore.CoreApplication;
import by.istin.android.xcore.utils.Log;
import by.istin.android.xcore.utils.UiUtil;

public class TestUiUtil extends ApplicationTestCase<CoreApplication> {

	public TestUiUtil() {
		super(CoreApplication.class);
	}

	@Override
	protected void setUp() throws Exception {
		createApplication();
		super.setUp();
	}

	public void testGetDisplayWidth() {
		Log.d("TestUiUtil", UiUtil.getDisplayWidth());
		assertTrue(UiUtil.getDisplayWidth() > 0);
	}

	public void testGetDisplayHeight() {
		Log.d("TestUiUtil", UiUtil.getDisplayHeight());
		assertTrue(UiUtil.getDisplayHeight() > 0);
	}

	public void testGetDp() {
		assertTrue(UiUtil.getDp(getContext(), 10.5f) > 10.5 * 0.75
				&& UiUtil.getDp(getContext(), 10.5f) < 10.5 * 3);
		assertTrue(UiUtil.getDp(getContext(), 10.5f) instanceof Float);
		assertTrue(UiUtil.getDp(getContext(), 10) > 10 * 0.75
				&& UiUtil.getDp(getContext(), 10) < 10 * 3);
		assertTrue(UiUtil.getDp(getContext(), 100.5f) > 100.5 * 0.75
				&& UiUtil.getDp(getContext(), 100.5f) < 100.5 * 3);
		assertTrue(UiUtil.getDp(getContext(), 100.5f) instanceof Float);
		assertTrue(UiUtil.getDp(getContext(), 100) > 100 * 0.75
				&& UiUtil.getDp(getContext(), 100) < 100 * 3);
	}

	public void testGetFontSize() {
		assertTrue(UiUtil.getFontSize(getContext(), 10) >= 10
				&& UiUtil.getFontSize(getContext(), 10) < 10 * 3);
		assertTrue(UiUtil.getFontSize(getContext(), 100) > 10
				&& UiUtil.getFontSize(getContext(), 100) < 100 * 3);
	}

	public void testGetPx() {
		assertTrue(UiUtil.getPx(getContext(), 10f) > 0);
		assertTrue(UiUtil.getPx(getContext(), 10.4f) > 0);
		assertTrue(UiUtil.getPx(getContext(), 100f) > 0);
		assertTrue(UiUtil.getPx(getContext(), 100.4f) > 0);
	}

	public void testIsPortrait() {
		Log.d("TestUiUtil", UiUtil.isPortrait(getContext()));
	}

}
