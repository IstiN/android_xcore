package by.istin.android.xcore.fragment;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
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
import android.widget.*;
import android.widget.AbsListView.OnScrollListener;
import by.istin.android.xcore.XCoreHelper;
import by.istin.android.xcore.error.IErrorHandler;
import by.istin.android.xcore.fragment.CursorLoaderFragmentHelper.ICursorLoaderFragmentHelper;
import by.istin.android.xcore.model.CursorModel;
import by.istin.android.xcore.plugin.IXListFragmentPlugin;
import by.istin.android.xcore.service.DataSourceService;
import by.istin.android.xcore.service.StatusResultReceiver;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.impl.http.HttpAndroidDataSource;
import by.istin.android.xcore.utils.AppUtils;
import by.istin.android.xcore.utils.HashUtils;
import by.istin.android.xcore.utils.Log;
import by.istin.android.xcore.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

public abstract class XListFragment extends AdapterViewFragment implements ICursorLoaderFragmentHelper, IDataSourceHelper, DataSourceExecuteHelper.IDataSourceListener {

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
            //Log.d("fragment_status", "paging " + firstVisibleItem + " " + visibleItemCount + " " + totalItemCount + " " + count);
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
            SimpleCursorAdapter filterAdapter = (SimpleCursorAdapter) adapter;
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
            filterAdapter.getFilter().filter(value);
        }

    };

    private EndlessScrollListener mEndlessScrollListener;
	
	@Override
	public int getLoaderId() {
		return (int)HashUtils.generateId(((Object) this).getClass(), getArguments());
	}

    private List<OnScrollListener> onScrollListenerList = new ArrayList<OnScrollListener>();

    public void setOnScrollListViewListener(OnScrollListener scrollListViewListener) {
        onScrollListenerList.add(scrollListViewListener);
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(getViewLayout(), container, false);
        onViewCreated(view);

        //plugins
        List<IXListFragmentPlugin> listFragmentPlugins = XCoreHelper.get(view.getContext()).getListFragmentPlugins();
        if (listFragmentPlugins != null) {
            for(IXListFragmentPlugin plugin : listFragmentPlugins) {
                plugin.onCreateView(this, view, inflater, container, savedInstanceState);
            }
        }

		if (isPagingSupport()) {
			mEndlessScrollListener = new EndlessScrollListener();
            setOnScrollListViewListener(mEndlessScrollListener);
			((ListView)view.findViewById(android.R.id.list)).setOnScrollListener(new OnScrollListener() {
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
        Loader<Cursor> cursorLoader = CursorLoaderFragmentHelper.onCreateLoader(this, id, args);
        //plugins
        List<IXListFragmentPlugin> listFragmentPlugins = XCoreHelper.get(getActivity()).getListFragmentPlugins();
        if (listFragmentPlugins != null) {
            for(IXListFragmentPlugin plugin : listFragmentPlugins) {
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
			SimpleCursorAdapter cursorAdapter = createAdapter(activity, cursor);
			ViewBinder adapterViewBinder = getAdapterViewBinder();
			if (adapterViewBinder != null) {
				cursorAdapter.setViewBinder(adapterViewBinder);
			}
			cursorAdapter.setFilterQueryProvider(new FilterQueryProvider() {
				
				@Override
				public Cursor runQuery(CharSequence constraint) {
					return getActivity().getContentResolver().query(getUri(), getProjection(), getSearchField() + " like ?", new String[]{"%"+StringUtil.translit(constraint.toString().trim())+"%"}, getOrder());
				}
				
			});
			adapter = cursorAdapter;
			setListAdapter(adapter);
		} else {
			((CursorAdapter) adapter).swapCursor(cursor);
		}
        //plugins
        List<IXListFragmentPlugin> listFragmentPlugins = XCoreHelper.get(getActivity()).getListFragmentPlugins();
        if (listFragmentPlugins != null) {
            for(IXListFragmentPlugin plugin : listFragmentPlugins) {
                plugin.onLoadFinished(this, loader, cursor);
            }
        }
        setLoaderWork(false, LOADER_PRIORITY_HIGH);
        checkStatus("onLoadFinished");
	}

	public SimpleCursorAdapter createAdapter(FragmentActivity activity, Cursor cursor) {
        int adapterLayout = getAdapterLayout();
        String[] adapterColumns = getAdapterColumns();
        int[] adapterControlIds = getAdapterControlIds();
        SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(activity, adapterLayout, cursor, adapterColumns, adapterControlIds, 2) {

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

        };
        //plugins
        List<IXListFragmentPlugin> listFragmentPlugins = XCoreHelper.get(getActivity()).getListFragmentPlugins();
        if (listFragmentPlugins != null) {
            for(IXListFragmentPlugin plugin : listFragmentPlugins) {
                plugin.createAdapter(this, simpleCursorAdapter, activity, cursor);
            }
        }
        return simpleCursorAdapter;
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
        List<IXListFragmentPlugin> listFragmentPlugins = XCoreHelper.get(activity).getListFragmentPlugins();
        if (listFragmentPlugins != null) {
            for(IXListFragmentPlugin plugin : listFragmentPlugins) {
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
        isServiceWork = isWork;
    }

    @Override
    public void onError(Exception exception, DataSourceRequest dataSourceRequest) {
        isServiceWork = false;
        exception.printStackTrace();
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        IErrorHandler errorHandler = (IErrorHandler) AppUtils.get(activity, IErrorHandler.SYSTEM_SERVICE_KEY);
        if (errorHandler != null) {
            errorHandler.onError(activity, XListFragment.this, dataSourceRequest, exception);
        } else {
            Toast.makeText(activity, exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
        checkStatus("onError");
        //plugins
        List<IXListFragmentPlugin> listFragmentPlugins = XCoreHelper.get(activity).getListFragmentPlugins();
        if (listFragmentPlugins != null) {
            for (IXListFragmentPlugin plugin : listFragmentPlugins) {
                plugin.onStatusResultReceiverError(XListFragment.this, exception);
            }
        }
    }

    protected ViewBinder getAdapterViewBinder() {
		return null;
	}
	
	private boolean isServiceWork = false;

    private int loaderPriority = 0;

    private boolean isLoaderWork = false;

    public boolean isServiceWork() {
        return isServiceWork;
    }

    public void setLoaderWork(boolean isLoaderWork, int priority) {
        if (loaderPriority == LOADER_PRIORITY_HIGH && this.isLoaderWork && priority == LOADER_PRIORITY_SERVICE) {
            return;
        }
        this.loaderPriority = priority;
        this.isLoaderWork = isLoaderWork;
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
        List<IXListFragmentPlugin> listFragmentPlugins = XCoreHelper.get(getActivity()).getListFragmentPlugins();
        if (listFragmentPlugins != null) {
            for(IXListFragmentPlugin plugin : listFragmentPlugins) {
                plugin.onActivityCreated(this, savedInstanceState);
            }
        }
        Log.d("fragment_status", getClass().getSimpleName() + " onActivityCreated ");
        checkStatus("onActivityCreated");
	}

    public void refresh() {
        Log.d("fragment_status", getClass().getSimpleName() + " refresh ");
        refresh(getActivity());
    }

    public void refresh(Activity activity) {
        Log.d("fragment_status", getClass().getSimpleName() + " refresh ");
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
        isServiceWork = true;
        Log.d("fragment_status", getClass().getSimpleName() + " dataSourceExecute: " + dataSourceRequest.getUri());
        DataSourceService.execute(context, dataSourceRequest, getProcessorKey(), getDataSourceKey(), new StatusResultReceiver(new Handler(Looper.getMainLooper())) {

            @Override
            public void onAddToQueue(Bundle resultData) {
                isServiceWork = true;
                //plugins
                FragmentActivity activity = getActivity();
                if (activity == null) return;
                List<IXListFragmentPlugin> listFragmentPlugins = XCoreHelper.get(activity).getListFragmentPlugins();
                if (listFragmentPlugins != null) {
                    for (IXListFragmentPlugin plugin : listFragmentPlugins) {
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
                isServiceWork = false;
                //TODO needs check if loader not was launched and finished before
                //isLoaderWork = true;
                FragmentActivity fragmentActivity = getActivity();
                if (fragmentActivity == null) {
                    return;
                }
                //plugins
                List<IXListFragmentPlugin> listFragmentPlugins = XCoreHelper.get(fragmentActivity).getListFragmentPlugins();
                if (listFragmentPlugins != null) {
                    for (IXListFragmentPlugin plugin : listFragmentPlugins) {
                        plugin.onStatusResultReceiverDone(XListFragment.this, resultData);
                    }
                }
                onReceiverOnDone(resultData);
                checkStatus("onDone");
            }

            @Override
            protected void onCached(Bundle resultData) {
                isServiceWork = false;
                setLoaderWork(false, LOADER_PRIORITY_SERVICE);
                super.onCached(resultData);
                FragmentActivity context = getActivity();
                if (context == null) {
                    return;
                }
                //plugins
                List<IXListFragmentPlugin> listFragmentPlugins = XCoreHelper.get(context).getListFragmentPlugins();
                if (listFragmentPlugins != null) {
                    for (IXListFragmentPlugin plugin : listFragmentPlugins) {
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
            Log.d("empty_view", loader.isAbandoned() + " " + loader.isReset() + " " + loader.isStarted());
            Adapter adapter = getListView().getAdapter();
            if (adapter instanceof HeaderViewListAdapter) {
                adapter = ((HeaderViewListAdapter) adapter).getWrappedAdapter();
            }
            ((CursorAdapter) adapter).swapCursor(null);
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
        isServiceWork = false;
        checkStatus("onDestroy");
        super.onDestroy();
    }

    @Override
    public void hideProgress() {
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        View view = getView();
        if (view == null) {
            return;
        }
        View progressView = view.findViewById(getProgressViewId());
        if (progressView != null) {
            progressView.setVisibility(View.GONE);
        }
    }

    protected int getProgressViewId() {
        return android.R.id.progress;
    }

    @Override
    public void showProgress() {
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        View view = getView();
        if (view == null) {
            return;
        }
        View progressView = view.findViewById(getProgressViewId());
        if (progressView != null) {
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
        View progressView = view.findViewById(getSecondaryProgressId());
        if (progressView != null) {
            progressView.setVisibility(View.GONE);
        }
    }

    protected void checkStatus(String reason) {
        Log.d("fragment_status", getClass().getSimpleName() + " reason:" + reason);
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
        Log.d("fragment_status", getClass().getSimpleName() + " " + isLoaderWork + " " + isServiceWork + " " + size);
        if (isLoaderWork) {
            if (size == 0) {
                showProgress();
                hideEmptyView(view);
                hidePagingProgress();
            } else {
                hidePagingProgress();
                hideProgress();
                hideEmptyView(view);
            }
            return;
        }
        if (isServiceWork) {
            if (size == 0) {
                showProgress();
                hideEmptyView(view);
                hidePagingProgress();
            } else {
                if (isPaging) {
                    showPagingProgress();
                }
                hideEmptyView(view);
                hideProgress();
            }
            return;
        }
        if (mEndlessScrollListener != null) {
            mEndlessScrollListener.pagingLoading = false;
        }
        if (size == 0) {
            if (isPaging) {
                hidePagingProgress();
            }
            hideProgress();
            showEmptyView(view);
        } else {
            hideProgress();
            if (isPaging) {
                hidePagingProgress();
            }
            hideEmptyView(view);
        }
    }

    public void hideEmptyView(View view) {
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        if (view == null) return;
        View emptyView = view.findViewById(android.R.id.empty);
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
        View emptyView = view.findViewById(android.R.id.empty);
        if (emptyView != null) {
            emptyView.setVisibility(View.VISIBLE);
        }
    }
}
