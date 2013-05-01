package by.istin.android.xcore.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class ReflectUtils {

	public static List<Field> getEntityKeys(Class<?> clazz) {
		Field[] fields = clazz.getFields();
		List<Field> keys = null;
		for (Field field : fields) {
			Annotation[] annotations = field.getAnnotations();
			if (field.getType().equals(String.class) && Modifier.isStatic(field.getModifiers()) && annotations != null && annotations.length != 0) {
				if (keys == null) {
					keys = new ArrayList<Field>();
				}
				keys.add(field);
			}
		}
		return keys;
	}
	
	public static String getStaticStringValue(Field field) {
		try {
			field.setAccessible(true);
			String fieldValue = (String)field.get(null);
			field.setAccessible(false);
			return fieldValue;
		} catch (IllegalArgumentException e) {
			throw e;
		} catch (IllegalAccessException e) {
			//ignored
			return null;
		}
	}
	
	public static <T> T getInstanceInterface(Class<?> clazz, Class<T> interfaceTargetClazz) {
		try {
			Class<?>[] interfaces = clazz.getInterfaces();
			for (Class<?> interfaceClazz: interfaces) {
				if (interfaceClazz.equals(interfaceTargetClazz)) {
					return (T)clazz.newInstance();
				}
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}
	
}
