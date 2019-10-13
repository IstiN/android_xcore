package by.istin.android.xcore.plugin.picasso;

import android.content.Context;
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

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import by.istin.android.xcore.model.CursorModel;
import by.istin.android.xcore.plugin.IFragmentPlugin;
import by.istin.android.xcore.utils.StringUtil;

public abstract class ImageLoaderPlugin implements IFragmentPlugin {

    private Picasso picasso;

    public ImageLoaderPlugin(Context context) {
        this(Picasso.with(context));
    }

    public ImageLoaderPlugin(Picasso picasso) {
        this.picasso = picasso;
    }

    @Override
    public void onCreateView(final Fragment fragment, View view, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

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
        if (StringUtil.isEmpty(value)) {
            value = null;
        }
        RequestCreator requestCreator = picasso.load(value);
        onRequestCreated(requestCreator);
        requestCreator.into(v);
        return true;
    }

    public abstract void onRequestCreated(RequestCreator requestCreator);

    @Override
    public void createAdapter(Fragment fragment, BaseAdapter baseAdapter, FragmentActivity activity, Cursor cursor) {

    }

    @Override
    public void onCreateLoader(Fragment fragment, Loader loader, int id, Bundle args) {

    }

    @Override
    public void onLoadFinished(Fragment fragment, Loader loader, CursorModel cursor) {

    }
}