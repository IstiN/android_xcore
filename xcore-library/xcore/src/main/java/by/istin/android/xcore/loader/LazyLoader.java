package by.istin.android.xcore.loader;

import android.os.Handler;
import android.os.Looper;
import android.support.v4.util.LruCache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;

import by.istin.android.xcore.loader.assist.LazyExecutorService;

public abstract class LazyLoader<View, Params, Result> {

    public static enum BindResult {
        LOADING, DONE, ERROR
    }

    private class BindParam {

        private Listener<View, Params, Result> doneCallback;

        private Params params;

        private View view;

        private BindParam(View view, Listener<View, Params, Result> doneCallback, Params params) {
            this.view = view;
            this.doneCallback = doneCallback;
            this.params = params;
        }

        @Override
        public boolean equals(Object o) {
            Listener<View, Params, Result> oCallback = ((BindParam) o).doneCallback;
            Params oParams = ((BindParam) o).params;
            return oCallback.equals(doneCallback) && oParams.equals(params);
        }

        @Override
        public int hashCode() {
            return params.hashCode();
        }

        public Listener<View, Params, Result> getDoneCallback() {
            return doneCallback;
        }

        public Params getParams() {
            return params;
        }

        public View getView() {
            return view;
        }
    }

    public static interface Listener<View, Params, Result> {
        void success(View v, Params params, Result t);
        void fail(View v, Params params, Throwable t);
    }

    private LruCache<Params, Result> mStorage;

    private Map<Params, Throwable> mErrorStorage;

    private List<BindParam> mQueue;

    private Map<View, BindParam> mViewMap;

    private ExecutorService mExecutor;

    private ExecutorService mCancelExecutor;

    private Handler mHandler;

    private int mQueueSize;

    private final Object mLock = new Object();

    public LazyLoader(int queueSize, int memoryCacheSize) {
        mQueueSize = queueSize;
        mHandler = new Handler(Looper.getMainLooper());
        mViewMap = new WeakHashMap<View, BindParam>();
        mStorage = new LruCache<Params, Result>(memoryCacheSize) {
            @Override
            protected int sizeOf(Params key, Result value) {
                return LazyLoader.this.sizeOf(key, value);
            }
        };
        mErrorStorage = new HashMap<Params, Throwable>();
        mQueue = new ArrayList<BindParam>();
        mExecutor = new LazyExecutorService();
        mCancelExecutor = new LazyExecutorService();
    }

    public abstract int sizeOf(Params p, Result t);

    public abstract Result load(Params p) throws Throwable;

    public abstract void cancel(Params p) throws Throwable;

    public BindResult bind(View view, Listener<View, Params, Result> doneCallback, Params params) {
        synchronized (mLock) {
            BindParam bindParam = new BindParam(view, doneCallback, params);
            final BindParam storedBindParam = mViewMap.get(view);
            if (storedBindParam != null) {
                if (storedBindParam.equals(bindParam)) {
                    return BindResult.LOADING;
                } else {
                    mViewMap.remove(view);
                    mCancelExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                cancel(storedBindParam.params);
                            } catch (Throwable throwable) {
                                //do we realy need this?
                            }
                        }
                    });
                }
            }
            Result result = mStorage.get(params);
            if (result != null) {
                doneCallback.success(view, params, result);
                return BindResult.DONE;
            }
            Throwable throwable = mErrorStorage.get(params);
            if (throwable == null) {
                mViewMap.put(view, bindParam);
                addToQueue(bindParam);
                return BindResult.LOADING;
            } else {
                return BindResult.ERROR;
            }
        }
    }

    private void addToQueue(BindParam bindParam) {
        mQueue.add(0, bindParam);
        if (mQueue.size() > mQueueSize) {
            mQueue.remove(mQueue.size() - 1);
        }
        proceed(bindParam);
    }

    private void proceed(final BindParam bindParam) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final Params params = bindParam.getParams();
                final View view = bindParam.getView();
                final Listener<View, Params, Result> doneCallback = bindParam.getDoneCallback();
                try {
                    final BindParam storedBindParam = mViewMap.get(view);
                    if (storedBindParam != null && storedBindParam.equals(bindParam)) {
                        final Result result = load(params);
                        Handler handler = mHandler;
                        if (handler == null) {
                            return;
                        }
                        mStorage.put(params, result);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                synchronized (mLock) {
                                    final BindParam storedBindParam = mViewMap.get(view);
                                    if (storedBindParam != null && storedBindParam.equals(bindParam)) {
                                        doneCallback.success(view, params, result);
                                        mViewMap.remove(view);
                                        mQueue.remove(bindParam);
                                    }
                                }
                            }
                        });
                    }
                } catch (final Throwable throwable) {
                    Handler handler = mHandler;
                    if (handler == null) {
                        return;
                    }
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            synchronized (mLock) {
                                final BindParam storedBindParam = mViewMap.get(view);
                                if (storedBindParam != null && storedBindParam.equals(bindParam)) {
                                    doneCallback.fail(view, params, throwable);
                                    mViewMap.remove(view);
                                    mQueue.remove(bindParam);
                                }
                            }
                        }

                    });
                }
            }
        });
    }

    public void destroy() {
        synchronized (mLock) {
            mHandler = null;
            mViewMap.clear();
            mStorage.evictAll();
            mErrorStorage.clear();
            mQueue.clear();
            mExecutor.shutdownNow();
            mCancelExecutor.shutdownNow();
        }
    }

}
