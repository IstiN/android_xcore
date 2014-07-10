package by.istin.android.xcore.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import by.istin.android.xcore.annotations.Config;
import by.istin.android.xcore.annotations.dbEntities;
import by.istin.android.xcore.annotations.dbEntity;

public class ReflectUtils {

    private static final Object STUB = new Object();

    public static Config getClassConfig(Class<?> clazz) {
        return getXClass(clazz).getConfig();
    }

    private static class XClass {

        private Class<?> clazz;

        private Object instance;

        private Config config;

        private XClass(Class<?> clazz) {
            this.clazz = clazz;
            dbEntity annotation = this.clazz.getAnnotation(dbEntity.class);
            if (annotation != null) {
                config = annotation.value();
            }
        }

        public Config getConfig() {
            return config;
        }

        private final Object lock = new Object();

        private List<XField> listFields;

        private final Map<XClass, Holder> instancesOfInterface = new ConcurrentHashMap<XClass, Holder>();

        public List<XField> getFields() {
            if (listFields == null) {
                synchronized (lock) {
                    if (listFields == null) {
                        listFields = new ArrayList<XField>();
                        Field[] fields = clazz.getFields();
                        for (Field field : fields) {
                            Annotation[] annotations = field.getAnnotations();
                            if (field.getType().equals(String.class) && Modifier.isStatic(field.getModifiers()) && annotations != null && annotations.length != 0) {
                                //we need be sure that all sub entities insert after parent
                                XField xField = new XField(field);
                                if (ReflectUtils.isAnnotationPresent(xField, dbEntity.class) || ReflectUtils.isAnnotationPresent(xField, dbEntities.class)) {
                                    listFields.add(xField);
                                } else {
                                    listFields.add(0, xField);
                                }
                            }
                        }
                    }
                }
            }
            return listFields;
        }

        public <T> T getInstanceInterface(Class<T> interfaceTargetClazz) {
            try {
                XClass xInterfaceClass = getXClass(interfaceTargetClazz);
                Holder<T> result = (Holder<T>) instancesOfInterface.get(xInterfaceClass);
                if (result == null) {
                    synchronized (lock) {
                        result = (Holder<T>) instancesOfInterface.get(xInterfaceClass);
                        if (result == null && !instancesOfInterface.containsKey(xInterfaceClass)) {
                            Class<?> cls = clazz;
                            while (cls != null) {
                                Class<?>[] interfaces = cls.getInterfaces();
                                for (Class<?> i : interfaces) {
                                    if (i.equals(interfaceTargetClazz)) {
                                        T object = (T) clazz.newInstance();
                                        instancesOfInterface.put(xInterfaceClass, new Holder<T>(object));
                                        return object;
                                    }
                                }
                                cls = cls.getSuperclass();
                            }
                        }
                        result = new Holder<T>();
                        instancesOfInterface.put(xInterfaceClass, result);
                    }
                }
                return result.get();
            } catch (Exception e) {
                return null;
            }
        }

        public <T> T getInstance() {
            if (instance == null) {
                instance = ReflectUtils.newInstance(clazz);
            }
            return (T) instance;
        }
    }

    public static class XField {

        public static final String DB_ANNOTATION_PREFIX = "by.istin.android.xcore.annotations.db";
        private final Field mField;

        private final String mNameOfField;

        private final HashSet<Class<? extends Annotation>> mAnnotations;

        private final Map<Class<? extends Annotation>, Annotation> mClassAnnotationHashMap;

        private Config mConfig;

        XField(Field field) {
            mField = field;

            //init name of field
            mField.setAccessible(true);
            try {
                mNameOfField = (String)mField.get(null);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException(e);
            }
            mField.setAccessible(false);

            //init annotations
            Annotation[] annotations = mField.getAnnotations();
            if (annotations != null) {
                mAnnotations = new HashSet<Class<? extends Annotation>>(annotations.length);
                mClassAnnotationHashMap = new ConcurrentHashMap<Class<? extends Annotation>, Annotation>(annotations.length);
                for (Annotation annotation : annotations) {
                    Class<? extends Annotation> annotationType = annotation.annotationType();
                    mAnnotations.add(annotationType);
                    mClassAnnotationHashMap.put(annotationType, annotation);
                    String name = annotationType.getName();
                    if (name.startsWith(DB_ANNOTATION_PREFIX)) {
                        try {
                            Method method = annotation.getClass().getMethod("value");
                            mConfig = (Config) method.invoke(annotation);
                        } catch (Exception e) {
                            throw new IllegalArgumentException(e);
                        }
                    }
                }
            } else {
                mAnnotations = new HashSet<Class<? extends Annotation>>(0);
                mClassAnnotationHashMap = new ConcurrentHashMap<Class<? extends Annotation>, Annotation>(0);
            }

        }

        public Field getField() {
            return mField;
        }

        public String getNameOfField() {
            return mNameOfField;
        }

        public Config getConfig() {
            return mConfig;
        }

        public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
            return mAnnotations.contains(annotationClass);
        }

        public <T extends Annotation> T getAnnotation(Class<? extends Annotation> annotationClass) {
            return (T) mClassAnnotationHashMap.get(annotationClass);
        }
    }

    private static final Map<Class<?>, XClass> sClassesCache = new ConcurrentHashMap<Class<?>, XClass>();

    public static <T extends Annotation> T getAnnotation(XField field, Class<T> annotationClass) {
        return field.getAnnotation(annotationClass);
    }

    public static List<XField> getEntityKeys(Class<?> clazz) {
        return getXClass(clazz).getFields();
    }

    private static XClass getXClass(Class<?> clazz) {
        XClass xClass = sClassesCache.get(clazz);
        if (xClass == null) {
            xClass = new XClass(clazz);
            sClassesCache.put(clazz, xClass);
        }
        return xClass;
    }

    public static boolean isAnnotationPresent(XField field, Class<? extends Annotation> annotationClass) {
        return field.isAnnotationPresent(annotationClass);
    }

	public static String getStaticStringValue(XField field) {
        return field.getNameOfField();
	}

    public static Class<?> classForName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    //TODO move common logic to XClass
	public static <T> T getInstanceInterface(Class<?> clazz, Class<T> interfaceTargetClazz) {
        XClass xClass = getXClass(clazz);
        return xClass.getInstanceInterface(interfaceTargetClazz);
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

    public static <T> T newSingleInstance(Class<T> clazz) {
        XClass xClass = getXClass(clazz);
        return xClass.getInstance();
    }

}
