/**
 * 
 */
package by.istin.android.xcore.test.utils;

import android.content.ContentValues;
import android.test.AndroidTestCase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import by.istin.android.xcore.annotations.dbSubEntity;
import by.istin.android.xcore.db.entity.IBeforeArrayUpdate;
import by.istin.android.xcore.db.entity.IMerge;
import by.istin.android.xcore.db.impl.DBHelper;
import by.istin.android.xcore.model.BigTestEntity;
import by.istin.android.xcore.utils.BytesUtils;
import by.istin.android.xcore.utils.ReflectUtils;

/**
 * @author Uladzimir_Klyshevich
 *
 */
public class TestReflectUtils extends AndroidTestCase {

	public void testKeysFields() throws Exception {
		List<ReflectUtils.XField> entityKeys = ReflectUtils.getEntityKeys(BigTestEntity.class);
		assertEquals(9, entityKeys.size());
	}
	
	public void testInterfaceInstance() throws Exception {
		IMerge merge = ReflectUtils.getInstanceInterface(BigTestEntity.class, IMerge.class);
		assertNotNull(merge);
		IBeforeArrayUpdate beforeListUpdate = ReflectUtils.getInstanceInterface(BigTestEntity.class, IBeforeArrayUpdate.class);
		assertNotNull(beforeListUpdate);
	}
	
	public void testContentValueByteConvertation() throws Exception {
		ContentValues values = new ContentValues();
		values.put("key1", true);
		values.put("key2", "value");
		byte[] byteArray = BytesUtils.toByteArray(values);
		ContentValues createFromParcel = BytesUtils.contentValuesFromByteArray(byteArray);
		assertTrue(createFromParcel.getAsBoolean("key1") && true);
		assertEquals(createFromParcel.getAsString("key2"), "value");
		
		ContentValues[] contentValues = new ContentValues[2];
		contentValues[0] = values;
		values = new ContentValues();
		values.put("key3", false);
		values.put("key4", "val2");
		contentValues[1] = values;
		byteArray = BytesUtils.arrayToByteArray(contentValues);
		contentValues = BytesUtils.arrayContentValuesFromByteArray(byteArray);
		assertEquals(contentValues.length, 2);
	}

    public static class TestClass {

        //NEED TO MERGE WITH EXISTING ENTITY
        @dbSubEntity(key = "test_sub1", mergeWithParent = true)
        public static class TestSubClass1 {

        }

        //NEED TO CREATE NEW ENTITY
        @dbSubEntity(key = "test_sub2", mergeWithParent = false)
        public static class TestSubClass2 {

            public static final String VALUE = "value";

            public static final String PARENT_ID = DBHelper.getForeignKey(TestClass.class);

            public static class TestSubInternalClass {

            }
        }

    }

    public void testGetSubClasses() throws Exception {
        Class<?>[] subClasses = ReflectUtils.getSubClasses(TestClass.class);
        ArrayList<Class<?>> list = new ArrayList<Class<?>>();
        Collections.addAll(list, subClasses);
        assertTrue(list.contains(TestClass.TestSubClass1.class));
        assertTrue(list.contains(TestClass.TestSubClass2.class));

        subClasses = ReflectUtils.getSubClasses(TestClass.TestSubClass2.class);
        list = new ArrayList<Class<?>>();
        Collections.addAll(list, subClasses);
        assertTrue(list.contains(TestClass.TestSubClass2.TestSubInternalClass.class));
    }
}
