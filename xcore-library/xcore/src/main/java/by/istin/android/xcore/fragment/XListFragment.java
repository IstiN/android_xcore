package by.istin.android.xcore.fragment;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.FilterQueryProvider;
import android.widget.Filterable;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import by.istin.android.xcore.XCoreHelper;
import by.istin.android.xcore.error.IErrorHandler;
import by.istin.android.xcore.fragment.CursorLoaderFragmentHelper.ICursorLoaderFragmentHelper;
import by.istin.android.xcore.model.CursorModel;
import by.istin.android.xcore.model.CursorModelLoader;
import by.istin.android.xcore.plugin.IFragmentPlugin;
import by.istin.android.xcore.provider.ModelContract;
import by.istin.android.xcore.service.DataSourceService;
import by.istin.android.xcore.service.StatusResultReceiver;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.impl.http.HttpAndroidDataSource;
import by.istin.android.xcore.utils.AppUtils;
import by.istin.android.xcore.utils.HashUtils;
import by.istin.android.xcore.utils.Log;
import by.istin.android.xcore.utils.StringUtil;
import by.istin.android.xcore.utils.UiUtil;
import by.istin.android.xcore.widget.ISetViewBinder;

public abstract class XListFragment extends AdapterViewFragment
        implements IRefresh,
            ICursorLoaderFragmentHelper,
            IDataSourceHelper,
            DataSourceExecuteHelper.IDataSourceListener,
            CursorModelLoader.ILoading {

    public static final boolean IS_CHECK_STATUS_LOG_ENABLED = true;

    public static final int LOADER_PRIORITY_SERVICE = 1;

    public static final int LOADER_PRIORITY_HIGH = 2;

    private class EndlessScrollListener implements OnScrollListener {

        private int visibleThreshold = 5;

        private int currentPage = 0;

        private int previousTotal = 0;

        private volatile boolean pagingLoading = false;

        public EndlessScrollListener() {

        }

        public EndlessScrollListener(int visibleThreshold) {
            this.visibleThreshold = visibleThreshold;
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                int visibleItemCount, int totalItemCount) {
            ListAdapter adapter = view.getAdapter();
            int count = getRealAdapterCount(adapter);
            if (count == 0) {
                return;
            }
            if (IS_CHECK_STATUS_LOG_ENABLED)
            Log.d("fragment_status", "paging " + firstVisibleItem + " " + visibleItemCount + " " + totalItemCount + " " + count);
            if (previousTotal != totalItemCount && !pagingLoading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                previousTotal = totalItemCount;
            	pagingLoading = true;
                currentPage++;
            	onPageLoad(currentPage, totalItemCount);
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

    }

    public static int getRealAdapterCount(ListAdapter adapter) {
        if (adapter == null) {
            return 0;
        }
        int count = adapter.getCount();
        if (adapter instanceof HeaderViewListAdapter) {
            HeaderViewListAdapter headerViewListAdapter = (HeaderViewListAdapter) adapter;
            count = count - headerViewListAdapter.getFootersCount() - headerViewListAdapter.getHeadersCount();
        }
        return count;
    }

    private TextWatcher mWatcher = new TextWatcher() {

        private View searchClear;

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void afterTextChanged(Editable s) {
            View view = getView();
            if (view == null) {
                return;
            }
            ListView av = (ListView) view.findViewById(android.R.id.list);
            ListAdapter adapter = av.getAdapter();
            if (adapter instanceof HeaderViewListAdapter) {
                adapter = ((HeaderViewListAdapter) adapter).getWrappedAdapter();
            }
            Filterable filterAdapter = (Filterable) adapter;
            String value = s.toString();
            if (getSearchEditTextClearId() != null) {
                if (searchClear == null) {
                    searchClear = view.findViewById(getSearchEditTextClearId());
                }
                if (StringUtil.isEmpty(value)) {
                    if (searchClear != null) {
                        searchClear.setVisibility(View.GONE);
                    }
                } else {
                    if (searchClear != null) {
                        searchClear.setVisibility(View.VISIBLE);
                    }
                }
            }
            if (filterAdapter == null) {
                return;
            }
            Filter filter = filterAdapter.getFilter();
            if (filter != null) {
                filter.filter(value);
            }
        }

    };

    private EndlessScrollListener mEndlessScrollListener;

    @Override
    public LoaderManager getSupportLoaderManager() {
        return getLoaderManager();
    }

	@Override
	public int getLoaderId() {
		return (int)HashUtils.generateId(((Object) this).getClass(), getArguments());
	}

    private final List<OnScrollListener> onScrollListenerList = new ArrayList<OnScrollListener>();

    public void setOnScrollListViewListener(OnScrollListener scrollListViewListener) {
        onScrollListenerList.add(scrollListViewListener);
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(getViewLayout(), container, false);
        onViewCreated(view);

        //plugins
        List<IFragmentPlugin> listFragmentPlugins = XCoreHelper.get(view.getContext()).getListFragmentPlugins();
        if (listFragmentPlugins != null) {
            for(IFragmentPlugin plugin : listFragmentPlugins) {
                plugin.onCreateView(this, view, inflater, container, savedInstanceState);
            }
        }

		if (isPagingSupport()) {
			mEndlessScrollListener = new EndlessScrollListener();
            setOnScrollListViewListener(mEndlessScrollListener);
			((AbsListView)view.findViewById(android.R.id.list)).setOnScrollListener(new OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView absListView, int i) {
                    for (OnScrollListener onScrollListener : onScrollListenerList) {
                        onScrollListener.onScrollStateChanged(absListView, i);
                    }
                }

                @Override
                public void onScroll(AbsListView absListView, int i, int i2, int i3) {
                    for (OnScrollListener onScrollListener : onScrollListenerList) {
                        onScrollListener.onScroll(absListView, i, i2, i3);
                    }
                }
            });
		}
		Integer searchEditTextId = getSearchEditTextId();
		if (searchEditTextId == null) {
			return view;
		}
		final EditText searchEditText = (EditText) view.findViewById(getSearchEditTextId());
		Integer searchHintText = getSearchHintText();
		if (searchHintText != null) {
			searchEditText.setHint(searchHintText);
		}
		final Integer searchEditTextClearId = getSearchEditTextClearId();
		if (searchEditTextClearId != null) {
			View searchClear = view.findViewById(searchEditTextClearId);
			if (searchClear != null) {
				searchClear.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View paramView) {
						searchEditText.setText(StringUtil.EMPTY);
					}
					
				});
			}
		}
        searchEditText.addTextChangedListener(mWatcher);
		return view;
	}

	protected void onPageLoad(int newPage, int totalItemCount) {
		
	}

	protected boolean isPagingSupport() {
		return false;
	}
	
	protected Integer getSearchHintText() {
		return null;
	}

	public void onViewCreated(View view) {
		
	}

	@Override
    public void onAdapterViewItemClick(AdapterView<?> l, View v, int position, long id) {
		super.onAdapterViewItemClick(l, v, position, id);
		Cursor cursor = (Cursor) l.getAdapter().getItem(position);
		onListItemClick(cursor, v, position, id);
	}

	public abstract void onListItemClick(Cursor cursor, View v, int position, long id);

	public abstract int getViewLayout();
	
	public Integer getSearchEditTextId() {
		return null;
	}
	
	public Integer getSearchEditTextClearId() {
		return null;
	}
	
	public String getSearchField() {
		return null;
	}
	
	public String getSelection() {
		return null;
	}
	
	public String getOrder() {
		return null;
	}
	
	public String[] getSelectionArgs() {
		return null;
	}
	
	public String[] getProjection() {
		return null;
	}
	
	public abstract Uri getUri();
	
	public abstract String getUrl();
	
	public abstract String getProcessorKey();
	
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
	
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        ListAdapter adapter = getListAdapter();
		FragmentActivity activity = getActivity();
		if (activity == null) {
			return;
		}
		if (adapter == null || !(adapter instanceof CursorAdapter)) {
			BaseAdapter baseAdapter = createAdapter(activity, cursor);
			ViewBinder adapterViewBinder = getAdapterViewBinder();
			if (adapterViewBinder != null && baseAdapter instanceof ISetViewBinder) {
                ((ISetViewBinder)baseAdapter).setViewBinder(adapterViewBinder);
			}
            if (baseAdapter instanceof CursorAdapter) {
                ((CursorAdapter)baseAdapter).setFilterQueryProvider(new FilterQueryProvider() {

                    @Override
                    public Cursor runQuery(CharSequence constraint) {
                        return runSearchQuery(getActivity(), constraint);
                    }

                });
            }
			adapter = baseAdapter;
			setListAdapter(adapter);
		} else {
			((CursorAdapter) adapter).swapCursor(cursor);
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

    public Cursor runSearchQuery(Context context, CharSequence constraint) {
        Uri uri = getUri();
        if (!StringUtil.isEmpty(ModelContract.getSqlParam(uri))) {
            throw new IllegalArgumentException("you need Override XListFragment.runSearchQuery method if you want use search functionality");
        }
        String selection = getSelection();
        if (StringUtil.isEmpty(selection)) {
            selection = getSearchField() + " like ?";
        } else {
            selection = selection + " AND " + getSearchField() + " like ?";
        }
        String[] selectionArgs = getSelectionArgs();
        String[] searchArgs = {"%" + StringUtil.translit(constraint.toString().trim()) + "%"};
        if (selectionArgs == null) {
            selectionArgs = searchArgs;
        } else {
            selectionArgs = concat(selectionArgs, searchArgs);
        }
        return context.getContentResolver().query(uri, getProjection(), selection, selectionArgs, getOrder());
    }

    private static String[] concat(String[] first, String[] second) {
        String[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public BaseAdapter createAdapter(FragmentActivity activity, Cursor cursor) {
        int adapterLayout = getAdapterLayout();
        String[] adapterColumns = getAdapterColumns();
        int[] adapterControlIds = getAdapterControlIds();
        BaseAdapter baseAdapter = createAdapter(activity, cursor, adapterLayout, adapterColumns, adapterControlIds);
        //plugins
        List<IFragmentPlugin> listFragmentPlugins = XCoreHelper.get(getActivity()).getListFragmentPlugins();
        if (listFragmentPlugins != null) {
            for(IFragmentPlugin plugin : listFragmentPlugins) {
                plugin.createAdapter(this, baseAdapter, activity, cursor);
            }
        }
        return baseAdapter;
	}

    public BaseAdapter createAdapter(final FragmentActivity activity, final Cursor cursor, final int adapterLayout, final String[] adapterColumns, final int[] adapterControlIds) {
        return new DefaultAdapter(activity, adapterLayout, cursor, adapterColumns, adapterControlIds);
    }

    protected View onAdapterGetView(SimpleCursorAdapter simpleCursorAdapter, int position, View view) {
        return view;
    }

    @Override
    public DataSourceExecuteHelper.IDataSourceListener getDataSourceListener() {
        return this;
    }

    protected abstract String[] getAdapterColumns();

	protected abstract int[] getAdapterControlIds();

	protected abstract int getAdapterLayout();

	protected boolean setAdapterViewImage(ImageView v, String value) {
        //plugins
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return true;
        }
        List<IFragmentPlugin> listFragmentPlugins = XCoreHelper.get(activity).getListFragmentPlugins();
        if (listFragmentPlugins != null) {
            for(IFragmentPlugin plugin : listFragmentPlugins) {
                if (plugin.setAdapterViewImage(this, v, value)) {
                    return true;
                }
            }
        }
		return false;
	}
	
	protected boolean setAdapterViewText(TextView v, String value) {
		return false;
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
                plugin.onStatusResultReceiverError(XListFragment.this, exception);
            }
        }
        hidePagingProgress();
    }

    public void handleException(Exception exception, DataSourceRequest dataSourceRequest, FragmentActivity activity) {
        IErrorHandler errorHandler = AppUtils.get(activity, IErrorHandler.SYSTEM_SERVICE_KEY);
        errorHandler.onError(activity, XListFragment.this, dataSourceRequest, exception);
    }

    protected ViewBinder getAdapterViewBinder() {
		return null;
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
		if (CursorLoaderFragmentHelper.onActivityCreated(this, savedInstanceState)) {
            setLoaderWork(true, LOADER_PRIORITY_HIGH);
        }
		String url = getUrl();
        loadData(getActivity(), url, isForceUpdateData(), null);
        //plugins
        List<IFragmentPlugin> listFragmentPlugins = XCoreHelper.get(getActivity()).getListFragmentPlugins();
        if (listFragmentPlugins != null) {
            for(IFragmentPlugin plugin : listFragmentPlugins) {
                plugin.onActivityCreated(this, savedInstanceState);
            }
        }
        if (IS_CHECK_STATUS_LOG_ENABLED)
        Log.d("fragment_status", ((Object)this).getClass().getSimpleName() + " onActivityCreated ");
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
        if (isPagingSupport()) {
            mEndlessScrollListener.currentPage = 0;
            mEndlessScrollListener.previousTotal = 0;
        }
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
                        plugin.onStatusResultReceiverStart(XListFragment.this, resultData);
                    }
                }
                setLoaderWork(true, LOADER_PRIORITY_SERVICE);
                XListFragment.this.onAddToQueue(resultData);
                checkStatus("onAddToQueue");
            }

            @Override
            public void onStart(Bundle resultData) {
                //TODO maybe for some status
            }

            @Override
            public void onError(Exception exception) {
                XListFragment.this.onError(exception, dataSourceRequest);
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
                        plugin.onStatusResultReceiverDone(XListFragment.this, resultData);
                    }
                }
                onReceiverOnDone(resultData);

                if (mEndlessScrollListener != null && mEndlessScrollListener.pagingLoading) {
                    mEndlessScrollListener.pagingLoading = false;
                }

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
                        plugin.onStatusResultReceiverCached(XListFragment.this, resultData);
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

    protected boolean isForceUpdateData() {
		return false;
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
        //isLoaderWork = false;
		if (getView() != null) {
            if (IS_CHECK_STATUS_LOG_ENABLED)
            Log.d("empty_view", loader.isAbandoned() + " " + loader.isReset() + " " + loader.isStarted());
            Adapter adapter = getListView().getAdapter();
            if (adapter != null) {
                if (adapter instanceof HeaderViewListAdapter) {
                    adapter = ((HeaderViewListAdapter) adapter).getWrappedAdapter();
                }
                if (adapter instanceof CursorAdapter) {
                    ((CursorAdapter) adapter).swapCursor(null);
                }
            }
		}
        checkStatus("onLoaderReset");
	}
	
	public String getDataSourceKey() {
		return HttpAndroidDataSource.SYSTEM_SERVICE_KEY;
	}

	public long getCacheExpiration() {
		return DateUtils.DAY_IN_MILLIS;
	}

	public boolean isCacheable() {
		return true;
	}

    @Override
    public CursorModel.CursorModelCreator getCursorModelCreator() {
        return CursorModel.CursorModelCreator.DEFAULT;
    }

    @Override
    public void onDestroy() {
        mEndlessScrollListener = null;
        mWatcher = null;
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

    protected int getProgressViewId() {
        return android.R.id.progress;
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

    protected void showPagingProgress() {
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        View view = getView();
        if (view == null) {
            return;
        }
        showPagingProgress(view);
    }

    protected void showPagingProgress(View view) {
        View progressView = view.findViewById(getSecondaryProgressId());
        if (progressView != null) {
            progressView.setVisibility(View.VISIBLE);
        }
    }

    protected int getSecondaryProgressId() {
        return android.R.id.secondaryProgress;
    }

    protected void hidePagingProgress() {
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        View view = getView();
        if (view == null) {
            return;
        }
        hidePagingProgress(view);
    }

    protected void hidePagingProgress(View view) {
        View progressView = view.findViewById(getSecondaryProgressId());
        if (progressView != null) {
            progressView.setVisibility(View.GONE);
        }
    }

    private int currentStatusView = STATUS_CONTENT_VISIBLE;

    private static final int STATUS_UNKNOWN = -1;
    private static final int STATUS_CONTENT_VISIBLE = 0;
    private static final int STATUS_PROGRESS_VISIBLE = 1;
    private static final int STATUS_SECONDARY_PROGRESS_VISIBLE = 2;
    private static final int STATUS_EMPTY_VISIBLE = 3;

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
            ListAdapter listAdapter = getListAdapter();
            int size = getRealAdapterCount(listAdapter);
            boolean isPaging = mEndlessScrollListener != null;
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
                        hidePagingProgress(view);
                        hideEmptyView(view);
                        break;
                    case STATUS_EMPTY_VISIBLE:
                        hidePagingProgress(view);
                        hideProgress(view);
                        showEmptyView(view);
                        break;
                    case STATUS_PROGRESS_VISIBLE:
                        showProgress(view);
                        hideEmptyView(view);
                        hidePagingProgress(view);
                        break;
                    case STATUS_SECONDARY_PROGRESS_VISIBLE:
                        showPagingProgress(view);
                        hideProgress(view);
                        hideEmptyView(view);
                        break;
                    default:
                        hideProgress(view);
                        hidePagingProgress(view);
                        hideEmptyView(view);
                        break;
                }
            }
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

    protected int getEmptyViewId() {
        return android.R.id.empty;
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

    private class DefaultAdapter extends SimpleCursorAdapter implements ISetViewBinder {

        public DefaultAdapter(FragmentActivity activity, int adapterLayout, Cursor cursor, String[] adapterColumns, int[] adapterControlIds) {
            super(activity, adapterLayout, cursor, adapterColumns, adapterControlIds, 2);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            view = XListFragment.this.onAdapterGetView(this, position, view);
            return view;
        }

        @Override
        public void setViewImage(ImageView v, String value) {
            if (!setAdapterViewImage(v, value)) {
                super.setViewImage(v, value);
            }
        }

        public void setViewText(TextView v, String text) {
            if (!setAdapterViewText(v, text)) {
                super.setViewText(v, text);
            }
        }

    }

}
