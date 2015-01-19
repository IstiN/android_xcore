package by.istin.android.xcore.sample;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.ImageView;

import by.istin.android.xcore.fragment.XListFragment;
import by.istin.android.xcore.provider.ModelContract;
import by.istin.android.xcore.sample.core.model.Content;
import by.istin.android.xcore.sample.core.processor.ContentEntityProcessor;

public class AdvancedActivity extends AbstractActivity {

    public static final String URL = "https://dl.dropboxusercontent.com/u/16403954/streamer.json";
    //TODO
    @Override
    protected Fragment createFragment() {
        return new StreamerFragment();
    }


    public static class StreamerFragment extends XListFragment {

        @Override
        public void onListItemClick(Cursor cursor, View v, int position, long id) {

        }

        @Override
        public int getViewLayout() {
            return R.layout.fragment_main;
        }

        @Override
        public Uri getUri() {
            return ModelContract.getUri(Content.class);
        }

        @Override
        public String getUrl() {
            return URL;
        }

        @Override
        public String getProcessorKey() {
            return ContentEntityProcessor.APP_SERVICE_KEY;
        }

        @Override
        public String getOrder() {
            return Content.POSITION + " ASC";
        }

        @Override
        public String[] getProjection() {
            return new String[]{Content.ID, Content.POSITION, Content.AUTHOR_AVATAR_URL, Content.CONTENT_TEXT, Content.AUTHOR_DISPLAY_NAME, Content.TIMESTAMP_FORMATTED, Content.MAIN_CONTENT_IMAGE};
        }

        @Override
        public String[] getAdapterColumns() {
            return new String[]{Content.AUTHOR_DISPLAY_NAME, Content.CONTENT_TEXT, Content.AUTHOR_AVATAR_URL, Content.TIMESTAMP_FORMATTED, Content.MAIN_CONTENT_IMAGE};
        }

        @Override
        public int[] getAdapterControlIds() {
            return new int[]{R.id.title, R.id.about, R.id.thumbnail, R.id.date, R.id.main_image};
        }

        @Override
        public boolean isForceUpdateData() {
            return true;
        }

        @Override
        public long getCacheExpiration() {
            return 1l;
        }

        @Override
        public int getAdapterLayout() {
            return R.layout.adapter_advanced_entity;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            super.onLoadFinished(loader, cursor);
            if (cursor != null) {
                //DatabaseUtils.dumpCursor(cursor);
            }
        }

        @Override
        protected boolean setAdapterViewImage(ImageView v, String value) {
            return super.setAdapterViewImage(v, value);
        }

        @Override
        protected View onAdapterGetView(SimpleCursorAdapter simpleCursorAdapter, int position, View view) {
            return super.onAdapterGetView(simpleCursorAdapter, position, view);
        }
    }
}
