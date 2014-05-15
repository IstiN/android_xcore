package by.istin.android.xcore.test.utils;

import java.io.Serializable;

import org.apache.http.entity.SerializableEntity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.test.AndroidTestCase;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.widget.BaseAdapter;
import by.istin.android.xcore.utils.BytesUtils;

public class TestByteUtils extends AndroidTestCase {

	public void testContentValues() {
		ContentValues values = new ContentValues();
		values.put("bn", "bmmm");
		values.put("jk", 6);
		ContentValues values2 = new ContentValues(values);
		assertTrue(values.equals(BytesUtils
				.contentValuesFromByteArray(BytesUtils.toByteArray(values2))));
	}

	public void testBundle() {
		Bundle b = new Bundle();
		b.putString("1", "test");
		b.putFloat("2", (float) 1.57);
		b = BytesUtils.bundleFromByteArray(BytesUtils.toByteArray(b));
		assertTrue(b.getString("1").equals("test")
				&& b.getFloat("2") == (float) 1.57);
	}

	public void testIntent() {
		Intent intent = new Intent("ACTION_TEST");
		intent.putExtra("test", "532");
		intent = BytesUtils.intentFromByteArray(BytesUtils.toByteArray(intent));
		assertTrue(intent.getAction().equals("ACTION_TEST")
				&& intent.getStringExtra("test").equals("532"));
	}

	public void testSpanned() {
		Spanned spanned = new SpannedString("123");
		assertTrue(BytesUtils
				.spannedFromByteArray(BytesUtils.toByteArray(spanned))
				.toString().equals("123"));
	}

	public void testArrayContentValues() {
		ContentValues v1 = new ContentValues();
		v1.put("column1", "polll");
		ContentValues v2 = new ContentValues();
		v2.put("zxczxc", "bbbbbbb");
		ContentValues[] values = new ContentValues[] { v1, v2 };
		values = BytesUtils.arrayContentValuesFromByteArray(BytesUtils
				.arrayToByteArray(values));
		assertTrue(values.length == 2
				&& values[0].getAsString("column1").equals("polll")
				&& values[1].getAsString("zxczxc").equals("bbbbbbb"));
	}

}
