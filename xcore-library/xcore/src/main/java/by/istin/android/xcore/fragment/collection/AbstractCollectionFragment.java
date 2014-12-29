package by.istin.android.xcore.fragment.collection;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.format.DateUtils;
import android.view.View;

import java.util.List;

import by.istin.android.xcore.XCoreHelper;
import by.istin.android.xcore.error.IErrorHandler;
import by.istin.android.xcore.fragment.AbstractFragment;
import by.istin.android.xcore.fragment.CursorLoaderFragmentHelper;
import by.istin.android.xcore.fragment.DataSourceExecuteHelper;
import by.istin.android.xcore.fragment.IDataSourceHelper;
import by.istin.android.xcore.fragment.IRefresh;
import by.istin.android.xcore.model.CursorModel;
import by.istin.android.xcore.model.CursorModelLoader;
import by.istin.android.xcore.plugin.IFragmentPlugin;
import by.istin.android.xcore.service.DataSourceService;
import by.istin.android.xcore.service.StatusResultReceiver;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.impl.http.HttpAndroidDataSource;
import by.istin.android.xcore.utils.AppUtils;
import by.istin.android.xcore.utils.HashUtils;
import by.istin.android.xcore.utils.Log;
import by.istin.android.xcore.utils.StringUtil;
import by.istin.android.xcore.utils.UiUtil;

/**
 * Created by IstiN on 21.12.2014.
 */
public abstract class AbstractCollectionFragment<CollectionView, CollectionViewAdapter, Model extends CursorModel> extends AbstractFragment
        implements IRefresh,
        CursorLoaderFragmentHelper.ICursorLoaderFragmentHelper,
        IDataSourceHelper,
        DataSourceExecuteHelper.IDataSourceListener,
            CursorModelLoader.ILoading {

    public static final boolean IS_CHECK_STATUS_LOG_ENABLED = true;

    public static final int LOADER_PRIORITY_SERVICE = 1;

    public static final int LOADER_PRIORITY_HIGH = 2;

    private CollectionView mCollectionView;

    private CollectionViewAdapter mAdapter;

    private View mEmptyView;

    private View mProgressView;

    private View mSecondaryProgressView;

    @Override
    public void onViewCreated(View view) {
        super.onViewCreated(view);
        mCollectionView = (CollectionView) view.findViewById(getCollectionViewId());
        mEmptyView = view.findViewById(getEmptyViewId());
        mProgressView = view.findViewById(getProgressViewId());
        mSecondaryProgressView = view.findViewById(getProgressViewId());
        if (isPagingSupport()) {
            addPagingSupport(view);
        }
    }

    protected abstract void addPagingSupport(View view);

    protected int getCollectionViewId() {
        return android.R.id.list;
    }

    protected int getEmptyViewId() {
        return android.R.id.empty;
    }

    protected int getProgressViewId() {
        return android.R.id.progress;
    }

    protected int getSecondaryProgressViewId() {
        return android.R.id.secondaryProgress;
    }

    public CollectionView getCollectionView() {
        return mCollectionView;
    }

    @Override
    public LoaderManager getSupportLoaderManager() {
        return getLoaderManager();
    }

    @Override
    public int getLoaderId() {
        return (int) HashUtils.generateId(((Object) this).getClass(), getArguments());
    }

    @Override
    public String getSelection() {
        return null;
    }

    @Override
    public String getOrder() {
        return null;
    }

    @Override
    public String[] getSelectionArgs() {
        return null;
    }

    @Override
    public String[] getProjection() {
        return null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        setLoaderWork(true, LOADER_PRIORITY_HIGH);
        Loader<Cursor> cursorLoader = CursorLoaderFragmentHelper.onCreateLoader(this, this, id, args);
        //plugins
        List<IFragmentPlugin> listFragmentPlugins = XCoreHelper.get(getActivity()).getListFragmentPlugins();
        if (listFragmentPlugins != null) {
            for(IFragmentPlugin plugin : listFragmentPlugins) {
                plugin.onCreateLoader(this, cursorLoader, id, args);
            }
        }
        checkStatus("onCreateLoader");
        return cursorLoader;
    }

    public abstract CollectionViewAdapter createAdapter(FragmentActivity fragmentActivity, Model cursor);
    public abstract void setAdapter(CollectionView collectionView, CollectionViewAdapter collectionViewAdapter);
    public abstract void swap(CollectionViewAdapter collectionViewAdapter, Model cursor);
    protected abstract int getAdapterCount(CollectionViewAdapter listAdapter);

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        CollectionViewAdapter adapter = mAdapter;
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        if (adapter == null || !(adapter instanceof CursorAdapter)) {
            mAdapter = createAdapter(activity, (Model)cursor);
            adapter = mAdapter;
            setAdapter(mCollectionView, adapter);
        } else {
            swap(adapter, (Model)cursor);
        }
        //plugins
        List<IFragmentPlugin> listFragmentPlugins = XCoreHelper.get(getActivity()).getListFragmentPlugins();
        if (listFragmentPlugins != null) {
            for(IFragmentPlugin plugin : listFragmentPlugins) {
                plugin.onLoadFinished(this, loader, cursor);
            }
        }
        setLoaderWork(false, LOADER_PRIORITY_HIGH);
        checkStatus("onLoadFinished");
    }

    @Override
    public DataSourceExecuteHelper.IDataSourceListener getDataSourceListener() {
        return this;
    }

    @Override
    public void setServiceWork(boolean isWork) {
        synchronized (mStatusViewLock) {
            isServiceWork = isWork;
        }
    }

    @Override
    public void onError(Exception exception, DataSourceRequest dataSourceRequest) {
        setServiceWork(false);
        exception.printStackTrace();
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        handleException(exception, dataSourceRequest, activity);
        checkStatus("onError");
        //plugins
        List<IFragmentPlugin> listFragmentPlugins = XCoreHelper.get(activity).getListFragmentPlugins();
        if (listFragmentPlugins != null) {
            for (IFragmentPlugin plugin : listFragmentPlugins) {
                plugin.onStatusResultReceiverError(AbstractCollectionFragment.this, exception);
            }
        }
        hideSecondaryProgress();
    }

    public void handleException(Exception exception, DataSourceRequest dataSourceRequest, FragmentActivity activity) {
        IErrorHandler errorHandler = AppUtils.get(activity, IErrorHandler.SYSTEM_SERVICE_KEY);
        errorHandler.onError(activity, AbstractCollectionFragment.this, dataSourceRequest, exception);
    }

    private boolean isServiceWork = false;

    private int loaderPriority = 0;

    private final Object mStatusViewLock = new Object();

    private boolean isLoaderWork = false;

    public boolean isServiceWork() {
        return isServiceWork;
    }

    public void setLoaderWork(boolean isLoaderWork, int priority) {
        synchronized (mStatusViewLock) {
            if (loaderPriority == LOADER_PRIORITY_HIGH && this.isLoaderWork && priority == LOADER_PRIORITY_SERVICE) {
                return;
            }
            this.loaderPriority = priority;
            this.isLoaderWork = isLoaderWork;
        }
    }

    public boolean isLoaderWork() {
        return isLoaderWork;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        if (CursorLoaderFragmentHelper.onActivityCreated(this, savedInstanceState)) {
            setLoaderWork(true, LOADER_PRIORITY_HIGH);
        }
        String url = getUrl();
        loadData(activity, url, isForceUpdateData(), null);
        //plugins
        List<IFragmentPlugin> listFragmentPlugins = XCoreHelper.get(activity).getListFragmentPlugins();
        if (listFragmentPlugins != null) {
            for(IFragmentPlugin plugin : listFragmentPlugins) {
                plugin.onActivityCreated(this, savedInstanceState);
            }
        }
        if (IS_CHECK_STATUS_LOG_ENABLED)
            Log.d("fragment_status", ((Object) this).getClass().getSimpleName() + " onActivityCreated ");
        checkStatus("onActivityCreated");
    }

    public void refresh() {
        if (IS_CHECK_STATUS_LOG_ENABLED)
            Log.d("fragment_status", ((Object)this).getClass().getSimpleName() + " refresh ");
        refresh(getActivity());
    }

    public void refresh(Activity activity) {
        if (IS_CHECK_STATUS_LOG_ENABLED)
            Log.d("fragment_status", ((Object)this).getClass().getSimpleName() + " refresh ");
        loadData(activity, getUrl(), true, null);
    }

    public void loadData(Activity activity, String url, String parentRequestUri) {
        loadData(activity, url, isForceUpdateData(), parentRequestUri);
    }

    public void loadData(Activity activity, String url, Boolean isForceUpdate, String parentRequestUri) {
        if (StringUtil.isEmpty(url)) {
            return;
        }
        final DataSourceRequest dataSourceRequest = createDataSourceRequest(url, isForceUpdate, parentRequestUri);
        dataSourceExecute(activity, dataSourceRequest);
    }

    public DataSourceRequest createDataSourceRequest(String url, Boolean isForceUpdate, String parentRequestUri) {
        final DataSourceRequest dataSourceRequest = new DataSourceRequest(url);
        dataSourceRequest.setCacheable(isCacheable());
        dataSourceRequest.setCacheExpiration(getCacheExpiration());
        dataSourceRequest.setForceUpdateData(isForceUpdate);
        dataSourceRequest.setParentUri(parentRequestUri);
        return dataSourceRequest;
    }

    @Override
    public void dataSourceExecute(final Context context, final DataSourceRequest dataSourceRequest) {
        setServiceWork(true);
        if (IS_CHECK_STATUS_LOG_ENABLED)
            Log.d("fragment_status", ((Object)this).getClass().getSimpleName() + " dataSourceExecute: " + dataSourceRequest.getUri());
        DataSourceService.execute(context, dataSourceRequest, getProcessorKey(), getDataSourceKey(), new StatusResultReceiver(new Handler()) {

            @Override
            public void onAddToQueue(Bundle resultData) {
                setServiceWork(true);
                //plugins
                FragmentActivity activity = getActivity();
                if (activity == null) return;
                List<IFragmentPlugin> listFragmentPlugins = XCoreHelper.get(activity).getListFragmentPlugins();
                if (listFragmentPlugins != null) {
                    for (IFragmentPlugin plugin : listFragmentPlugins) {
                        plugin.onStatusResultReceiverStart(AbstractCollectionFragment.this, resultData);
                    }
                }
                setLoaderWork(true, LOADER_PRIORITY_SERVICE);
                AbstractCollectionFragment.this.onAddToQueue(resultData);
                checkStatus("onAddToQueue");
            }

            @Override
            public void onStart(Bundle resultData) {
                //TODO maybe for some status
            }

            @Override
            public void onError(Exception exception) {
                AbstractCollectionFragment.this.onError(exception, dataSourceRequest);
                setLoaderWork(false, LOADER_PRIORITY_SERVICE);
            }

            @Override
            public void onDone(Bundle resultData) {
                setServiceWork(false);
                FragmentActivity fragmentActivity = getActivity();
                if (fragmentActivity == null) {
                    return;
                }
                //plugins
                List<IFragmentPlugin> listFragmentPlugins = XCoreHelper.get(fragmentActivity).getListFragmentPlugins();
                if (listFragmentPlugins != null) {
                    for (IFragmentPlugin plugin : listFragmentPlugins) {
                        plugin.onStatusResultReceiverDone(AbstractCollectionFragment.this, resultData);
                    }
                }
                onReceiverOnDone(resultData);


                checkStatus("onDone");
            }

            @Override
            protected void onCached(Bundle resultData) {
                setServiceWork(false);
                setLoaderWork(false, LOADER_PRIORITY_SERVICE);
                super.onCached(resultData);
                FragmentActivity context = getActivity();
                if (context == null) {
                    return;
                }
                //plugins
                List<IFragmentPlugin> listFragmentPlugins = XCoreHelper.get(context).getListFragmentPlugins();
                if (listFragmentPlugins != null) {
                    for (IFragmentPlugin plugin : listFragmentPlugins) {
                        plugin.onStatusResultReceiverCached(AbstractCollectionFragment.this, resultData);
                    }
                }
                onReceiverOnCached(resultData);
                checkStatus("onCached");
            }

        });
    }

    public void onAddToQueue(Bundle resultData) {

    }

    public void onReceiverOnCached(Bundle resultData) {

    }

    public void onReceiverOnDone(Bundle resultData) {

    }

    public boolean isForceUpdateData() {
        return false;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //isLoaderWork = false;
        if (getView() != null) {
            if (IS_CHECK_STATUS_LOG_ENABLED)
                Log.d("empty_view", loader.isAbandoned() + " " + loader.isReset() + " " + loader.isStarted());
        }
        swap(mAdapter, null);
        checkStatus("onLoaderReset");
    }

    @Override
    public String getDataSourceKey() {
        return HttpAndroidDataSource.SYSTEM_SERVICE_KEY;
    }

    @Override
    public long getCacheExpiration() {
        return DateUtils.DAY_IN_MILLIS;
    }

    @Override
    public boolean isCacheable() {
        return true;
    }

    @Override
    public CursorModel.CursorModelCreator getCursorModelCreator() {
        return CursorModel.CursorModelCreator.DEFAULT;
    }

    @Override
    public void onDestroy() {
        setLoaderWork(false, LOADER_PRIORITY_HIGH);
        setServiceWork(false);
        checkStatus("onDestroy");
        super.onDestroy();
    }

    @Override
    public void hideProgress() {
        if (IS_CHECK_STATUS_LOG_ENABLED)
            Log.d("fragment_status", "hide progress");
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        View view = getView();
        if (view == null) {
            return;
        }
        hideProgress(view);
    }

    protected void hideProgress(View view) {
        final View progressView = view.findViewById(getProgressViewId());
        if (progressView != null) {
            if (IS_CHECK_STATUS_LOG_ENABLED)
                Log.d("fragment_status", "call progressView.setVisibility(View.GONE)");
            if (UiUtil.hasL()) {
                progressView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressView.setVisibility(View.GONE);
                    }
                }, 100l);
            } else {
                progressView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void showProgress() {
        if (IS_CHECK_STATUS_LOG_ENABLED)
            Log.d("fragment_status", "show progress");
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        View view = getView();
        if (view == null) {
            return;
        }
        showProgress(view);
    }

    protected void showProgress(View view) {
        View progressView = view.findViewById(getProgressViewId());
        if (progressView != null) {
            if (IS_CHECK_STATUS_LOG_ENABLED)
                Log.d("fragment_status", "call progressView.setVisibility(View.VISIBLE)");
            progressView.setVisibility(View.VISIBLE);
        }
    }

    protected void onPageLoad(int newPage, int totalItemCount) {

    }

    protected boolean isPagingSupport() {
        return false;
    }

    private int currentStatusView = STATUS_CONTENT_VISIBLE;

    private static final int STATUS_UNKNOWN = -1;
    private static final int STATUS_CONTENT_VISIBLE = 0;
    private static final int STATUS_PROGRESS_VISIBLE = 1;
    private static final int STATUS_SECONDARY_PROGRESS_VISIBLE = 2;
    private static final int STATUS_EMPTY_VISIBLE = 3;

    public CollectionViewAdapter getAdapter() {
        return mAdapter;
    }

    protected void checkStatus(String reason) {
        synchronized (mStatusViewLock) {
            if (IS_CHECK_STATUS_LOG_ENABLED)
                Log.d("fragment_status", ((Object) this).getClass().getSimpleName() + " reason:" + reason);
            FragmentActivity activity = getActivity();
            if (activity == null) {
                return;
            }
            View view = getView();
            if (view == null) {
                return;
            }
            CollectionViewAdapter listAdapter = mAdapter;
            int size = listAdapter == null ? 0 : getAdapterCount(listAdapter);
            boolean isPaging = isPagingSupport();
            if (IS_CHECK_STATUS_LOG_ENABLED)
                Log.d("fragment_status", ((Object) this).getClass().getSimpleName() + " " + isLoaderWork + " " + isServiceWork + " " + size);
            int newViewStatus = STATUS_UNKNOWN;
            if (isLoaderWork) {
                if (size == 0) {
                    newViewStatus = STATUS_PROGRESS_VISIBLE;
                } else {
                    if (isPaging) {
                        newViewStatus = STATUS_SECONDARY_PROGRESS_VISIBLE;
                    } else {
                        newViewStatus = STATUS_CONTENT_VISIBLE;
                    }
                }
            }
            if (isServiceWork) {
                if (size == 0) {
                    newViewStatus = STATUS_PROGRESS_VISIBLE;
                } else {
                    if (isPaging) {
                        newViewStatus = STATUS_SECONDARY_PROGRESS_VISIBLE;
                    } else {
                        newViewStatus = STATUS_CONTENT_VISIBLE;
                    }
                }
            }
            if (newViewStatus == STATUS_UNKNOWN) {
                if (size == 0) {
                    newViewStatus = STATUS_EMPTY_VISIBLE;
                } else {
                    newViewStatus = STATUS_CONTENT_VISIBLE;
                }
            }
            if (currentStatusView != newViewStatus) {
                if (IS_CHECK_STATUS_LOG_ENABLED)
                    Log.d("fragment_status", "status changed from " + currentStatusView  + " to " + newViewStatus);
                currentStatusView = newViewStatus;
                switch (currentStatusView) {
                    case STATUS_CONTENT_VISIBLE:
                        hideProgress(view);
                        hideSecondaryProgress(view);
                        hideEmptyView(view);
                        break;
                    case STATUS_EMPTY_VISIBLE:
                        hideSecondaryProgress(view);
                        hideProgress(view);
                        showEmptyView(view);
                        break;
                    case STATUS_PROGRESS_VISIBLE:
                        showProgress(view);
                        hideEmptyView(view);
                        hideSecondaryProgress(view);
                        break;
                    case STATUS_SECONDARY_PROGRESS_VISIBLE:
                        showSecondaryProgress(view);
                        hideProgress(view);
                        hideEmptyView(view);
                        break;
                    default:
                        hideProgress(view);
                        hideSecondaryProgress(view);
                        hideEmptyView(view);
                        break;
                }
            }
        }
    }

    protected void hideSecondaryProgress() {
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        View view = getView();
        if (view == null) {
            return;
        }
        hideSecondaryProgress(view);
    }
    protected void hideSecondaryProgress(View view) {
        View progressView = view.findViewById(getSecondaryProgressViewId());
        if (progressView != null) {
            progressView.setVisibility(View.GONE);
        }
    }

    protected void showSecondaryProgress() {
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        View view = getView();
        if (view == null) {
            return;
        }
        showSecondaryProgress(view);
    }

    protected void showSecondaryProgress(View view) {
        View progressView = view.findViewById(getSecondaryProgressViewId());
        if (progressView != null) {
            progressView.setVisibility(View.VISIBLE);
        }
    }

    public void hideEmptyView(View view) {
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        if (view == null) return;
        View emptyView = view.findViewById(getEmptyViewId());
        if (emptyView != null) {
            emptyView.setVisibility(View.GONE);
        }
    }

    public void showEmptyView(View view) {
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        if (view == null) return;
        View emptyView = view.findViewById(getEmptyViewId());
        if (emptyView != null) {
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCursorLoaderStartLoading() {
        setLoaderWork(true, LOADER_PRIORITY_HIGH);
        checkStatus("onCursorLoaderStartLoading");
    }

    @Override
    public void onCursorLoaderStopLoading() {
        setLoaderWork(false, LOADER_PRIORITY_HIGH);
        checkStatus("onCursorLoaderStopLoading");
    }
}
