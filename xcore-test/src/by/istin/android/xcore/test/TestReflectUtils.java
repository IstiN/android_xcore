/**
 * 
 */
package by.istin.android.xcore.test;

import java.lang.reflect.Field;
import java.util.List;

import android.test.AndroidTestCase;
import by.istin.android.xcore.db.IBeforeArrayUpdate;
import by.istin.android.xcore.db.IMerge;
import by.istin.android.xcore.test.bo.TestEntity;
import by.istin.android.xcore.utils.ReflectUtils;

/**
 * @author Uladzimir_Klyshevich
 *
 */
public class TestReflectUtils extends AndroidTestCase {

	public void testKeysFields() throws Exception {
		List<Field> entityKeys = ReflectUtils.getEntityKeys(TestEntity.class);
		assertEquals(entityKeys.size(), 7);
	}
	
	public void testInterfaceInstance() throws Exception {
		IMerge merge = ReflectUtils.getInstanceInterface(TestEntity.class, IMerge.class);
		assertNotNull(merge);
		IBeforeArrayUpdate beforeListUpdate = ReflectUtils.getInstanceInterface(TestEntity.class, IBeforeArrayUpdate.class);
		assertNull(beforeListUpdate);
	}
	
}
