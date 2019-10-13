package by.istin.android.xcore.plugin;

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

import by.istin.android.xcore.model.CursorModel;


/**
 * Created by IstiN on 28.6.13.
 */
public interface IFragmentPlugin {

    void onCreateView(Fragment fragment, View view, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    void onCreateLoader(Fragment fragment, Loader loader, int id, Bundle args);

    void onLoadFinished(Fragment fragment, Loader loader, CursorModel cursor);

    void createAdapter(Fragment fragment, BaseAdapter baseAdapter, FragmentActivity activity, Cursor cursor);

    void onActivityCreated(Fragment fragment, Bundle savedInstanceState);

    void onStatusResultReceiverStart(Fragment fragment, Bundle resultData);

    void onStatusResultReceiverError(Fragment fragment, Exception exception);

    void onStatusResultReceiverDone(Fragment fragment, Bundle resultData);

    void onStatusResultReceiverCached(Fragment fragment, Bundle resultData);

    boolean setAdapterViewImage(Fragment fragment, ImageView v, String value);

}