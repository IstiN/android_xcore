package by.istin.android.xcore.fragment;

import android.R;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
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
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

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
import by.istin.android.xcore.utils.CursorUtils;
import by.istin.android.xcore.utils.HashUtils;
import by.istin.android.xcore.utils.StringUtil;

public abstract class XListFragment extends ListFragment implements ICursorLoaderFragmentHelper, IDataSourceHelper {

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
            if (adapter == null || adapter.getCount() == 0) {
                return;
            }
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

    private TextWatcher watcher = new TextWatcher() {

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
            ListView av = (ListView) view.findViewById(R.id.list);
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
        searchEditText.addTextChangedListener(watcher);
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
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
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
        Loader<Cursor> cursorLoader = CursorLoaderFragmentHelper.onCreateLoader(this, id, args);
        //plugins
        List<IXListFragmentPlugin> listFragmentPlugins = XCoreHelper.get(getActivity()).getListFragmentPlugins();
        if (listFragmentPlugins != null) {
            for(IXListFragmentPlugin plugin : listFragmentPlugins) {
                plugin.onCreateLoader(this, cursorLoader, id, args);
            }
        }
        showProgress();
        return cursorLoader;
	}
	
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        hideProgress();
        hidePagingProgress();
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
		if (isServiceWork) {
			hideEmptyView(getView());
		} else if (CursorUtils.isEmpty(cursor)) {
            showEmptyView(getView());
        }

        //plugins
        List<IXListFragmentPlugin> listFragmentPlugins = XCoreHelper.get(getActivity()).getListFragmentPlugins();
        if (listFragmentPlugins != null) {
            for(IXListFragmentPlugin plugin : listFragmentPlugins) {
                plugin.onLoadFinished(this, loader, cursor);
            }
        }
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
	
	protected ViewBinder getAdapterViewBinder() {
		return null;
	}
	
	private boolean isServiceWork = false;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		CursorLoaderFragmentHelper.onActivityCreated(this, savedInstanceState);
		String url = getUrl();
		if (!StringUtil.isEmpty(url)) {
			loadData(getActivity(), url, isForceUpdateData(), null);
		}
        //plugins
        List<IXListFragmentPlugin> listFragmentPlugins = XCoreHelper.get(getActivity()).getListFragmentPlugins();
        if (listFragmentPlugins != null) {
            for(IXListFragmentPlugin plugin : listFragmentPlugins) {
                plugin.onActivityCreated(this, savedInstanceState);
            }
        }
	}

    public void refresh() {
        loadData(getActivity(), getUrl(), true, null);
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
        DataSourceService.execute(context, dataSourceRequest, getProcessorKey(), getDataSourceKey(), new StatusResultReceiver(new Handler(Looper.getMainLooper())) {

            @Override
            public void onAddToQueue(Bundle resultData) {
                isServiceWork = true;
                if (mEndlessScrollListener != null && mEndlessScrollListener.pagingLoading) {
                    showPagingProgress();
                } else {
                    showProgress();
                }
                //plugins
                FragmentActivity activity = getActivity();
                if (activity == null) return;
                List<IXListFragmentPlugin> listFragmentPlugins = XCoreHelper.get(activity).getListFragmentPlugins();
                if (listFragmentPlugins != null) {
                    for (IXListFragmentPlugin plugin : listFragmentPlugins) {
                        plugin.onStatusResultReceiverStart(XListFragment.this, resultData);
                    }
                }
            }

            @Override
            public void onStart(Bundle resultData) {
                //TODO maybe for some status
            }

            @Override
            public void onError(Exception exception) {
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
                if (mEndlessScrollListener != null && mEndlessScrollListener.pagingLoading) {
                    mEndlessScrollListener.pagingLoading = false;
                    hidePagingProgress();
                } else {
                    hideProgressIfLoaderNotStarted();
                }
                //plugins
                List<IXListFragmentPlugin> listFragmentPlugins = XCoreHelper.get(activity).getListFragmentPlugins();
                if (listFragmentPlugins != null) {
                    for (IXListFragmentPlugin plugin : listFragmentPlugins) {
                        plugin.onStatusResultReceiverError(XListFragment.this, exception);
                    }
                }
            }

            @Override
            public void onDone(Bundle resultData) {
                isServiceWork = false;
                FragmentActivity fragmentActivity = getActivity();
                if (fragmentActivity == null) {
                    return;
                }
                if (mEndlessScrollListener != null && mEndlessScrollListener.pagingLoading) {
                    mEndlessScrollListener.pagingLoading = false;
                    hidePagingProgress();
                } else {
                    hideProgressIfLoaderNotStarted();
                }
                //plugins
                List<IXListFragmentPlugin> listFragmentPlugins = XCoreHelper.get(fragmentActivity).getListFragmentPlugins();
                if (listFragmentPlugins != null) {
                    for (IXListFragmentPlugin plugin : listFragmentPlugins) {
                        plugin.onStatusResultReceiverDone(XListFragment.this, resultData);
                    }
                }
            }

            private void hideProgressIfLoaderNotStarted() {
                Loader<Object> loader = getLoaderManager().getLoader(getLoaderId());
                if (loader == null || !loader.isStarted()) {
                    hideProgress();
                }
            }

            @Override
            protected void onCached(Bundle resultData) {
                isServiceWork = false;
                super.onCached(resultData);
                FragmentActivity context = getActivity();
                if (context == null) {
                    return;
                }
                if (mEndlessScrollListener != null && mEndlessScrollListener.pagingLoading) {
                    mEndlessScrollListener.pagingLoading = false;
                    hidePagingProgress();
                } else {
                    hideProgressIfLoaderNotStarted();
                }
                //plugins
                List<IXListFragmentPlugin> listFragmentPlugins = XCoreHelper.get(context).getListFragmentPlugins();
                if (listFragmentPlugins != null) {
                    for (IXListFragmentPlugin plugin : listFragmentPlugins) {
                        plugin.onStatusResultReceiverCached(XListFragment.this, resultData);
                    }
                }
            }

        });
    }

    protected boolean isForceUpdateData() {
		return false;
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		if (getView() != null) {
            ListAdapter adapter = getListView().getAdapter();
            if (adapter instanceof HeaderViewListAdapter) {
                adapter = ((HeaderViewListAdapter) adapter).getWrappedAdapter();
            }
            ((CursorAdapter) adapter).swapCursor(null);
			hideProgress();
		}
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
	public void hideProgress() {
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
		View view = getView();
		if (view == null) {
			return;
		}
		View progressView = view.findViewById(android.R.id.progress);
		if (progressView != null) {
			progressView.setVisibility(View.GONE);
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
        View progressView = view.findViewById(android.R.id.secondaryProgress);
        if (progressView != null) {
            progressView.setVisibility(View.VISIBLE);
        }
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
        View progressView = view.findViewById(android.R.id.secondaryProgress);
        if (progressView != null) {
            progressView.setVisibility(View.GONE);
        }
    }

    @Override
	public void showProgress() {
        ListAdapter listAdapter = getListAdapter();
        if (listAdapter != null && listAdapter.getCount() > 0) {
            return;
        }
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        View view = getView();
		if (view == null) {
			return;
		}
        View progressView = view.findViewById(android.R.id.progress);
		if (progressView != null) {
			progressView.setVisibility(View.VISIBLE);
		}
		hideEmptyView(view);
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

    @Override
    public CursorModel.CursorModelCreator getCursorModelCreator() {
        return CursorModel.CursorModelCreator.DEFAULT;
    }
}
