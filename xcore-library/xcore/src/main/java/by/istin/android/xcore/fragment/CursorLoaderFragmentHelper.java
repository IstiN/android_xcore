package by.istin.android.xcore.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;

import by.istin.android.xcore.model.CursorModel;
import by.istin.android.xcore.model.XCursorModelLoader;
import by.istin.android.xcore.utils.Log;

public class CursorLoaderFragmentHelper {

    public static interface ICursorLoaderFragmentHelper<T extends CursorModel> extends LoaderCallbacks<T> {

        Uri getUri();

        int getLoaderId();

        Activity getActivity();

        String[] getProjection();

        String[] getSelectionArgs();

        String getOrder();

        String getSelection();

        void showProgress();

        void hideProgress();

        CursorModel.CursorModelCreator<T> getCursorModelCreator();

        LoaderManager getSupportLoaderManager();
    }

    public static <T extends CursorModel> XCursorModelLoader<T> createLoader(final ICursorLoaderFragmentHelper<T> cursorLoaderFragment, int id) {
        XCursorModelLoader<T> loader = null;
        if (id == cursorLoaderFragment.getLoaderId()) {
            loader = new XCursorModelLoader<>(
                    cursorLoaderFragment.getActivity(),
                    cursorLoaderFragment.getCursorModelCreator(),
                    cursorLoaderFragment.getUri(),
                    cursorLoaderFragment.getProjection(),
                    cursorLoaderFragment.getSelection(),
                    cursorLoaderFragment.getSelectionArgs(),
                    cursorLoaderFragment.getOrder());
        }
        return loader;
    }

    public static boolean restartLoader(ICursorLoaderFragmentHelper cursorLoaderFragment) {
        if (cursorLoaderFragment.getUri() != null) {
            LoaderManager lm = cursorLoaderFragment.getSupportLoaderManager();
            Log.xd(cursorLoaderFragment, lm);
            if (lm == null) {
                throw new IllegalArgumentException("you need return LoaderManger from activity or fragment in the getSupportLoaderManager method");
            }
            lm.restartLoader(cursorLoaderFragment.getLoaderId(), null, cursorLoaderFragment);
            return true;
        }
        return false;
    }

}
