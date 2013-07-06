package by.istin.android.xcore.plugin;

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

/**
 * Created by IstiN on 28.6.13.
 */
public interface IXListFragmentPlugin {

    void onCreateView(XListFragment listFragment, View view, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    void onCreateLoader(XListFragment listFragment, Loader<Cursor> loader, int id, Bundle args);

    void onLoadFinished(XListFragment listFragment, Loader<Cursor> loader, Cursor cursor);

    void createAdapter(XListFragment listFragment, SimpleCursorAdapter simpleCursorAdapter, FragmentActivity activity, Cursor cursor);

    void onActivityCreated(XListFragment listFragment, Bundle savedInstanceState);

    void onStatusResultReceiverStart(XListFragment listFragment, Bundle resultData);

    void onStatusResultReceiverError(XListFragment listFragment, Exception exception);

    void onStatusResultReceiverDone(XListFragment listFragment, Bundle resultData);

    void onStatusResultReceiverCached(XListFragment listFragment, Bundle resultData);

    boolean setAdapterViewImage(XListFragment listFragment, ImageView v, String value);
}