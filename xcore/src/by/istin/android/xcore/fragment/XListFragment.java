package by.istin.android.xcore.fragment;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
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
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import by.istin.android.xcore.service.DataSourceService;
import by.istin.android.xcore.service.StatusResultReceiver;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.impl.http.HttpAndroidDataSource;
import by.istin.android.xcore.utils.StringUtil;

public abstract class XListFragment extends ListFragment implements LoaderCallbacks<Cursor> {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(getViewLayout(), container, false);
		onViewCreated(view);
		Integer searchEditTextId = getSearchEditTextId();
		if (searchEditTextId == null) {
			return view;
		}
		final EditText etext= (EditText) view.findViewById(getSearchEditTextId());
		Integer searchHintText = getSearchHintText();
		if (searchHintText != null) {
			etext.setHint(searchHintText);
		}
		final Integer searchEditTextClearId = getSearchEditTextClearId();
		if (searchEditTextClearId != null) {
			View searchClear = view.findViewById(searchEditTextClearId);
			if (searchClear != null) {
				searchClear.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View paramView) {
						etext.setText(StringUtil.EMPTY);
					}
					
				});
			}
		}
	    etext.addTextChangedListener(new TextWatcher() {
	    	
	    	private View searchClear;
	    	
	        public void onTextChanged(CharSequence s, int start, int before, int count) {
	        	
	        }

	        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	        	
	        }

	        public void afterTextChanged(Editable s) {
	            ListView av = (ListView)view.findViewById(android.R.id.list);
	            ListAdapter adapter = av.getAdapter();
	            if (adapter instanceof HeaderViewListAdapter) {
	            	adapter = ((HeaderViewListAdapter)adapter).getWrappedAdapter();
	            }
				SimpleCursorAdapter filterAdapter = (SimpleCursorAdapter)adapter;
	            String value = s.toString();
	            if (searchEditTextClearId != null) {
	            	if (searchClear == null) {
	            		searchClear = view.findViewById(searchEditTextClearId);
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
	        
	    });
		return view;
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
		Loader<Cursor> loader = null;
		if (id == getUri().hashCode()) {
			loader = new CursorLoader(getActivity(), getUri(), getProjection(), getSelection(), getSelectionArgs(), getOrder());
		}
		return loader;
	}
	
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		hideProgress();
		ListAdapter adapter = getListAdapter();
		if (adapter == null || !(adapter instanceof CursorAdapter)) {
			SimpleCursorAdapter cursorAdapter = createAdapter(cursor);
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
	}

	protected SimpleCursorAdapter createAdapter(Cursor cursor) {
		return new SimpleCursorAdapter(getActivity(), getAdapterLayout(), cursor, getAdapterColumns(), getAdapterControlIds(), 0){

			@Override
			public void setViewImage(ImageView v, String value) {
				if (!setAdapterViewImage(v, value)) {
					super.setViewImage(v, value);
				}
			}
			
		};
	}
	
	protected abstract String[] getAdapterColumns();
	protected abstract int[] getAdapterControlIds();
	protected abstract int getAdapterLayout();

	protected boolean setAdapterViewImage(ImageView v, String value) {
		return false;
	}
	
	protected ViewBinder getAdapterViewBinder() {
		return null;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Activity activity = getActivity();
		if (activity instanceof FragmentActivity) {
			LoaderManager lm = ((FragmentActivity) activity).getSupportLoaderManager();
			lm.restartLoader(getUri().hashCode(), null, this);
		}
		DataSourceRequest dataSourceRequest = new DataSourceRequest(getUrl());
		dataSourceRequest.setCacheable(isCacheable());
		dataSourceRequest.setCacheExpiration(getCacheExpiration());
		dataSourceRequest.setForceUpdateData(isForceUpdateData());
		DataSourceService.execute(activity, dataSourceRequest, getProcessorKey(), getDataSourceKey(), new StatusResultReceiver(new Handler(Looper.getMainLooper())) {
			
			@Override
			public void onStart(Bundle resultData) {
				showProgress();
			}

			@Override
			public void onError(Exception exception) {
				exception.printStackTrace();
				Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_SHORT).show();
				hideProgress();
			}
			
			@Override
			public void onDone(Bundle resultData) {
				FragmentActivity fragmentActivity = getActivity();
				if (fragmentActivity == null) {
					return;
				}
				Toast.makeText(fragmentActivity, "done", Toast.LENGTH_SHORT).show();
				hideProgress();
			}

			@Override
			protected void onCached(Bundle resultData) {
				super.onCached(resultData);
				hideProgress();
			}
			
		});
	}
	
	protected boolean isForceUpdateData() {
		return false;
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		if (getView() != null) {
			((CursorAdapter)getListView().getAdapter()).swapCursor(null);
			hideProgress();
		}
	}
	
	protected String getDataSourceKey() {
		return HttpAndroidDataSource.SYSTEM_SERVICE_KEY;
	}

	protected long getCacheExpiration() {
		return DateUtils.HOUR_IN_MILLIS;
	}

	protected boolean isCacheable() {
		return true;
	}
	
	protected void hideProgress() {
		View view = getView();
		if (view == null) {
			return;
		}
		View progressView = view.findViewById(android.R.id.progress);
		if (progressView != null) {
			progressView.setVisibility(View.GONE);
		}
	}
	
	protected void showProgress() {
		View view = getView();
		if (view == null) {
			return;
		}
		View progressView = view.findViewById(android.R.id.progress);
		if (progressView != null) {
			progressView.setVisibility(View.VISIBLE);
		}
	}
}
