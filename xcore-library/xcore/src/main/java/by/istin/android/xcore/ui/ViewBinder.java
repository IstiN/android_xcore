package by.istin.android.xcore.ui;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import by.istin.android.xcore.image.ImageService;
import by.istin.android.xcore.utils.CursorUtils;
import by.istin.android.xcore.utils.StringUtil;

/**
 * Created by uladzimir_klyshevich on 6/25/15.
 */
public class ViewBinder {

    private View mRoot;

    private IData mData;

    private final ImageService mImageService;

    public interface IData {
        String getValue(String key);
    }

    public ViewBinder(View root) {
        mRoot = root;
        mImageService = ImageService.get(mRoot.getContext());
    }

    public ViewBinder(View root, IData data) {
        this(root);
        mData = data;
    }

    public ViewBinder(View root, final Cursor cursor) {
        this(root, getData(cursor));
    }

    public ViewBinder(View root, final ContentValues contentValues) {
        this(root, getData(contentValues));
    }

    @NonNull
    public static IData getData(final Cursor cursor) {
        return new IData() {
            @Override
            public String getValue(String key) {
                return CursorUtils.getString(key, cursor);
            }
        };
    }

    @NonNull
    public static IData getData(final ContentValues contentValues) {
        return new IData() {
            @Override
            public String getValue(String key) {
                return contentValues.getAsString(key);
            }
        };
    }

    public ViewBinder setData(Cursor cursor) {
        mData = getData(cursor);
        return this;
    }

    public ViewBinder setData(ContentValues contentValues) {
        mData = getData(contentValues);
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
