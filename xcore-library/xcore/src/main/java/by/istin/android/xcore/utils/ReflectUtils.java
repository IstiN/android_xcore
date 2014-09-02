package by.istin.android.xcore.utils;

import com.google.gson.annotations.SerializedName;

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
import by.istin.android.xcore.annotations.converter.gson.GsonConverter;
import by.istin.android.xcore.annotations.dbEntities;
import by.istin.android.xcore.annotations.dbEntity;
import by.istin.android.xcore.annotations.dbFormattedDate;
import by.istin.android.xcore.annotations.dbIndex;

public class ReflectUtils {

    private static final Object STUB = new Object();

    public static class ConfigWrapper {

        private String key;

        private Config.DBType dbType;

        private Config.Transformer transformer;

        public ConfigWrapper(Config value) {
            key = value.key();
            dbType = value.dbType();
            transformer = ReflectUtils.newSingleInstance(value.transformer());
        }

        public Config.Transformer transformer() {
            return transformer;
        }

        public String key() {
            return key;
        }

        public Config.DBType dbType() {
            return dbType;
        }
    }

    public static ConfigWrapper getClassConfig(Class<?> clazz) {
        return getXClass(clazz).getConfig();
    }

    private static class XClass {

        private Class<?> clazz;

        private Object instance;

        private ConfigWrapper config;

        private XClass(Class<?> clazz) {
            this.clazz = clazz;
            dbEntity annotation = this.clazz.getAnnotation(dbEntity.class);
            if (annotation != null) {
                config = new ConfigWrapper(annotation.value());
            }
        }

        public ConfigWrapper getConfig() {
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

        private ConfigWrapper mConfig;

        private String mSerializedNameValue;

        private boolean isSerializedNameValueInited = false;

        private Class<?> mDbEntityClass;

        private boolean isDbEntityClassInited = false;

        private Class<?> mDbEntitiesClass;

        private boolean isDbEntitiesClassInited = false;

        private boolean isDbFormatInited = false;

        private String mDbFormatValue;

        private String mDbFormatContentValuesKey;

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
                    if (annotationType.equals(dbIndex.class)) {
                        continue;
                    }
                    String name = annotationType.getName();
                    if (name.startsWith(DB_ANNOTATION_PREFIX)) {
                        try {
                            Method method = annotation.getClass().getMethod("value");
                            mConfig = new ConfigWrapper((Config) method.invoke(annotation));
                        } catch (Exception e) {
                            Log.e("ReflectUtils", mField.toString() + " " + annotation.toString());
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

        public ConfigWrapper getConfig() {
            return mConfig;
        }

        public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
            return mAnnotations.contains(annotationClass);
        }

        public <T extends Annotation> T getAnnotation(Class<? extends Annotation> annotationClass) {
            return (T) mClassAnnotationHashMap.get(annotationClass);
        }

        public String getSerializedNameValue(String defaultValue) {
            if (isSerializedNameValueInited) {
                if (mSerializedNameValue != null) {
                    return mSerializedNameValue;
                } else {
                    return defaultValue;
                }
            }
            try {
                if (ReflectUtils.isAnnotationPresent(this, SerializedName.class)) {
                    SerializedName serializedAnnotation = ReflectUtils.getAnnotation(this, SerializedName.class);
                    if (serializedAnnotation != null) {
                        mSerializedNameValue = serializedAnnotation.value();
                        return mSerializedNameValue;
                    }
                }
                return defaultValue;
            } finally {
                isSerializedNameValueInited = true;
            }
        }

        public Class<?> getDbEntityClass() {
            if (isDbEntityClassInited) {
                return mDbEntityClass;
            }
            try {
                dbEntity entity = ReflectUtils.getAnnotation(this, dbEntity.class);
                if (entity != null) {
                    mDbEntityClass = entity.clazz();
                }
                return mDbEntityClass;
            } finally {
                isDbEntityClassInited = true;
            }
        }

        public Class<?> getDbEntitiesClass() {
            if (isDbEntitiesClassInited) {
                return mDbEntitiesClass;
            }
            try {
                dbEntities entity = ReflectUtils.getAnnotation(this, dbEntities.class);
                if (entity != null) {
                    mDbEntitiesClass = entity.clazz();
                }
                return mDbEntitiesClass;
            } finally {
                isDbEntitiesClassInited = true;
            }
        }

        public String getFormat() {
            if (isDbFormatInited) {
                return mDbFormatValue;
            }
            try {
                if (initDateFormatMeta()) return mDbFormatValue;
                return null;
            } finally {
                isDbFormatInited = true;
            }
        }

        public String getFormatContentValuesKey() {
            if (isDbFormatInited) {
                return mDbFormatContentValuesKey;
            }
            try {
                if (initDateFormatMeta()) return mDbFormatContentValuesKey;
                return null;
            } finally {
                isDbFormatInited = true;
            }
        }

        protected boolean initDateFormatMeta() {
            if (ReflectUtils.isAnnotationPresent(this, dbFormattedDate.class)) {
                dbFormattedDate dbFormattedDate = ReflectUtils.getAnnotation(this, dbFormattedDate.class);
                if (dbFormattedDate != null) {
                    mDbFormatValue = dbFormattedDate.format();
                    mDbFormatContentValuesKey = dbFormattedDate.contentValuesKey();
                    return true;
                }
            }
            return false;
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

    private static void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, newValue);
    }

    public static Class<?>[] getSubClasses(Class<?> clazz) {
        return clazz.getClasses();
    }
}
