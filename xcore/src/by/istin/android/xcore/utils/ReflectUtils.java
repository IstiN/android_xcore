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
}
