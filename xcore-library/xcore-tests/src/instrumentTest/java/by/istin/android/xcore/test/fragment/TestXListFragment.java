package by.istin.android.xcore.test.fragment;


import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.view.View;

import by.isitn.android.xcore.app.Application;
import by.istin.android.xcore.fragment.XListFragment;
import by.istin.android.xcore.model.SimpleEntity;
import by.istin.android.xcore.processor.SimpleEntityProcessor;
import by.istin.android.xcore.provider.ModelContract;
import by.istin.android.xcore.test.common.AbstractTestProcessor;
import by.istin.android.xcore.utils.CursorUtils;

public class TestXListFragment extends AbstractTestProcessor {

    public TestXListFragment() {
        super(Application.class);
    }

    private static class ListFragment extends XListFragment {

        @Override
        public void onListItemClick(Cursor cursor, View v, int position, long id) {

        }

        @Override
        public int getViewLayout() {
            return 0;
        }

        @Override
        public Uri getUri() {
            return ModelContract.getUri(SimpleEntity.class);
        }

        @Override
        public String getUrl() {
            return null;
        }

        @Override
        public String getProcessorKey() {
            return null;
        }

        @Override
        protected String[] getAdapterColumns() {
            return new String[0];
        }

        @Override
        protected int[] getAdapterControlIds() {
            return new int[0];
        }

        @Override
        protected int getAdapterLayout() {
            return 0;
        }

        @Override
        public String getSearchField() {
            return SimpleEntity.TITLE;
        }

        @Override
        public String[] getProjection() {
            return new String[]{SimpleEntity.TITLE};
        }
    }

    public void testSearch() throws Exception {
        clear(SimpleEntity.class);
        testExecute(SimpleEntityProcessor.APP_SERVICE_KEY, "simpleEntity/sample_page_1.json?page=1");
        checkCount(SimpleEntity.class, 5);
        ListFragment listFragment = new ListFragment();
        Cursor cursor = getApplication().getContentResolver().query(listFragment.getUri(), listFragment.getProjection(), listFragment.getSelection(), listFragment.getSelectionArgs(), listFragment.getOrder());
        assertEquals(5, cursor.getCount());
        DatabaseUtils.dumpCursor(cursor);
        CursorUtils.close(cursor);
        cursor = listFragment.runSearchQuery(getApplication(), "Susana");
        assertEquals(1, cursor.getCount());
    }

}
