package by.istin.android.xcore.plugin.actionbarpulltorefresh;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import by.istin.android.xcore.fragment.XListFragment;
import by.istin.android.xcore.plugin.IXListFragmentPlugin;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * Created by IstiN on 29.6.13.
 */
public class ActionBarPullToRefreshListFragmentPlugin implements IXListFragmentPlugin {

    private int pullToRefreshId = -1;

    public ActionBarPullToRefreshListFragmentPlugin(int id) {
        pullToRefreshId = id;
    }

    private PullToRefreshLayout findLayout(View view) {
        if (pullToRefreshId == -1) {
            if (view instanceof PullToRefreshLayout) {
                return (PullToRefreshLayout) view;
            } else {
                return null;
            }
        } else {
            return (PullToRefreshLayout) view.findViewById(pullToRefreshId);
        }
    }

    @Override
    public void onCreateView(final XListFragment listFragment, View view, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Now find the PullToRefreshLayout to setup
        PullToRefreshLayout pullToRefresh = findLayout(view);
        if (pullToRefresh == null) {
            return;
        }
        // Now setup the PullToRefreshLayout
        ActionBarPullToRefresh.from(listFragment.getActivity())
                .theseChildrenArePullable(android.R.id.list)
                .listener(new OnRefreshListener() {
                    @Override
                    public void onRefreshStarted(View view) {
                        if (listFragment != null) {
                            listFragment.refresh();
                        };
                    }
                })
        .setup(pullToRefresh);
    }

    @Override
    public void onCreateLoader(XListFragment listFragment, Loader<Cursor> loader, int id, Bundle args) {

    }

    @Override
    public void onLoadFinished(XListFragment listFragment, Loader<Cursor> loader, Cursor cursor) {

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
        // Now find the PullToRefreshLayout to setup
        PullToRefreshLayout pullToRefresh = findLayout(view);
        if (pullToRefresh == null) {
            return;
        }
        pullToRefresh.setRefreshComplete();
    }

    @Override
    public void onStatusResultReceiverDone(XListFragment listFragment, Bundle resultData) {
        View view = listFragment.getView();
        if (view == null) {
            return;
        }
        // Now find the PullToRefreshLayout to setup
        PullToRefreshLayout pullToRefresh = findLayout(view);
        if (pullToRefresh == null) {
            return;
        }
        pullToRefresh.setRefreshComplete();
    }

    @Override
    public void onStatusResultReceiverCached(XListFragment listFragment, Bundle resultData) {
        View view = listFragment.getView();
        if (view == null) {
            return;
        }
        // Now find the PullToRefreshLayout to setup
        PullToRefreshLayout pullToRefresh = findLayout(view);
        if (pullToRefresh == null) {
            return;
        }
        pullToRefresh.setRefreshComplete();
    }

    @Override
    public boolean setAdapterViewImage(XListFragment listFragment, ImageView v, String value) {
        return false;
    }

    @Override
    public void createAdapter(XListFragment listFragment, SimpleCursorAdapter simpleCursorAdapter, FragmentActivity activity, Cursor cursor) {

    }

}