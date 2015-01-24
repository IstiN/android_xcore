package by.istin.android.xcore.plugin.uil;

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

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import by.istin.android.xcore.fragment.XListFragment;
import by.istin.android.xcore.model.CursorModel;
import by.istin.android.xcore.plugin.IFragmentPlugin;

/**
 * Created by IstiN on 29.6.13.
 */
public class ImageLoaderPlugin implements IFragmentPlugin {

    public ImageLoaderPlugin(ImageLoaderConfiguration configuration) {
        ImageLoader.getInstance().init(configuration);
    }

    @Override
    public void onCreateView(final Fragment fragment, View view, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (fragment instanceof XListFragment) {
            ((XListFragment) fragment).setOnScrollListViewListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
        }
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

    }

    @Override
    public void onStatusResultReceiverDone(Fragment fragment, Bundle resultData) {

    }

    @Override
    public void onStatusResultReceiverCached(Fragment fragment, Bundle resultData) {

    }

    @Override
    public boolean setAdapterViewImage(Fragment fragment, ImageView v, String value) {
        ImageLoader.getInstance().displayImage(value, v);
        return true;
    }

    @Override
    public void createAdapter(Fragment fragment, BaseAdapter baseAdapter, FragmentActivity activity, Cursor cursor) {

    }

}