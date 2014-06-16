package by.istin.android.xcore.widget;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import by.istin.android.xcore.utils.CursorUtils;

public abstract class ViewPagerCursorAdapter extends PagerAdapter {

    private final ChangeObserver mChangeObserver;
    private final MyDataSetObserver mDataSetObserver;

    private Cursor mCursor;

    private int mCount;

	private final Context mContext;
	
	private final int mResource;

    private boolean mDataValid = false;

    public ViewPagerCursorAdapter(Context ctx, Cursor cursor, int resource) {
		super();
		this.mCursor = cursor;
		this.mContext = ctx;
		this.mResource = resource;
        mChangeObserver = new ChangeObserver();
        mDataSetObserver = new MyDataSetObserver();
        if (cursor != null) {
            mDataValid = true;
            cursor.registerContentObserver(mChangeObserver);
            cursor.registerDataSetObserver(mDataSetObserver);
            registerDataSetObserver(mDataSetObserver);
            mCount = cursor.getCount();
        } else {
            mCount = 0;
        }

	}

	@Override
	public int getCount() {
        return mCount;
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
        if (!mDataValid || CursorUtils.isClosed(mCursor)) {
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

	public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            mCount = newCursor.getCount();
            notifyDataSetChanged();
            return newCursor;
        }
        Cursor oldCursor = mCursor;
        if (oldCursor != null) {
            oldCursor.unregisterContentObserver(mChangeObserver);
            oldCursor.unregisterDataSetObserver(mDataSetObserver);
            unregisterDataSetObserver(mDataSetObserver);
        }
        mCursor = newCursor;
        if (newCursor != null && !CursorUtils.isClosed(mCursor)) {
            newCursor.registerContentObserver(mChangeObserver);
            newCursor.registerDataSetObserver(mDataSetObserver);
            registerDataSetObserver(mDataSetObserver);
            // notify the observers about the new cursor
            mDataValid = true;
            mCount = newCursor.getCount();
            notifyDataSetChanged();
        } else {
            try {
                unregisterDataSetObserver(mDataSetObserver);
            } catch (IllegalStateException e) {

            }
            mCount = 0;
            mDataValid = false;
            notifyDataSetChanged();
        }
        return oldCursor;
	}

    /**
     * Called when the {@link ContentObserver} on the cursor receives a change notification.
     * The default implementation provides the auto-requery logic, but may be overridden by
     * sub classes.
     *
     * @see ContentObserver#onChange(boolean)
     */
    protected void onContentChanged() {
        if (mCursor != null && !mCursor.isClosed()) {
            mDataValid = mCursor.requery();
        }
    }

    private class ChangeObserver extends ContentObserver {
        public ChangeObserver() {
            super(new Handler());
        }

        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }

        @Override
        public void onChange(boolean selfChange) {
            onContentChanged();
        }
    }

    private class MyDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            mDataValid = true;
        }

        @Override
        public void onInvalidated() {
            mDataValid = false;
        }
    }
}