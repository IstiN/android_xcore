package by.istin.android.xcore.widget;

import android.content.Context;
import android.database.Cursor;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public abstract class ViewPagerCursorAdapter extends PagerAdapter {

	private Cursor cursor;
	
	private Context context;
	
	private int resource;
	
	public ViewPagerCursorAdapter(Context ctx, Cursor cursor, int resource) {
		super();
		this.cursor = cursor;
		this.context = ctx;
		this.resource = resource;
	}

	@Override
	public int getCount() {
		return cursor.getCount();
	}


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final View containerItem = View.inflate(context, getResource(position), null);
        Cursor cursor = getItemAtPosition(position);
        init(containerItem, cursor);
        container.addView(containerItem, 0);
        return containerItem;
    }

    public Cursor getItemAtPosition(int position) {
		cursor.moveToPosition(position);
		return cursor;
	}

	public int getResource(int position) {
	      return resource;
	};
	   
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
		this.cursor = newCursor;
		notifyDataSetChanged();
	}

}