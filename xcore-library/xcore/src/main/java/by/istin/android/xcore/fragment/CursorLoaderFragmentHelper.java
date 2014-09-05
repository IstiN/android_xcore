package by.istin.android.xcore.fragment;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import by.istin.android.xcore.model.CursorModel;
import by.istin.android.xcore.model.CursorModelLoader;
import by.istin.android.xcore.utils.Log;

public class CursorLoaderFragmentHelper {

	public static interface ICursorLoaderFragmentHelper extends LoaderCallbacks<Cursor> {
		
		Uri getUri();
		
		int getLoaderId();

		Activity getActivity();

		String[] getProjection();

		String[] getSelectionArgs();

		String getOrder();

		String getSelection();

        void showProgress();

        void hideProgress();

        CursorModel.CursorModelCreator getCursorModelCreator();

        LoaderManager getSupportLoaderManager();
	}

    public static Loader<Cursor> onCreateLoader(final ICursorLoaderFragmentHelper cursorLoaderFragment, int id, Bundle args) {
        return onCreateLoader(cursorLoaderFragment, null, id, args);
    }

	public static Loader<Cursor> onCreateLoader(final ICursorLoaderFragmentHelper cursorLoaderFragment, CursorModelLoader.ILoading loading, int id, Bundle args) {
		Loader<Cursor> loader = null;
		if (id == cursorLoaderFragment.getLoaderId()) {
			loader = new CursorModelLoader(
					cursorLoaderFragment.getActivity(),
                    cursorLoaderFragment.getCursorModelCreator(),
                    loading,
					cursorLoaderFragment.getUri(),
					cursorLoaderFragment.getProjection(), 
					cursorLoaderFragment.getSelection(), 
					cursorLoaderFragment.getSelectionArgs(), 
					cursorLoaderFragment.getOrder());
		}
		return loader;
	}

	public static boolean onActivityCreated(ICursorLoaderFragmentHelper cursorLoaderFragment, Bundle savedInstanceState) {
		Activity activity = cursorLoaderFragment.getActivity();
		if (activity instanceof FragmentActivity) {
			if (cursorLoaderFragment.getUri() != null) {
                LoaderManager lm = cursorLoaderFragment.getSupportLoaderManager();
                if (lm == null) {
                    if (cursorLoaderFragment instanceof FragmentActivity){
                        lm = ((FragmentActivity) activity).getSupportLoaderManager();
                    } else {
                        Log.xe("lm", "loader manager is not specified");
                    }
                }
                Log.xd("lm", lm);
                lm.restartLoader(cursorLoaderFragment.getLoaderId(), null, cursorLoaderFragment);
                return true;
			}
		}
        return false;
	}
	
}
