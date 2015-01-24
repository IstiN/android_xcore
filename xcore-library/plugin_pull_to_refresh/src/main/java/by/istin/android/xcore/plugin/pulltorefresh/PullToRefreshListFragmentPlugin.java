package by.istin.android.xcore.plugin.pulltorefresh;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.handmark.pulltorefresh.library.PullToRefreshAdapterViewBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

import by.istin.android.xcore.fragment.IRefresh;
import by.istin.android.xcore.model.CursorModel;
import by.istin.android.xcore.plugin.IFragmentPlugin;

/**
 * Created by IstiN on 29.6.13.
 */
public class PullToRefreshListFragmentPlugin implements IFragmentPlugin {

    private int pullToRefreshId;

    public PullToRefreshListFragmentPlugin(int pullToRefreshId) {
        this.pullToRefreshId = pullToRefreshId;
    }

    @Override
    public void onCreateView(final Fragment fragment, View view, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        PullToRefreshAdapterViewBase pullToRefresh = (PullToRefreshAdapterViewBase) view.findViewById(pullToRefreshId);
        if (pullToRefresh == null) {
            return;
        }
        pullToRefresh.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {

            @Override
            public void onRefresh(PullToRefreshBase refreshView) {
                if (fragment != null && fragment instanceof IRefresh) {
                    ((IRefresh)fragment).refresh();
                }
            }

        });
    }

    @Override
    public void onCreateLoader(Fragment fragment, Loader loader, int id, Bundle args) {

    }

    @Override
    public void onLoadFinished(Fragment fragment, Loader loader, CursorModel cursor) {

    }

    @Override
    public void onActivityCreated(Fragment fragment, Bundle savedInstanceState) {

    }

    @Override
    public void onStatusResultReceiverStart(Fragment fragment, Bundle resultData) {

    }

    @Override
    public void onStatusResultReceiverError(Fragment fragment, Exception exception) {
        View view = fragment.getView();
        if (view == null) {
            return;
        }
        PullToRefreshAdapterViewBase pullToRefresh = (PullToRefreshAdapterViewBase) view.findViewById(pullToRefreshId);
        if (pullToRefresh == null) {
            return;
        }
        pullToRefresh.onRefreshComplete();
    }

    @Override
    public void onStatusResultReceiverDone(Fragment fragment, Bundle resultData) {
        View view = fragment.getView();
        if (view == null) {
            return;
        }
        PullToRefreshAdapterViewBase pullToRefresh = (PullToRefreshAdapterViewBase) view.findViewById(pullToRefreshId);
        if (pullToRefresh == null) {
            return;
        }
        pullToRefresh.onRefreshComplete();
    }

    @Override
    public void onStatusResultReceiverCached(Fragment fragment, Bundle resultData) {
        View view = fragment.getView();
        if (view == null) {
            return;
        }
        PullToRefreshAdapterViewBase pullToRefresh = (PullToRefreshAdapterViewBase) view.findViewById(pullToRefreshId);
        if (pullToRefresh == null) {
            return;
        }
        pullToRefresh.onRefreshComplete();
    }

    @Override
    public boolean setAdapterViewImage(Fragment fragment, ImageView v, String value) {
        return false;
    }

    @Override
    public void createAdapter(Fragment fragment, BaseAdapter simpleCursorAdapter, FragmentActivity activity, Cursor cursor) {

    }

}