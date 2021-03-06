package by.istin.android.xcore.sample;

import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.View;

import by.istin.android.xcore.fragment.XListFragment;
import by.istin.android.xcore.provider.ModelContract;
import by.istin.android.xcore.sample.core.model.SampleEntity;
import by.istin.android.xcore.sample.core.processor.SampleEntityProcessor;

public class MainActivity extends AbstractActivity {

    @Override
    protected Fragment createFragment() {
        return new SampleListFragment();
    }

    public static class SampleListFragment extends XListFragment {

        private static final String PAGE1 = "https://dl.dropboxusercontent.com/u/16403954/xcore/sample_page_1.json?page=1";

        private static final String PAGE2 = "https://dl.dropboxusercontent.com/u/16403954/xcore/sample_page_2.json?page=2";

        public SampleListFragment() {

        }

        @Override
        public void onListItemClick(Cursor cursor, View view, int i, long l) {

        }

        @Override
        public int getViewLayout() {
            return R.layout.fragment_main;
        }

        @Override
        public Uri getUri() {
            return ModelContract.getUri(SampleEntity.class);
        }

        @Override
        public String getUrl() {
            return PAGE1;
        }

        @Override
        protected void onPageLoad(int newPage, int totalItemCount) {
            loadData(getActivity(), PAGE2, PAGE1);
        }

        @Override
        protected boolean isPagingSupport() {
            return true;
        }

        @Override
        public String getProcessorKey() {
            return SampleEntityProcessor.APP_SERVICE_KEY;
        }

        @Override
        protected String[] getAdapterColumns() {
            return new String[]{SampleEntity.TITLE, SampleEntity.ABOUT, SampleEntity.IMAGE_URL};
        }

        @Override
        protected int[] getAdapterControlIds() {
            return new int[]{R.id.title, R.id.about, R.id.thumbnail};
        }

        @Override
        protected int getAdapterLayout() {
            return R.layout.adapter_sample_entity;
        }

        @Override
        public long getCacheExpiration() {
            return 1l;
        }
    }

}
