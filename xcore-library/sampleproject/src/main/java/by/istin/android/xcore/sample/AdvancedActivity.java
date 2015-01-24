package by.istin.android.xcore.sample;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import by.istin.android.xcore.fragment.XListFragment;
import by.istin.android.xcore.model.CursorModel;
import by.istin.android.xcore.provider.ModelContract;
import by.istin.android.xcore.sample.core.model.Content;
import by.istin.android.xcore.sample.core.processor.ContentEntityProcessor;
import by.istin.android.xcore.utils.CursorUtils;
import by.istin.android.xcore.utils.StringUtil;

public class AdvancedActivity extends AbstractActivity {

    public static final String URL = "https://dl.dropboxusercontent.com/u/16403954/streamer.json";
    public static final int[] ADAPTER_CONTROL_IDS = new int[]{R.id.title, R.id.thumbnail, R.id.date, R.id.main_image, R.id.attachs_count};
    public static final String[] ADAPTER_COLUMNS = new String[]{Content.AUTHOR_DISPLAY_NAME, Content.AUTHOR_AVATAR_URL, Content.TIMESTAMP_FORMATTED, Content.MAIN_CONTENT_IMAGE, Content.ATTACHS_COUNT};
    public static final String[] PROJECTION = new String[]{Content.ID, Content.POSITION, Content.AUTHOR_AVATAR_URL, Content.CONTENT_TEXT, Content.AUTHOR_DISPLAY_NAME, Content.TIMESTAMP_FORMATTED, Content.MAIN_CONTENT_IMAGE, Content.ATTACHS_COUNT};

    private static class StreamCursorModel extends CursorModel {

        private SparseArrayCompat<Spanned> mSpannedCache;

        public StreamCursorModel(Cursor cursor) {
            super(cursor);
        }

        @Override
        public void doInBackground(Context context) {
            super.doInBackground(context);
            SparseArrayCompat<Spanned> spannedSparseArrayCompat = new SparseArrayCompat<>();
            mSpannedCache = spannedSparseArrayCompat;
            List<ContentValues> convertedList = new ArrayList<>();
            for (int i = 0; i < size(); i++) {
                CursorModel cursorModel = get(i);
                String contentText = cursorModel.getString(Content.CONTENT_TEXT);
                if (StringUtil.isEmpty(contentText)) {
                    continue;
                }
                if (i % 2 == 0) {
                    contentText = "<b>" + contentText + "</b>";
                }
                spannedSparseArrayCompat.put(i, Html.fromHtml(contentText));
                ContentValues contentValues = new ContentValues();
                CursorUtils.cursorRowToContentValues(cursorModel, contentValues);
                contentValues.put(Content.AUTHOR_DISPLAY_NAME, "--" + contentValues.getAsString(Content.AUTHOR_DISPLAY_NAME));
                convertedList.add(contentValues);
            }
            Cursor cursor = CursorUtils.listContentValuesToCursor(convertedList, PROJECTION);
            setCursor(cursor);
        }

        @Override
        public void close() {
            super.close();
            mSpannedCache = null;
        }

        public Spanned getContentText() {
            return mSpannedCache.get(getPosition());
        }
    }

    public static CursorModel.CursorModelCreator<StreamCursorModel> STREAM_CURSOR_CREATOR = new CursorModel.CursorModelCreator<StreamCursorModel>() {
        @Override
        public StreamCursorModel create(Cursor cursor) {
            return new StreamCursorModel(cursor);
        }
    };

    @Override
    protected Fragment createFragment() {
        return new StreamerFragment();
    }


    public static class StreamerFragment extends XListFragment<StreamCursorModel> {

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
            //return ModelContract.getSQLQueryUri("some sql code", ModelContract.getUri(Content.class));
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
        public CursorModel.CursorModelCreator<StreamCursorModel> getCursorModelCreator() {
            return STREAM_CURSOR_CREATOR;
        }

        @Override
        public String[] getProjection() {
            return PROJECTION;
        }

        @Override
        public String[] getAdapterColumns() {
            return ADAPTER_COLUMNS;
        }

        @Override
        public int[] getAdapterControlIds() {
            return ADAPTER_CONTROL_IDS;
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
        protected boolean setAdapterViewImage(ImageView v, String value) {
            if (StringUtil.isEmpty(value)) {
                v.setVisibility(View.GONE);
            } else {
                v.setVisibility(View.VISIBLE);
            }
            return super.setAdapterViewImage(v, value);
        }

        @Override
        protected boolean setAdapterViewText(TextView v, String value) {
            if (StringUtil.isEmpty(value)) {
                v.setVisibility(View.GONE);
            } else {
                v.setVisibility(View.VISIBLE);
            }
            return super.setAdapterViewText(v, value);
        }

        @Override
        public BaseAdapter createAdapter(FragmentActivity activity, Cursor cursor, int adapterLayout, String[] adapterColumns, int[] adapterControlIds) {
            return super.createAdapter(activity, cursor, adapterLayout, adapterColumns, adapterControlIds);
        }

        @Override
        public BaseAdapter createAdapter(FragmentActivity activity, Cursor cursor) {
            return super.createAdapter(activity, cursor);
        }

        @Override
        protected View onAdapterGetView(SimpleCursorAdapter simpleCursorAdapter, int position, View view) {
            View root = super.onAdapterGetView(simpleCursorAdapter, position, view);
            StreamCursorModel cursorModel = (StreamCursorModel) simpleCursorAdapter.getItem(position);
            ((TextView)root.findViewById(R.id.about)).setText(cursorModel.getContentText(), TextView.BufferType.SPANNABLE);
            return root;
        }
    }
}
