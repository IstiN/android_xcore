package by.istin.android.xcore.fragment.collection;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

import by.istin.android.xcore.model.CursorModel;

/**
 * Created by IstiN on 21.12.2014.
 */
public abstract class RecyclerViewFragment<VH extends RecyclerView.ViewHolder, CollectionAdapter extends RecyclerView.Adapter<VH>, Model extends CursorModel>
        extends AbstractCollectionFragment<RecyclerView, CollectionAdapter, Model> {

    private volatile boolean pagingLoading = false;

    private final Set<RecyclerView.OnScrollListener> onScrollListenerSet = new HashSet<>();

    private RecyclerView.LayoutManager mLayoutManager;

    public RecyclerView.ItemAnimator getItemAnimator() {
        return mItemAnimator;
    }

    private RecyclerView.ItemAnimator mItemAnimator;

    private static class GroupScrollListener extends RecyclerView.OnScrollListener {

        private WeakReference<RecyclerViewFragment> mRecyclerViewFragmentWeakReference;

        public GroupScrollListener(RecyclerViewFragment pRecyclerViewFragment) {
            mRecyclerViewFragmentWeakReference = new WeakReference<>(pRecyclerViewFragment);
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            RecyclerViewFragment recyclerViewFragment = mRecyclerViewFragmentWeakReference.get();
            if (recyclerViewFragment == null) {
                recyclerView.removeOnScrollListener(this);
                return;
            }
            Set<RecyclerView.OnScrollListener> onScrollListenerSet = recyclerViewFragment.onScrollListenerSet;
            for (RecyclerView.OnScrollListener onScrollListener : onScrollListenerSet) {
                onScrollListener.onScrollStateChanged(recyclerView, newState);
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            RecyclerViewFragment recyclerViewFragment = mRecyclerViewFragmentWeakReference.get();
            if (recyclerViewFragment == null) {
                recyclerView.removeOnScrollListener(this);
                return;
            }
            Set<RecyclerView.OnScrollListener> onScrollListenerSet = recyclerViewFragment.onScrollListenerSet;
            for (RecyclerView.OnScrollListener onScrollListener : onScrollListenerSet) {
                onScrollListener.onScrolled(recyclerView, dx, dy);
            }
        }

    }

    private static class EndlessScrollListener extends RecyclerView.OnScrollListener {

        private int visibleThreshold = 5;

        private int currentPage = 0;

        private int previousTotal = 0;

        private WeakReference<RecyclerView.LayoutManager> mLayoutManagerWeakReference;

        private WeakReference<RecyclerViewFragment> mRecyclerViewFragmentWeakReference;

        public EndlessScrollListener(RecyclerView.LayoutManager layoutManager, RecyclerViewFragment pRecyclerViewFragment) {
            mLayoutManagerWeakReference = new WeakReference<>(layoutManager);
            mRecyclerViewFragmentWeakReference = new WeakReference<>(pRecyclerViewFragment);
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
            RecyclerView.LayoutManager layoutManager = mLayoutManagerWeakReference.get();
            if (layoutManager == null) {
                recyclerView.removeOnScrollListener(this);
                return;
            }
            RecyclerViewFragment recyclerViewFragment = mRecyclerViewFragmentWeakReference.get();
            if (recyclerViewFragment == null) {
                recyclerView.removeOnScrollListener(this);
            }
            int firstVisibleItem = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
            int visibleItemCount = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition() - firstVisibleItem;
            if (previousTotal != count && !recyclerViewFragment.pagingLoading && (count - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                previousTotal = count;
                recyclerViewFragment.pagingLoading = true;
                currentPage++;
                recyclerViewFragment.onPageLoad(currentPage, count);
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


    @Override
    public void onViewCreated(View view) {
        super.onViewCreated(view);
        RecyclerView collectionView = getCollectionView();
        mLayoutManager = createLayoutManager();
        collectionView.setLayoutManager(mLayoutManager);
        mItemAnimator = createItemAnimator();
        collectionView.setItemAnimator(mItemAnimator);
        getCollectionView().addOnScrollListener(new GroupScrollListener(this));
    }

    protected RecyclerView.LayoutManager createLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }

    protected RecyclerView.ItemAnimator createItemAnimator() {
        return new DefaultItemAnimator();
    }

    @Override
    protected void addPagingSupport(View view) {
        EndlessScrollListener mEndlessScrollListener = new EndlessScrollListener(mLayoutManager, this);
        setOnScrollListViewListener(mEndlessScrollListener);
    }

    public void setOnScrollListViewListener(RecyclerView.OnScrollListener scrollListener) {
        onScrollListenerSet.add(scrollListener);
    }

    public void removeOnScrollListViewListener(RecyclerView.OnScrollListener scrollListener) {
        onScrollListenerSet.remove(scrollListener);
    }

    @Override
    public void onDestroy() {
        getCollectionView().clearOnScrollListeners();
        super.onDestroy();
        onScrollListenerSet.clear();
        mLayoutManager = null;
        mItemAnimator = null;
    }

    @Override
    public void setAdapter(RecyclerView recyclerView, CollectionAdapter collectionAdapter) {
        recyclerView.setAdapter(collectionAdapter);
    }

    @Override
    public void checkIfAdapterValid(RecyclerView recyclerView, CollectionAdapter collectionAdapter) {
        if (recyclerView.getAdapter() == null || recyclerView.getAdapter() != collectionAdapter) {
            recyclerView.setAdapter(collectionAdapter);
        }
    }

    @Override
    protected int getAdapterCount(CollectionAdapter listAdapter) {
        if (listAdapter == null) {
            return 0;
        }
        return listAdapter.getItemCount();
    }

}
