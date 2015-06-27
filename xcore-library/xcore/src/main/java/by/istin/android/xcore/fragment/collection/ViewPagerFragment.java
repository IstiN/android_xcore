package by.istin.android.xcore.fragment.collection;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ListView;

import by.istin.android.xcore.model.CursorModel;

public abstract class ViewPagerFragment <CollectionAdapter extends PagerAdapter, Model extends CursorModel>
        extends AbstractCollectionFragment<ViewPager, CollectionAdapter, Model> {

    @Override
    public void setAdapter(ViewPager viewPager, CollectionAdapter collectionAdapter) {
        viewPager.setAdapter(collectionAdapter);
    }

    @Override
    public void checkIfAdapterValid(ViewPager viewPager, CollectionAdapter collectionAdapter) {
        if (viewPager.getAdapter() == null || viewPager.getAdapter() != collectionAdapter) {
            viewPager.setAdapter(collectionAdapter);
        }
    }

    @Override
    protected int getAdapterCount(CollectionAdapter pagerAdapter) {
        if (pagerAdapter == null) {
            return 0;
        }
        return pagerAdapter.getCount();
    }

    @Override
    protected void addPagingSupport(View view) {

    }

}
