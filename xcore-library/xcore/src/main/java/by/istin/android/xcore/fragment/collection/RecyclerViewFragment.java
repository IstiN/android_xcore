package by.istin.android.xcore.fragment.collection;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.HashSet;
import java.util.Set;

import by.istin.android.xcore.model.CursorModel;

/**
 * Created by IstiN on 21.12.2014.
 */
public abstract class RecyclerViewFragment<VH extends RecyclerView.ViewHolder, CollectionAdapter extends RecyclerView.Adapter<VH>, Model extends CursorModel>
        extends AbstractCollectionFragment<RecyclerView, CollectionAdapter, Model> {

    private volatile boolean pagingLoading = false;

    private class EndlessScrollListener extends RecyclerView.OnScrollListener {

        private int visibleThreshold = 5;

        private int currentPage = 0;

        private int previousTotal = 0;

        public EndlessScrollListener() {

        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            RecyclerView.Adapter adapter = recyclerView.getAdapter();
            int count = adapter.getItemCount();
            if (count == 0) {
                return;
            }
            int firstVisibleItem = ((LinearLayoutManager) mLayoutManager).findFirstVisibleItemPosition();
            int visibleItemCount = ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition() - firstVisibleItem;
            if (previousTotal != count && !pagingLoading && (count - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                previousTotal = count;
                pagingLoading = true;
                currentPage++;
                onPageLoad(currentPage, count);
            }
        }

    }

    @Override
    public void onReceiverOnDone(Bundle resultData) {
        super.onReceiverOnDone(resultData);
        if (isPagingSupport()) {
            pagingLoading = false;
        }
    }

    private final Set<RecyclerView.OnScrollListener> onScrollListenerSet = new HashSet<>();

    private RecyclerView.LayoutManager mLayoutManager;

    public RecyclerView.ItemAnimator getItemAnimator() {
        return mItemAnimator;
    }

    private RecyclerView.ItemAnimator mItemAnimator;

    @Override
    public void onViewCreated(View view) {
        super.onViewCreated(view);
        RecyclerView collectionView = getCollectionView();
        mLayoutManager = createLayoutManager();
        collectionView.setLayoutManager(mLayoutManager);
        mItemAnimator = createItemAnimator();
        collectionView.setItemAnimator(mItemAnimator);
        getCollectionView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                for (RecyclerView.OnScrollListener onScrollListener : onScrollListenerSet) {
                    onScrollListener.onScrollStateChanged(recyclerView, newState);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                for (RecyclerView.OnScrollListener onScrollListener : onScrollListenerSet) {
                    onScrollListener.onScrolled(recyclerView, dx, dy);
                }
            }
        });
    }

    protected RecyclerView.LayoutManager createLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }

    protected RecyclerView.ItemAnimator createItemAnimator() {
        return new DefaultItemAnimator();
    }

    @Override
    protected void addPagingSupport(View view) {
        EndlessScrollListener mEndlessScrollListener = new EndlessScrollListener();
        setOnScrollListViewListener(mEndlessScrollListener);
    }

    public void setOnScrollListViewListener(RecyclerView.OnScrollListener scrollListener) {
        onScrollListenerSet.add(scrollListener);
    }

    public void removeOnScrollListViewListener(RecyclerView.OnScrollListener scrollListener) {
        onScrollListenerSet.remove(scrollListener);
    }

    @Override
    public void setAdapter(RecyclerView recyclerView, CollectionAdapter collectionAdapter) {
        recyclerView.setAdapter(collectionAdapter);
    }

    @Override
    protected int getAdapterCount(CollectionAdapter listAdapter) {
        if (listAdapter == null) {
            return 0;
        }
        return listAdapter.getItemCount();
    }

}
