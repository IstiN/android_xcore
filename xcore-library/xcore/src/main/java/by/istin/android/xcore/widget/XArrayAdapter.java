package by.istin.android.xcore.widget;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

public abstract class XArrayAdapter<T> extends ArrayAdapter<T> {

    private int resource;

    public XArrayAdapter(Context context, int resource, T[] objects) {
        super(context, resource, objects);
        this.resource = resource;
    }

    public XArrayAdapter(Context context, int resource, List<T> objects) {
        super(context, resource, objects);
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent, getResource(position));
    }

    protected int getResource(int position) {
        return resource;
    }

    private View createViewFromResource(int position, View convertView, ViewGroup parent, int resource) {
        View view;
        T item = getItem(position);
        int viewTypeCount = getViewTypeCount();
        if (convertView == null) {
            view = createView(parent, resource, position, item, parent);
            if (viewTypeCount > 0) {
                view.setTag(getItemViewType(position));
            }
        } else {
            if (viewTypeCount > 0) {
                int itemViewType = getItemViewType(position);
                if (itemViewType != convertView.getTag()) {
                    convertView = createView(parent, resource, position, item, parent);
                    convertView.setTag(itemViewType);
                }
            }
            view = convertView;
        }
        bindView(position, item, view, parent);
        return view;
    }

    protected View createView(ViewGroup parent,  int resource, int position, T item, ViewGroup viewGroup) {
        return View.inflate(parent.getContext(), resource, null);
    }

    protected abstract void bindView(int position, T item, View view, ViewGroup parent);

}
