package by.istin.android.xcore.ui.binder;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.apache.commons.codec.internal.StringDecoder;

import java.net.DatagramSocket;
import java.util.List;

import by.istin.android.xcore.utils.CursorUtils;
import by.istin.android.xcore.widget.XArrayAdapter;

/**
 * Created by uladzimir_klyshevich on 7/22/15.
 */
public class Binder {

    public interface IData<Data> {

        void setData(Data data);

        String getValue(String key);
    }

    public static ViewBinder view(View view) {
        return new ViewBinder(view);
    }

    public static ViewBinder view(View view, Cursor data) {
        return new ViewBinder(view, data);
    }

    public static CollectionViewBinder<RecyclerView, RecyclerView.Adapter> collection(final RecyclerView recyclerView) {
        return new CollectionViewBinder<RecyclerView, RecyclerView.Adapter>(recyclerView) {

            @Override
            protected void setAdapter(RecyclerView mCollectionView, RecyclerView.Adapter mAdapter) {
                mCollectionView.setAdapter(mAdapter);
            }

            @Override
            protected RecyclerView.Adapter createAdapter(final List<IData> mCollection, final List<Pair<String, Integer>> mBindingRules, final int layout) {
                return new RecyclerView.Adapter() {

                    private ViewBinder mBinder;

                    @Override
                    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        return new RecyclerView.ViewHolder(View.inflate(recyclerView.getContext(), layout, null)){};
                    }

                    @Override
                    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                        View view = holder.itemView;
                        if (mBinder == null) {
                            mBinder = view(view);
                        } else {
                            mBinder.view(view);
                        }
                        mBinder.data(mCollection.get(position));
                        for (Pair<String, Integer> pair : mBindingRules) {
                            mBinder.bind(pair.first, pair.second);
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

    public static CollectionViewBinder<ListView, ListAdapter> collection(final ListView listView) {
        return new CollectionViewBinder<ListView, ListAdapter>(listView) {
            @Override
            protected void setAdapter(ListView mCollectionView, ListAdapter mAdapter) {
                mCollectionView.setAdapter(mAdapter);
            }

            @Override
            protected ListAdapter createAdapter(List<IData> mCollection, final List<Pair<String, Integer>> mBindingRules, int layout) {
                return new XArrayAdapter<IData>(listView.getContext(), layout, mCollection) {
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
        };
        data.setData(contentValues);
        return data;
    }
}
