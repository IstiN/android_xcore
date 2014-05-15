package by.istin.android.xcore.test.fragment;


import android.database.Cursor;
import android.net.Uri;
import android.view.View;

import by.istin.android.xcore.app.Application;
import by.istin.android.xcore.db.impl.DBHelper;
import by.istin.android.xcore.fragment.XListFragment;
import by.istin.android.xcore.model.SimpleEntity;
import by.istin.android.xcore.processor.SimpleEntityProcessor;
import by.istin.android.xcore.provider.ModelContract;
import by.istin.android.xcore.test.common.AbstractTestProcessor;

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

    public static class SQLListFragment extends ListFragment {

        @Override
        public Uri getUri() {
            return ModelContract.getSQLQueryUri("SELECT * FROM " + DBHelper.getTableName(SimpleEntity.class), ModelContract.getUri(SimpleEntity.class));
        }

    }

    public static class ListWithSelectionFragment extends ListFragment {

        @Override
        public String getSelection() {
            return SimpleEntity.TITLE + " like ?";
        }

        @Override
        public String[] getSelectionArgs() {
            return new String[]{"%ll%"};
        }
    }

    public void testSearch() throws Exception {
        clear(SimpleEntity.class);
        testExecute(SimpleEntityProcessor.APP_SERVICE_KEY, "simpleEntity/sample_page_1.json?page=1");
        checkCount(SimpleEntity.class, 5);
        ListFragment listFragment = new ListFragment();
        Cursor cursor = listFragment.runSearchQuery(getApplication(), "Susana");
        assertEquals(1, cursor.getCount());
        cursor.close();
    }

    public void testSearchWithSelections() throws Exception {
        clear(SimpleEntity.class);
        testExecute(SimpleEntityProcessor.APP_SERVICE_KEY, "simpleEntity/sample_page_1.json?page=1");
        checkCount(SimpleEntity.class, 5);

        ListFragment listFragment = new ListFragment();
        Cursor cursor = listFragment.runSearchQuery(getApplication(), "e");
        assertEquals(3, cursor.getCount());
        cursor.close();

        ListWithSelectionFragment listWithSelectionFragment = new ListWithSelectionFragment();
        cursor = listWithSelectionFragment.runSearchQuery(getApplication(), "e");
        assertEquals(2, cursor.getCount());
        cursor.close();

    }

    public void testWrongParametersForSearch() throws Exception {
        boolean isException = false;
        try {
            SQLListFragment listFragment = new SQLListFragment();
            listFragment.runSearchQuery(getApplication(), "Susana");
        } catch (IllegalArgumentException e) {
            isException = true;
        }
        assertEquals(true, isException);
    }

}
