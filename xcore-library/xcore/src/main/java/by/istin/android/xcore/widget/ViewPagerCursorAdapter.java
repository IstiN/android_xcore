package by.istin.android.xcore.widget;

import android.content.Context;
import android.database.Cursor;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import by.istin.android.xcore.utils.CursorUtils;

public abstract class ViewPagerCursorAdapter extends PagerAdapter {

	private Cursor mCursor;
	
	private final Context mContext;
	
	private final int mResource;
	
	public ViewPagerCursorAdapter(Context ctx, Cursor cursor, int resource) {
		super();
		this.mCursor = cursor;
		this.mContext = ctx;
		this.mResource = resource;
	}

	@Override
	public int getCount() {
        if (CursorUtils.isClosed(mCursor)) {
            return 0;
        }
		return mCursor.getCount();
	}


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final View containerItem = View.inflate(mContext, getResource(position), null);
        onViewItemCreated(containerItem);
        Cursor cursor = getItemAtPosition(position);
        if (cursor != null) {
            init(containerItem, cursor);
        }
        container.addView(containerItem, 0);
        return containerItem;
    }

    protected void onViewItemCreated(View containerItem) {

    }

    public Cursor getItemAtPosition(int position) {
        if (CursorUtils.isClosed(mCursor)) {
            return null;
        }
		mCursor.moveToPosition(position);
		return mCursor;
	}

	public int getResource(int position) {
	      return mResource;
	}
	   
	public abstract void init(View container, Cursor cursor);


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
	public boolean isViewFromObject(View view, Object object) {
		return view == (object);
	}


	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	public void swapCursor(Cursor newCursor) {
		this.mCursor = newCursor;
		notifyDataSetChanged();
	}

}