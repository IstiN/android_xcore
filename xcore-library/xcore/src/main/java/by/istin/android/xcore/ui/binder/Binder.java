package by.istin.android.xcore.ui.binder;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.List;

import by.istin.android.xcore.utils.ContentUtils;
import by.istin.android.xcore.utils.CursorUtils;
import by.istin.android.xcore.widget.XArrayAdapter;

/**
 * Created by uladzimir_klyshevich on 7/22/15.
 */
public class Binder {

    public interface IData<Data> {

        void setData(Data data);

        String getValue(String key);

        ContentValues toContentValues();
    }

    public static ViewBinder view(View view) {
        return new ViewBinder(view);
    }

    public static ViewBinder view(View view, Cursor data) {
        return new ViewBinder(view, data);
    }

    public static CollectionViewBinder<ICollectionView<RecyclerView>, RecyclerView.Adapter> collection(final RecyclerView recyclerView) {
        ICollectionView<RecyclerView> collectionView = new ICollectionView<RecyclerView>() {
            @Override
            public Context getContext() {
                return recyclerView.getContext();
            }

            @Override
            public RecyclerView getWrappedView() {
                return recyclerView;
            }
        };
        return new CollectionViewBinder<ICollectionView<RecyclerView>, RecyclerView.Adapter>(collectionView) {

            @Override
            protected void setAdapter(ICollectionView<RecyclerView> mCollectionView, RecyclerView.Adapter mAdapter) {
                mCollectionView.getWrappedView().setAdapter(mAdapter);
            }

            @Override
            protected RecyclerView.Adapter createAdapter(final List<IData> mCollection, final List<Pair<String, Integer>> mBindingRules, final int layout) {
                final IOnBindViewListener onBindViewListener = getOnBindViewListener();
                return new RecyclerView.Adapter() {

                    private ViewBinder mBinder;

                    @Override
                    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        View createdView = View.inflate(recyclerView.getContext(), layout, null);
                        RecyclerView.ViewHolder viewHolder = new RecyclerView.ViewHolder(createdView) {
                        };
                        if (onBindViewListener != null) {
                            onBindViewListener.onCreateView(this, viewHolder);
                        }
                        return viewHolder;
                    }

                    @Override
                    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                        View view = holder.itemView;
                        if (mBinder == null) {
                            mBinder = view(view);
                        } else {
                            mBinder.view(view);
                        }
                        IData data = mCollection.get(position);
                        mBinder.data(data);
                        for (Pair<String, Integer> pair : mBindingRules) {
                            mBinder.bind(pair.first, pair.second);
                        }
                        if (onBindViewListener != null) {
                            onBindViewListener.onBindView(this, holder, position, data);
                        }

                    }

                    @Override
                    public int getItemCount() {
                        return mCollection.size();
                    }

                };
            }

        };
    }

    public static CollectionViewBinder<ICollectionView<ListView>, ListAdapter> collection(final ListView listView) {
        return new CollectionViewBinder<ICollectionView<ListView>, ListAdapter>(new ICollectionView<ListView>() {
            @Override
            public Context getContext() {
                return listView.getContext();
            }

            @Override
            public ListView getWrappedView() {
                return listView;
            }
        }) {
            @Override
            protected void setAdapter(ICollectionView<ListView> mCollectionView, ListAdapter mAdapter) {
                mCollectionView.getWrappedView().setAdapter(mAdapter);
            }

            @Override
            protected ListAdapter createAdapter(List<IData> mCollection, final List<Pair<String, Integer>> mBindingRules, int layout) {
                return createXArrayAdapter(listView.getContext(), mCollection, mBindingRules, layout);
            }

        };
    }

    public static CollectionViewBinder<ICollectionView<AlertDialog.Builder>, ListAdapter> collection(final AlertDialog.Builder alertDialogBuilder, final DialogInterface.OnClickListener onClickListener) {
        return new CollectionViewBinder<ICollectionView<AlertDialog.Builder>, ListAdapter>(new ICollectionView<AlertDialog.Builder>() {
            @Override
            public Context getContext() {
                return alertDialogBuilder.getContext();
            }

            @Override
            public AlertDialog.Builder getWrappedView() {
                return alertDialogBuilder;
            }
        }) {

            @Override
            protected void setAdapter(ICollectionView<AlertDialog.Builder> mCollectionView, ListAdapter mAdapter) {
                mCollectionView.getWrappedView().setAdapter(mAdapter, onClickListener);
            }

            @Override
            protected ListAdapter createAdapter(List<IData> mCollection, final List<Pair<String, Integer>> mBindingRules, int layout) {
                return createXArrayAdapter(alertDialogBuilder.getContext(), mCollection, mBindingRules, layout);
            }

        };
    }

    @NonNull
    private static XArrayAdapter<IData> createXArrayAdapter(Context pContext, final List<IData> mCollection, final List<Pair<String, Integer>> mBindingRules, final int layout) {
        return new XArrayAdapter<IData>(pContext, layout, mCollection) {
            private ViewBinder mBinder;

            @Override
            protected void bindView(int position, IData item, View view, ViewGroup parent) {
                if (mBinder == null) {
                    mBinder = view(view);
                } else {
                    mBinder.view(view);
                }
                mBinder.data(item);
                for (Pair<String, Integer> pair : mBindingRules) {
                    mBinder.bind(pair.first, pair.second);
                }
            }
        };
    }

    @NonNull
    public static Binder.IData<Cursor> getData(final Cursor cursor) {
        Binder.IData<Cursor> data = new Binder.IData<Cursor>() {

            private Cursor mCursor;

            @Override
            public void setData(Cursor cursor) {
                mCursor = cursor;
            }

            @Override
            public String getValue(String key) {
                return CursorUtils.getString(key, mCursor);
            }

            @Override
            public ContentValues toContentValues() {
                return CursorUtils.cursorRowToContentValues(mCursor);
            }
        };
        data.setData(cursor);
        return data;
    }

    @NonNull
    public static Binder.IData<ContentValues> getData(final ContentValues contentValues) {
        Binder.IData<ContentValues> data = new Binder.IData<ContentValues>() {

            private ContentValues mContentValues;

            @Override
            public void setData(ContentValues contentValues) {
                mContentValues = contentValues;
            }

            @Override
            public String getValue(String key) {
                return mContentValues.getAsString(key);
            }

            @Override
            public ContentValues toContentValues() {
                return mContentValues;
            }
        };
        data.setData(contentValues);
        return data;
    }
}
