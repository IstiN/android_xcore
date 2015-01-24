package by.istin.android.xcore.fragment;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import by.istin.android.xcore.error.IErrorHandler;
import by.istin.android.xcore.fragment.CursorLoaderFragmentHelper.ICursorLoaderFragmentHelper;
import by.istin.android.xcore.model.CursorModel;
import by.istin.android.xcore.service.DataSourceService;
import by.istin.android.xcore.service.StatusResultReceiver;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.impl.http.HttpAndroidDataSource;
import by.istin.android.xcore.utils.AppUtils;
import by.istin.android.xcore.utils.CursorUtils;
import by.istin.android.xcore.utils.ResponderUtils;
import by.istin.android.xcore.utils.StringUtil;

public abstract class XFragment<T extends CursorModel> extends Fragment implements IRefresh, ICursorLoaderFragmentHelper<T>,IDataSourceHelper,
        DataSourceExecuteHelper.IDataSourceListener {

	@Override
	public int getLoaderId() {
		return getUri().hashCode();
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = getActivity().getLayoutInflater().inflate(getViewLayout(), container, false);
        onViewCreated(view);
		return view;
	}

    @Override
    public void setServiceWork(boolean isWork) {

    }

    @Override
    public void onError(Exception e, DataSourceRequest dataSourceRequest) {

    }

    @Override
    public void onReceiverOnCached(Bundle resultData) {

    }

    @Override
    public void onReceiverOnDone(Bundle resultData) {

    }

    @Override
    public DataSourceExecuteHelper.IDataSourceListener getDataSourceListener() {
        return this;
    }

	public void onViewCreated(View view) {
		
	}

	public abstract int getViewLayout();
	


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
    public LoaderManager getSupportLoaderManager() {
        return getLoaderManager();
    }

    @Override
	public Loader<T> onCreateLoader(int id, Bundle args) {
        return CursorLoaderFragmentHelper.createLoader(this, id);
	}

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        IFragmentViewCreated fragmentViewCreated = findFirstResponderFor(IFragmentViewCreated.class);
        if (fragmentViewCreated != null) {
            fragmentViewCreated.onFragmentViewCreated(this);
        }
    }

    @Override
	public void onLoadFinished(Loader<T> loader, T cursor) {
		FragmentActivity activity = getActivity();
		if (activity == null) {
			return;
		}
		onLoadFinished(cursor);
        View view = getView();
        if (view == null) {
            return;
        }
        if (isServiceWork) {
			hideEmptyView(view);
		}
        if (CursorUtils.isEmpty(cursor)) {
            return;
        }
        cursor.moveToFirst();
        int[] adapterControlIds = getAdapterControlIds();
        String[] adapterColumns = getAdapterColumns();
        final int count = adapterControlIds.length;

        for (int i = 0; i < count; i++) {
            final View v = view.findViewById(adapterControlIds[i]);
            if (v != null) {
                String text = CursorUtils.getString(adapterColumns[i], cursor);
                if (text == null) {
                    text = "";
                }
                if (v instanceof TextView) {
                    setViewText((TextView) v, text);
                } else if (v instanceof ImageView) {
                    setViewImage((ImageView) v, text);
                } else {
                    throw new IllegalStateException(v.getClass().getName() + " is not a " +
                            " view that can be bounds by this SimpleCursorAdapter");
                }
            }
       }
    }

    protected void setViewImage(ImageView v, String text) {
        //TODO plugins
    }

    protected void setViewText(TextView v, String text) {
        if (StringUtil.isEmpty(text)) {
            v.setText(StringUtil.EMPTY);
        } else {
            v.setText(text);
        }
    }

    protected abstract void onLoadFinished(Cursor cursor);

    protected abstract String[] getAdapterColumns();

	protected abstract int[] getAdapterControlIds();

	private boolean isServiceWork = false;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		CursorLoaderFragmentHelper.restartLoader(this);
		String url = getUrl();
		if (!StringUtil.isEmpty(url)) {
			loadData(getActivity(), url, isForceUpdateData());
		}
	}

    public void refresh() {
        loadData(getActivity(), getUrl(), true);
    }

    protected void loadData(Activity activity, String url) {
        loadData(activity, url, isForceUpdateData());
    }

    @Override
    public void dataSourceExecute(Context context, final DataSourceRequest dataSourceRequest) {
        String processorKey = getProcessorKey();
        String dataSourceKey = getDataSourceKey();
        dataSourceExecute(context, dataSourceRequest, processorKey, dataSourceKey);
    }

    protected void dataSourceExecute(Context context, final DataSourceRequest dataSourceRequest, String processorKey, String dataSourceKey) {
        DataSourceService.execute(context, dataSourceRequest, processorKey, dataSourceKey, new StatusResultReceiver(new Handler(Looper.getMainLooper())) {

            @Override
            public void onStart(Bundle resultData) {
                isServiceWork = true;
            }

            @Override
            public void onError(Exception exception) {
                isServiceWork = false;
                exception.printStackTrace();
                FragmentActivity activity = getActivity();
                if (activity == null) {
                    return;
                }
                IErrorHandler errorHandler = AppUtils.get(activity, IErrorHandler.SYSTEM_SERVICE_KEY);
                errorHandler.onError(activity, XFragment.this, dataSourceRequest, exception);
                hideProgress();
            }

            @Override
            public void onDone(Bundle resultData) {
                isServiceWork = false;
                FragmentActivity fragmentActivity = getActivity();
                if (fragmentActivity == null) {
                    return;
                }
                hideProgress();
                onReceiverOnDone(resultData);
            }

            @Override
            protected void onCached(Bundle resultData) {
                isServiceWork = false;
                onReceiverOnCached(resultData);
                super.onCached(resultData);
                hideProgress();
            }

        });
    }

    protected void loadData(Activity activity, String url, Boolean isForceUpdate) {
		final DataSourceRequest dataSourceRequest = new DataSourceRequest(url);
        loadData(activity, isForceUpdate, dataSourceRequest);
	}

    protected void loadData(Activity activity, Boolean isForceUpdate, DataSourceRequest dataSourceRequest) {
        dataSourceRequest.setCacheable(isCacheable());
        dataSourceRequest.setCacheExpiration(getCacheExpiration());
        dataSourceRequest.setForceUpdateData(isForceUpdate);
        dataSourceExecute(activity, dataSourceRequest);
    }

    protected boolean isForceUpdateData() {
		return false;
	}

	@Override
	public void onLoaderReset(Loader<T> loader) {
		if (getView() != null) {
			onLoaderReset();
			hideProgress();
		}
	}

    protected abstract void onLoaderReset();

    public String getDataSourceKey() {
		return HttpAndroidDataSource.SYSTEM_SERVICE_KEY;
	}

	public long getCacheExpiration() {
		return DateUtils.HOUR_IN_MILLIS;
	}

	public boolean isCacheable() {
		return true;
	}

    @Override
	public void hideProgress() {
		View view = getView();
		if (view == null) {
			return;
		}
		View progressView = view.findViewById(android.R.id.progress);
		if (progressView != null) {
			progressView.setVisibility(View.GONE);
		}
	}

    @Override
	public void showProgress() {
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
		if (view == null) return;
		View emptyView = view.findViewById(android.R.id.empty);
		if (emptyView != null) {
			emptyView.setVisibility(View.GONE);
		}
	}

    @Override
    public CursorModel.CursorModelCreator getCursorModelCreator() {
        return CursorModel.CursorModelCreator.DEFAULT;
    }

    protected <T> T findFirstResponderFor(Class<T> clazz) {
        return ResponderUtils.findFirstResponderFor(this, clazz);
    }

}
