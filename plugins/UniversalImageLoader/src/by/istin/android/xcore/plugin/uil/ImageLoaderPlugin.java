package by.istin.android.xcore.test.bo;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

import by.istin.android.xcore.fragment.XListFragment;
import by.istin.android.xcore.plugin.IXListFragmentPlugin;

/**
 * Created by IstiN on 29.6.13.
 */
public class ImageLoaderPlugin implements IXListFragmentPlugin {

    public ImageLoaderPlugin(ImageLoaderConfiguration configuration) {
        ImageLoader.getInstance().init(configuration);
    }

    @Override
    public void onCreateView(final XListFragment listFragment, View view, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        listFragment.setOnScrollListViewListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
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

    }

    @Override
    public void onStatusResultReceiverDone(XListFragment listFragment, Bundle resultData) {
        View view = listFragment.getView();
        if (view == null) {
            return;
        }

    }

    @Override
    public void onStatusResultReceiverCached(XListFragment listFragment, Bundle resultData) {
        View view = listFragment.getView();
        if (view == null) {
            return;
        }

    }

    @Override
    public boolean setAdapterViewImage(XListFragment listFragment, ImageView v, String value) {
        ImageLoader.getInstance().displayImage(value, v);
        return true;
    }

    @Override
    public void createAdapter(XListFragment listFragment, SimpleCursorAdapter simpleCursorAdapter, FragmentActivity activity, Cursor cursor) {

    }

}