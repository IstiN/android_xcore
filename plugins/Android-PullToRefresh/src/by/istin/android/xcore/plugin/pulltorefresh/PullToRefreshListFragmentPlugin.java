package by.istin.android.xcore.test.bo;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handmark.pulltorefresh.library.PullToRefreshAdapterViewBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

import by.istin.android.xcore.fragment.XListFragment;
import by.istin.android.xcore.plugin.IXListFragmentPlugin;

/**
 * Created by IstiN on 29.6.13.
 */
public class PullToRefreshListFragmentPlugin implements IXListFragmentPlugin {

    private int pullToRefreshId;

    public PullToRefreshListFragmentPlugin(int pullToRefreshId) {
        this.pullToRefreshId = pullToRefreshId;
    }

    @Override
    public void onCreateView(final XListFragment listFragment, View view, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        PullToRefreshAdapterViewBase pullToRefresh = (PullToRefreshAdapterViewBase) view.findViewById(pullToRefreshId);
        pullToRefresh.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {

            @Override
            public void onRefresh(PullToRefreshBase refreshView) {
                listFragment.refresh();
            }

        });
    }

    @Override
    public void onCreateLoader(XListFragment listFragment, Loader<Cursor> loader, int id, Bundle args) {

    }

    @Override
    public void onLoadFinished(XListFragment listFragment, android.support.v4.content.Loader<Cursor> loader, Cursor cursor) {

    }

    @Override
    public void onActivityCreated(XListFragment listFragment, Bundle savedInstanceState) {

    }

    @Override
    public void onStatusResultReceiverStart(XListFragment listFragment, Bundle resultData) {

    }

    @Override
    public void onStatusResultReceiverError(XListFragment listFragment, Exception exception) {
        View view = listFragment.getView();
        if (view == null) {
            return;
        }
        PullToRefreshAdapterViewBase pullToRefresh = (PullToRefreshAdapterViewBase) view.findViewById(pullToRefreshId);
        pullToRefresh.onRefreshComplete();
    }

    @Override
    public void onStatusResultReceiverDone(XListFragment listFragment, Bundle resultData) {
        View view = listFragment.getView();
        if (view == null) {
            return;
        }
        PullToRefreshAdapterViewBase pullToRefresh = (PullToRefreshAdapterViewBase) view.findViewById(pullToRefreshId);
        pullToRefresh.onRefreshComplete();
    }

    @Override
    public void onStatusResultReceiverCached(XListFragment listFragment, Bundle resultData) {
        View view = listFragment.getView();
        if (view == null) {
            return;
        }
        PullToRefreshAdapterViewBase pullToRefresh = (PullToRefreshAdapterViewBase) view.findViewById(pullToRefreshId);
        pullToRefresh.onRefreshComplete();
    }

    @Override
    public void createAdapter(XListFragment listFragment, SimpleCursorAdapter simpleCursorAdapter, FragmentActivity activity, Cursor cursor) {

    }

}