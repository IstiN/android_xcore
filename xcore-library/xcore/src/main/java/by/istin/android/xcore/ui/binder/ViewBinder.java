package by.istin.android.xcore.ui.binder;

import android.content.ContentValues;
import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import by.istin.android.xcore.image.ImageService;
import by.istin.android.xcore.utils.StringUtil;

/**
 * Created by uladzimir_klyshevich on 6/25/15.
 */
public class ViewBinder {

    private View mRoot;

    private Binder.IData mData;

    private final ImageService mImageService;

    ViewBinder(View root) {
        mRoot = root;
        mImageService = ImageService.get(mRoot.getContext());
    }

    ViewBinder(View root, Binder.IData data) {
        this(root);
        mData = data;
    }

    ViewBinder(View root, final Cursor cursor) {
        this(root, Binder.getData(cursor));
    }

    ViewBinder(View root, final ContentValues contentValues) {
        this(root, Binder.getData(contentValues));
    }

    public ViewBinder view(View root) {
        mRoot = root;
        return this;
    }

    public ViewBinder data(Cursor cursor) {
        return data(Binder.getData(cursor));
    }

    public ViewBinder data(ContentValues contentValues) {
        return data(Binder.getData(contentValues));
    }

    public ViewBinder data(Binder.IData data) {
        mData = data;
        return this;
    }

    public ViewBinder bind(String key, int id) {
        final View viewById = mRoot.findViewById(id);
        final String value = mData.getValue(key);
        if (viewById instanceof TextView) {
            if (StringUtil.isEmpty(value)) {
                onEmpty(viewById);
            } else {
                ((TextView) viewById).setText(value);
            }
        } else if (viewById instanceof ImageView) {
            mImageService.load(viewById, value);
        }
        return this;
    }

    public ViewBinder click(int id, View.OnClickListener clickListener) {
        final View viewById = mRoot.findViewById(id);
        return click(viewById, clickListener);
    }

    public ViewBinder click(View viewById, View.OnClickListener clickListener) {
        viewById.setOnClickListener(clickListener);
        return this;
    }

    protected void onEmpty(View viewById) {
        viewById.setVisibility(View.GONE);
    }

}
