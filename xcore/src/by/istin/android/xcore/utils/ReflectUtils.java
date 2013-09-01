package by.istin.android.xcore.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ReflectUtils {

    private static Map<Class<?>, List<Field>> sFieldsOfClass = new ConcurrentHashMap<Class<?>, List<Field>>();

    private static Map<Field, String> sNameOfField = new ConcurrentHashMap<Field, String>();

    private static Map<Field, Set<Class<? extends Annotation>>> sAnnotationsOfField = new ConcurrentHashMap<Field, Set<Class<? extends Annotation>>>();

	public static List<Field> getEntityKeys(Class<?> clazz) {
        if (sFieldsOfClass.containsKey(clazz)) {
            return sFieldsOfClass.get(clazz);
        }
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
        sFieldsOfClass.put(clazz, keys);
		return keys;
	}

    public static boolean isAnnotationPresent(Field field, Class<? extends Annotation> annotationClass) {
        if (sAnnotationsOfField.containsKey(field)) {
            return sAnnotationsOfField.get(field).contains(annotationClass);
        }
        Annotation[] annotations = field.getAnnotations();
        HashSet<Class<? extends Annotation>> hashSet = new HashSet<Class<? extends Annotation>>();
        for (Annotation annotation : annotations) {
            hashSet.add(annotation.annotationType());
        }
        sAnnotationsOfField.put(field, hashSet);
        return hashSet.contains(annotationClass);
    }

	public static String getStaticStringValue(Field field) {
        if (sNameOfField.containsKey(field)) {
            return sNameOfField.get(field);
        }
		try {
			field.setAccessible(true);
			String fieldValue = (String)field.get(null);
			field.setAccessible(false);
            sNameOfField.put(field, fieldValue);
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
            Class<?> cls = clazz;
            while (cls != null) {
                Class<?>[] interfaces = cls.getInterfaces();

                for (Class<?> i : interfaces) {
                    if (i.equals(interfaceTargetClazz)) {
                        return (T)clazz.newInstance();
                    }
                }
                cls = cls.getSuperclass();
            }
			return null;
		} catch (Exception e) {
			return null;
		}
	}



}
