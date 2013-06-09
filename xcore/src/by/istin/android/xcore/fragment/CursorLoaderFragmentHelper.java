package by.istin.android.xcore.fragment;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

public class CursorLoaderFragmentHelper {

	public static interface ICursorLoaderFragmentHelper extends LoaderCallbacks<Cursor> {
		
		Uri getUri();
		
		int getLoaderId();

		Activity getActivity();

		String[] getProjection();

		String[] getSelectionArgs();

		String getOrder();

		String getSelection();
	}
	
	public static Loader<Cursor> onCreateLoader(ICursorLoaderFragmentHelper cursorLoaderFragment, int id, Bundle args) {
		Loader<Cursor> loader = null;
		if (id == cursorLoaderFragment.getLoaderId()) {
			loader = new CursorLoader(
					cursorLoaderFragment.getActivity(), 
					cursorLoaderFragment.getUri(), 
					cursorLoaderFragment.getProjection(), 
					cursorLoaderFragment.getSelection(), 
					cursorLoaderFragment.getSelectionArgs(), 
					cursorLoaderFragment.getOrder());
		}
		return loader;
	}
	
	public static void onActivityCreated(ICursorLoaderFragmentHelper cursorLoaderFragment, Bundle savedInstanceState) {
		Activity activity = cursorLoaderFragment.getActivity();
		if (activity instanceof FragmentActivity) {
			if (cursorLoaderFragment.getUri() != null) {
				LoaderManager lm = ((FragmentActivity) activity).getSupportLoaderManager();
				lm.restartLoader(cursorLoaderFragment.getLoaderId(), null, cursorLoaderFragment);
			}
		}
	}
	
}
