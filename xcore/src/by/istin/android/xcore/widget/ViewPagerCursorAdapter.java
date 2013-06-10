package by.istin.android.xcore.widget;

import android.content.Context;
import android.database.Cursor;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

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

	/**
	 * Create the page for the given position. The adapter is responsible
	 * for adding the view to the container given here, although it only
	 * must ensure this is done by the time it returns from
	 * {@link #finishUpdate()}.
	 * 
	 * @param container
	 *            The containing View in which the page will be shown.
	 * @param position
	 *            The page position to be instantiated.
	 * @return Returns an Object representing the new page. This does not
	 *         need to be a View, but can be some other container of the
	 *         page.
	 */
	@Override
	public Object instantiateItem(View collection, int position) {
		final View container = View.inflate(context, getResource(position), null);
		Cursor cursor = getItemAtPosition(position);
		init(container, cursor);
		((ViewPager)collection).addView(container, 0);
		return container;
	}

	public Cursor getItemAtPosition(int position) {
		cursor.moveToPosition(position);
		return cursor;
	}

	public int getResource(int position) {
	      return resource;
	};
	   
	public abstract void init(View container, Cursor cursor);

	/**
	 * Remove a page for the given position. The adapter is responsible for
	 * removing the view from its container, although it only must ensure
	 * this is done by the time it returns from {@link #finishUpdate()}.
	 * 
	 * @param container
	 *            The containing View from which the page will be removed.
	 * @param position
	 *            The page position to be removed.
	 * @param object
	 *            The same object that was returned by
	 *            {@link #instantiateItem(View, int)}.
	 */
	@Override
	public void destroyItem(View collection, int position, Object view) {
		((ViewPager) collection).removeView((View) view);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == (object);
	}

	/**
	 * Called when the a change in the shown pages has been completed. At
	 * this point you must ensure that all of the pages have actually been
	 * added or removed from the container as appropriate.
	 * 
	 * @param container
	 *            The containing View which is displaying this adapter's
	 *            page views.
	 */
	@Override
	public void finishUpdate(View arg0) {
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {
	}

	public void swapCursor(Cursor newCursor) {
		this.cursor = newCursor;
		notifyDataSetChanged();
	}

}