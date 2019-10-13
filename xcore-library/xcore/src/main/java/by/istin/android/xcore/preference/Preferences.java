package by.istin.android.xcore.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import by.istin.android.xcore.XCoreHelper;
import by.istin.android.xcore.utils.AppUtils;

/**
 * Created by uladzimir_klyshevich on 9/15/15.
 */
public interface Preferences extends XCoreHelper.IAppServiceKey {

    interface IGetSharedPreferences {

        SharedPreferences get();

    }
    String APP_SERVICE_KEY = "xcore:prefs";


    void set(String pKey, String value);
    void set(String pKey, Integer value);
    void set(String pKey, Long value);
    void set(String pKey, Float value);
    void set(String pKey, Boolean value);

    void clear();

    void initAsync();

    void forceUpdate();

    String getString(String key, String def);

    Integer getInt(String key, Integer def);

    Float getFloat(String key, Float def);

    Boolean getBool(String key, Boolean def);

    Boolean getBoolean(String pKey, Boolean def);

    Long getLong(String pKey, Long pDef);

    boolean contains(String pKey);

    void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener pOnSharedPreferenceChangeListener);

    void unregisterOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener pOnSharedPreferenceChangeListener);

    public static class Impl {

        public static Preferences newInstance(final Context pContext) {
            return newInstance(new IGetSharedPreferences() {

                @Override
                public SharedPreferences get() {
                    return PreferenceManager.getDefaultSharedPreferences(pContext);
                }
            });
        }

        public static Preferences newInstance(IGetSharedPreferences pSharedPreferences) {
            final Preferences preference = createPreference(pSharedPreferences);
            preference.initAsync();
            return preference;
        }

        @NonNull
        private static Preferences createPreference(final IGetSharedPreferences pSharedPreferencesGet) {
            return new PreferencesImpl(pSharedPreferencesGet);
        }


        public static Preferences get(Context pContext) {
            return AppUtils.get(pContext, APP_SERVICE_KEY);
        }

        private static class PreferencesImpl implements Preferences {

            private ExecutorService mExecutorService = Executors.newSingleThreadExecutor();

            private ConcurrentHashMap mCache = new ConcurrentHashMap<>();

            private boolean isInited = false;

            private final Object mInitLock = new Object();

            private CountDownLatch mLatch = new CountDownLatch(1);

            private SharedPreferences mSharedPreferences;

            PreferencesImpl(final IGetSharedPreferences pSharedPreferencesGet) {
                mExecutorService.execute(new Runnable() {

                    @Override
                    public void run() {
                        mSharedPreferences = pSharedPreferencesGet.get();
                        mLatch.countDown();
                    }
                });
            }

            @Override
            public void set(final String pKey, final String value) {
                check();
                if (value == null) mCache.remove(pKey); else mCache.put(pKey, value);
                mExecutorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        mSharedPreferences.edit().putString(pKey, value).commit();
                    }
                });
            }

            @Override
            public void set(final String pKey, final Integer value) {
                check();
                if (value == null) mCache.remove(pKey); else mCache.put(pKey, value);
                mExecutorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        mSharedPreferences.edit().putInt(pKey, value).commit();
                    }
                });
            }

            @Override
            public void set(final String pKey, final Long value) {
                check();
                if (value == null) mCache.remove(pKey); else mCache.put(pKey, value);
                mExecutorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        mSharedPreferences.edit().putLong(pKey, value).commit();
                    }
                });
            }

            @Override
            public void set(final String pKey, final Float value) {
                check();
                if (value == null) mCache.remove(pKey); else mCache.put(pKey, value);
                mExecutorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        mSharedPreferences.edit().putFloat(pKey, value).commit();
                    }
                });
            }

            @Override
            public void set(final String pKey, final Boolean value) {
                check();
                if (value == null) mCache.remove(pKey); else mCache.put(pKey, value);
                mExecutorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        mSharedPreferences.edit().putBoolean(pKey, value).commit();
                    }
                });
            }

            @Override
            public void clear() {
                check();
                mCache.clear();
                mExecutorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        mSharedPreferences.edit().clear().commit();
                    }
                });
            }

            @Override
            public void initAsync() {
                mExecutorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        initSync();
                    }

                });
            }

            @Override
            public void forceUpdate() {
                isInited = false;
                initAsync();
            }

            public void check() {
                if (!isInited) {
                    initSync();
                }
            }

            private void initSync() {
                try {
                    mLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (mInitLock) {
                    if (!isInited) {
                        final Map<? extends String, ?> all = mSharedPreferences.getAll();
                        mCache.putAll(all);
                        isInited = true;
                    }
                }
            }

            @Override
            public Boolean getBoolean(String key, Boolean def) {
                check();
                final Object result = mCache.get(key);
                if (result == null) {
                    return def;
                } else {
                    if (result instanceof Boolean) {
                        return (Boolean) result;
                    } else {
                        return Boolean.valueOf((String) result);
                    }
                }
            }

            @Override
            public Long getLong(String pKey, Long pDef) {
                check();
                final Object result = mCache.get(pKey);
                if (result == null) {
                    return pDef;
                } else {
                    if (result instanceof Long) {
                        return (Long)result;
                    }
                    return Long.parseLong((String) result);
                }
            }

            @Override
            public boolean contains(String pKey) {
                check();
                return mCache.contains(pKey);
            }

            @Override
            public void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener pOnSharedPreferenceChangeListener) {
                check();
                synchronized (mInitLock) {
                    mSharedPreferences.registerOnSharedPreferenceChangeListener(pOnSharedPreferenceChangeListener);
                }
            }

            @Override
            public void unregisterOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener pOnSharedPreferenceChangeListener) {
                check();
                synchronized (mInitLock) {
                    mSharedPreferences.unregisterOnSharedPreferenceChangeListener(pOnSharedPreferenceChangeListener);
                }
            }

            public String getString(String key, String def) {
                check();
                final Object result = mCache.get(key);
                if (result == null) {
                    return def;
                } else {
                    return (String) result;
                }
            }

            @Override
            public Integer getInt(String key, Integer def) {
                check();
                final Object result = mCache.get(key);
                if (result == null) {
                    return def;
                } else {
                    if (result instanceof Integer) {
                        return (Integer) result;
                    }
                    return Integer.valueOf((String) result);
                }
            }

            @Override
            public Float getFloat(String key, Float def) {
                check();
                final Object result = mCache.get(key);
                if (result == null) {
                    return def;
                } else {
                    if (result instanceof Float) {
                        return (Float) result;
                    }
                    return Float.valueOf((String) result);
                }
            }

            @Override
            public Boolean getBool(String key, Boolean def) {
                return getBoolean(key, def);
            }

            @Override
            public String getAppServiceKey() {
                return APP_SERVICE_KEY;
            }
        }
    }

}
