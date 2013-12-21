package by.istin.android.xcore.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import by.istin.android.xcore.fragment.CursorLoaderFragmentHelper.ICursorLoaderFragmentHelper;
import by.istin.android.xcore.model.CursorModel;

public abstract class CursorLoaderFragment extends Fragment implements ICursorLoaderFragmentHelper {

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		restart(savedInstanceState);
	}

	public void restart(Bundle savedInstanceState) {
		CursorLoaderFragmentHelper.onActivityCreated(this, savedInstanceState);
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return CursorLoaderFragmentHelper.onCreateLoader(this, id, args);
	}

	@Override
	public String[] getProjection() {
		return null;
	}

	@Override
	public String[] getSelectionArgs() {
		return null;
	}

	@Override
	public String getOrder() {
		return null;
	}

	@Override
	public String getSelection() {
		return null;
	}

    @Override
    public CursorModel.CursorModelCreator getCursorModelCreator() {
        return CursorModel.CursorModelCreator.DEFAULT;
    }
}