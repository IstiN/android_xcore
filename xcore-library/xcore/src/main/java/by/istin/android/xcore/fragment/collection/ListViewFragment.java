package by.istin.android.xcore.fragment.collection;

import android.view.View;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.HashSet;
import java.util.Set;

import by.istin.android.xcore.fragment.XListFragment;
import by.istin.android.xcore.model.CursorModel;
import by.istin.android.xcore.utils.Log;

/**
 * Created by IstiN on 21.12.2014.
 */
public abstract class ListViewFragment<CollectionAdapter extends ListAdapter, Model extends CursorModel> extends AbstractCollectionFragment<ListView, CollectionAdapter, Model> {

    private class EndlessScrollListener implements AbsListView.OnScrollListener {

        private int visibleThreshold = 5;

        private int currentPage = 0;

        private int previousTotal = 0;

        private volatile boolean pagingLoading = false;

        public EndlessScrollListener() {

        }

        public EndlessScrollListener(int visibleThreshold) {
            this.visibleThreshold = visibleThreshold;
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            ListAdapter adapter = view.getAdapter();
            int count = XListFragment.getRealAdapterCount(adapter);
            if (count == 0) {
                return;
            }
            if (IS_CHECK_STATUS_LOG_ENABLED)
                Log.d("fragment_status", "paging " + firstVisibleItem + " " + visibleItemCount + " " + totalItemCount + " " + count);
            if (previousTotal != totalItemCount && !pagingLoading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                previousTotal = totalItemCount;
                pagingLoading = true;
                currentPage++;
                onPageLoad(currentPage, totalItemCount);
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

    }

    private final Set<AbsListView.OnScrollListener> onScrollListenerSet = new HashSet<>();

    private EndlessScrollListener mEndlessScrollListener;

    @Override
    protected void addPagingSupport(View view) {
        mEndlessScrollListener = new EndlessScrollListener();
        setOnScrollListViewListener(mEndlessScrollListener);
        getCollectionView().setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                for (AbsListView.OnScrollListener onScrollListener : onScrollListenerSet) {
                    onScrollListener.onScrollStateChanged(absListView, i);
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i2, int i3) {
                for (AbsListView.OnScrollListener onScrollListener : onScrollListenerSet) {
                    onScrollListener.onScroll(absListView, i, i2, i3);
                }
            }
        });
    }

    public void setOnScrollListViewListener(AbsListView.OnScrollListener scrollListener) {
        onScrollListenerSet.add(scrollListener);
    }

    public void removeOnScrollListViewListener(AbsListView.OnScrollListener scrollListener) {
        onScrollListenerSet.remove(scrollListener);
    }

    @Override
    public void setAdapter(ListView listView, CollectionAdapter collectionAdapter) {
        listView.setAdapter(collectionAdapter);
    }

    @Override
    public void checkIfAdapterValid(ListView listView, CollectionAdapter collectionAdapter) {
        if (listView.getAdapter() == null || listView.getAdapter() != collectionAdapter) {
            listView.setAdapter(collectionAdapter);
        }
    }

    @Override
    protected int getAdapterCount(CollectionAdapter listAdapter) {
        return XListFragment.getRealAdapterCount(listAdapter);
    }

}
