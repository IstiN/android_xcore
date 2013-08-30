package by.istin.android.xcore.fragment;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;

import by.istin.android.xcore.model.CursorModel;
import by.istin.android.xcore.model.CursorModelLoader;

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
	}
	
	public static Loader<Cursor> onCreateLoader(final ICursorLoaderFragmentHelper cursorLoaderFragment, int id, Bundle args) {
		Loader<Cursor> loader = null;
        final Handler handler = new Handler(Looper.getMainLooper());
		if (id == cursorLoaderFragment.getLoaderId()) {
			loader = new CursorModelLoader(
					cursorLoaderFragment.getActivity(),
                    cursorLoaderFragment.getCursorModelCreator(),
					cursorLoaderFragment.getUri(), 
					cursorLoaderFragment.getProjection(), 
					cursorLoaderFragment.getSelection(), 
					cursorLoaderFragment.getSelectionArgs(), 
					cursorLoaderFragment.getOrder()){
                @Override
                public Cursor loadInBackground() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            cursorLoaderFragment.showProgress();
                        }
                    });
                    Cursor cursor = super.loadInBackground();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            cursorLoaderFragment.hideProgress();
                        }
                    });
                    return cursor;
                }

            };
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
