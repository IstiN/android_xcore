package by.istin.android.xcore.plugin.actionbarpulltorefresh;

import android.database.Cursor;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import by.istin.android.xcore.fragment.IRefresh;
import by.istin.android.xcore.model.CursorModel;
import by.istin.android.xcore.plugin.IFragmentPlugin;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * Created by IstiN on 29.6.13.
 */
public class ActionBarPullToRefreshFragmentPlugin implements IFragmentPlugin {

    private int pullToRefreshId = -1;

    public ActionBarPullToRefreshFragmentPlugin(int id) {
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
    public void onCreateView(final Fragment fragment, View view, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Now find the PullToRefreshLayout to setup
        PullToRefreshLayout pullToRefresh = findLayout(view);
        if (pullToRefresh == null) {
            return;
        }
        // Now setup the PullToRefreshLayout
        ActionBarPullToRefresh.from(fragment.getActivity())
                .theseChildrenArePullable(android.R.id.list)
                .listener(new OnRefreshListener() {
                    @Override
                    public void onRefreshStarted(View view) {
                        if (fragment != null && fragment instanceof IRefresh) {
                            ((IRefresh) fragment).refresh();
                        }
                        ;
                    }
                })
                .setup(pullToRefresh);
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
        // Now find the PullToRefreshLayout to setup
        PullToRefreshLayout pullToRefresh = findLayout(view);
        if (pullToRefresh == null) {
            return;
        }
        pullToRefresh.setRefreshComplete();
    }

    @Override
    public void onStatusResultReceiverDone(Fragment fragment, Bundle resultData) {
        View view = fragment.getView();
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
    public void onStatusResultReceiverCached(Fragment fragment, Bundle resultData) {
        View view = fragment.getView();
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
    public boolean setAdapterViewImage(Fragment fragment, ImageView v, String value) {
        return false;
    }

    @Override
    public void createAdapter(Fragment fragment, BaseAdapter baseAdapter, FragmentActivity activity, Cursor cursor) {

    }

}