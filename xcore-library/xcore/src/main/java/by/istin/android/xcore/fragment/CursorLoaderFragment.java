package by.istin.android.xcore.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import by.istin.android.xcore.fragment.CursorLoaderFragmentHelper.ICursorLoaderFragmentHelper;
import by.istin.android.xcore.model.CursorModel;

public abstract class CursorLoaderFragment extends Fragment implements ICursorLoaderFragmentHelper {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(getViewLayout(), container, false);
        onViewCreated(view);
        return view;
    }

    protected void onViewCreated(View view) {

    }

    protected abstract int getViewLayout();


    public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
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