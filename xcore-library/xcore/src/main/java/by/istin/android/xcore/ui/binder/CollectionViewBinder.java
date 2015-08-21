package by.istin.android.xcore.ui.binder;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import by.istin.android.xcore.image.ImageService;
import by.istin.android.xcore.provider.ModelContract;
import by.istin.android.xcore.utils.ContentUtils;

/**
 * Created by uladzimir_klyshevich on 6/25/15.
 */
public abstract class CollectionViewBinder<CollectionView extends ICollectionView, Adapter> {

    private CollectionView mCollectionView;

    private Adapter mAdapter;

    private final ImageService mImageService;

    private List<Pair<String, Integer>> mBindingRules = new ArrayList<>();

    private List<Binder.IData> mCollection;

    private int mLayout;

    protected CollectionViewBinder(CollectionView collectionView) {
        mCollectionView = collectionView;
        mImageService = ImageService.get(mCollectionView.getContext());
    }

    public CollectionViewBinder data(Cursor cursor) {
        if (cursor == null) {
            mCollection = null;
        } else {
            mCollection = new CursorDataList(cursor);
        }
        updateAdapter();
        return this;
    }

    protected void updateAdapter() {
        if (mCollection == null) {
            mAdapter = null;
        } else {
            mAdapter = createAdapter(mCollection, mBindingRules, mLayout);
        }
        setAdapter(mCollectionView, mAdapter);
    }

    public CollectionViewBinder layout(int layout) {
        mLayout = layout;
        return this;
    }

    protected abstract void setAdapter(CollectionView mCollectionView, Adapter mAdapter);

    protected abstract Adapter createAdapter(List<Binder.IData> mCollection, List<Pair<String, Integer>> mBindingRules, int layout);

    public CollectionViewBinder data(List<ContentValues> contentValues) {
        if (contentValues == null) {
            mCollection = null;
        } else {
            mCollection = new ContentValuesDataList(contentValues);
        }
        updateAdapter();
        return this;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public CollectionViewBinder data(final Class clazz, final String selection, final String[] selectionArgs, final String order) {
        AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                final List<ContentValues> entities = ContentUtils.getEntities(mCollectionView.getContext(), ModelContract.getUri(clazz), order, selection, selectionArgs);
                Activity activity = (Activity) mCollectionView.getContext();
                if (activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            data(entities);
                        }
                    });
                }
            }
        });
        return this;
    }

    public CollectionViewBinder bind(String key, int id) {
        mBindingRules.add(new Pair<>(key, id));
        return this;
    }

    public CollectionViewBinder bind(String[] keys, int[] ids) {
        for (int i = 0; i < keys.length; i++) {
            mBindingRules.add(new Pair<>(keys[i], ids[i]));
        }
        return this;
    }

}
