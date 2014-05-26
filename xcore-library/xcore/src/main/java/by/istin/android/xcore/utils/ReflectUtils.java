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

import by.istin.android.xcore.annotations.dbEntities;
import by.istin.android.xcore.annotations.dbEntity;

public class ReflectUtils {

    public static class XField {

        private final Field mField;

        XField(Field field) {
            mField = field;
        }

        public Field getField() {
            return mField;
        }

    }

    private static final Map<Class<?>, List<XField>> sFieldsOfClass = new ConcurrentHashMap<Class<?>, List<XField>>();

    private static final Map<XField, String> sNameOfField = new ConcurrentHashMap<XField, String>();

    private static final Map<String, Object> sInstancesOfInterface = new ConcurrentHashMap<String, Object>();

    private static final Map<XField, Set<Class<? extends Annotation>>> sAnnotationsOfField = new ConcurrentHashMap<XField, Set<Class<? extends Annotation>>>();

    private static final Map<XField, ConcurrentHashMap<Class<? extends Annotation>, Annotation>> sAnnotationsImplOfField = new ConcurrentHashMap<XField, ConcurrentHashMap<Class<? extends Annotation>, Annotation>>();

    public static <T extends Annotation> T getAnnotation(XField field, Class<T> annotationClass) {
        ConcurrentHashMap<Class<? extends Annotation>, Annotation> classAnnotationConcurrentHashMap = sAnnotationsImplOfField.get(field);
        if (classAnnotationConcurrentHashMap == null) {
            classAnnotationConcurrentHashMap = new ConcurrentHashMap<Class<? extends Annotation>, Annotation>();
            sAnnotationsImplOfField.put(field, classAnnotationConcurrentHashMap);
        }
        Annotation annotation = classAnnotationConcurrentHashMap.get(annotationClass);
        if (annotation == null) {
            annotation = field.mField.getAnnotation(annotationClass);
        }
        if (annotation != null) {
            classAnnotationConcurrentHashMap.put(annotationClass, annotation);
        }
        return (T) annotation;
    }

	public static List<XField> getEntityKeys(Class<?> clazz) {
        if (sFieldsOfClass.containsKey(clazz)) {
            return sFieldsOfClass.get(clazz);
        }
		Field[] fields = clazz.getFields();
		List<XField> keys = null;
		for (Field field : fields) {
			Annotation[] annotations = field.getAnnotations();
			if (field.getType().equals(String.class) && Modifier.isStatic(field.getModifiers()) && annotations != null && annotations.length != 0) {
				if (keys == null) {
					keys = new ArrayList<XField>();
				}
                //we need be sure that all sub entities insert after parent
                XField xField = new XField(field);
                if (ReflectUtils.isAnnotationPresent(xField, dbEntity.class) || ReflectUtils.isAnnotationPresent(xField, dbEntities.class)) {
                    keys.add(xField);
                } else {
				    keys.add(0, xField);
                }
			}
		}
        sFieldsOfClass.put(clazz, keys);
		return keys;
	}

    public static boolean isAnnotationPresent(XField field, Class<? extends Annotation> annotationClass) {
        if (sAnnotationsOfField.containsKey(field)) {
            return sAnnotationsOfField.get(field).contains(annotationClass);
        }
        Annotation[] annotations = field.mField.getAnnotations();
        HashSet<Class<? extends Annotation>> hashSet = new HashSet<Class<? extends Annotation>>();
        for (Annotation annotation : annotations) {
            hashSet.add(annotation.annotationType());
        }
        sAnnotationsOfField.put(field, hashSet);
        return hashSet.contains(annotationClass);
    }

	public static String getStaticStringValue(XField field) {
        if (sNameOfField.containsKey(field)) {
            return sNameOfField.get(field);
        }
		try {
			field.mField.setAccessible(true);
			String fieldValue = (String)field.mField.get(null);
            field.mField.setAccessible(false);
            sNameOfField.put(field, fieldValue);
			return fieldValue;
		} catch (IllegalAccessException e) {
			//ignored
			return null;
		}
	}

    public static Class<?> classForName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }
	
	public static <T> T getInstanceInterface(Class<?> clazz, Class<T> interfaceTargetClazz) {
		try {
            String cacheKey = clazz.getName() + interfaceTargetClazz.getName();
            if (sInstancesOfInterface.containsKey(cacheKey)) {
                return (T) sInstancesOfInterface.get(cacheKey);
            }
            Class<?> cls = clazz;
            while (cls != null) {
                Class<?>[] interfaces = cls.getInterfaces();

                for (Class<?> i : interfaces) {
                    if (i.equals(interfaceTargetClazz)) {
                        T object = (T) clazz.newInstance();
                        sInstancesOfInterface.put(cacheKey, object);
                        return object;
                    }
                }
                cls = cls.getSuperclass();
            }
			return null;
		} catch (Exception e) {
			return null;
		}
	}

    public static <T> T newInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
