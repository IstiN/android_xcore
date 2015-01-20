package by.istin.android.xcore.sample.core.model;

import android.content.ContentValues;
import android.provider.BaseColumns;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import by.istin.android.xcore.annotations.Config;
import by.istin.android.xcore.annotations.converter.IConverter;
import by.istin.android.xcore.annotations.converter.gson.GsonConverter;
import by.istin.android.xcore.annotations.db;
import by.istin.android.xcore.annotations.dbFormattedDate;
import by.istin.android.xcore.annotations.dbInteger;
import by.istin.android.xcore.annotations.dbLong;
import by.istin.android.xcore.annotations.dbString;
import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.db.entity.IBeforeArrayUpdate;
import by.istin.android.xcore.db.impl.DBHelper;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.utils.StringUtil;

public class Content implements BaseColumns, IBeforeArrayUpdate {

    @SerializedName("id")
    @dbLong
    public static final String ID = _ID;

    //AUTHOR
    @dbString
    @SerializedName("author:network")
    public static final String NETWORK = "author_network";

    @dbString
    @SerializedName("author:displayName")
    public static final String AUTHOR_DISPLAY_NAME = "author_displayName";

    @dbString
    @SerializedName("author:avatar:url")
    public static final String AUTHOR_AVATAR_URL = "author_avatar_url";

    //CONTENT
    @dbString
    public static final String CONTENT_TEXT = "content_text";

    @dbString
    @SerializedName("content:title")
    public static final String CONTENT_TITLE = "content_title";

    @dbString
    @SerializedName("content:comment")
    public static final String CONTENT_COMMENT = "content_comment";

    @dbString
    @SerializedName("content:description")
    public static final String CONTENT_DESCRIPTION = "content_description";

    @dbString
    public static final String TIMESTAMP_FORMATTED = "timestamp_formatted";

    @dbFormattedDate(isUnix = true, format = "yyyy-MM-dd HH:mm", contentValuesKey = TIMESTAMP_FORMATTED)
    public static final String TIMESTAMP = "timestamp";

    @dbInteger
    public static final String ATTACHS_COUNT = "attachs_count";

    @db(@Config(dbType = Config.DBType.STRING, transformer = Transformer.class, key = "content:media"))
    public static final String MAIN_CONTENT_IMAGE = "main_content_image";

    public static class Transformer extends Config.AbstractTransformer<GsonConverter.Meta> {

        public static final IConverter<GsonConverter.Meta> CONVERTER = new GsonConverter() {
            @Override
            public void convert(ContentValues contentValues, String fieldValue, Object parent, Meta meta) {
                JsonElement jsonValue = meta.getJsonElement();
                if (jsonValue.isJsonNull()) {
                    return;
                }
                JsonObject jsonObject = jsonValue.getAsJsonObject();
                JsonElement photosElement = jsonObject.get("photos");
                if (photosElement.isJsonNull()) {
                    return;
                }
                JsonArray photos = photosElement.getAsJsonArray();
                if (photos == null || photos.size() == 0) {
                    return;
                }
                JsonElement jsonElement = photos.get(0);
                JsonObject photoAsObject = jsonElement.getAsJsonObject();
                JsonElement url = photoAsObject.get("url");
                contentValues.put(fieldValue, url.getAsString());

                contentValues.put(ATTACHS_COUNT, photos.size());
            }
        };

        @Override
        public IConverter<GsonConverter.Meta> converter() {
            return CONVERTER;
        }

    }


    //LOCAL
    @dbInteger
    public static final String POSITION = "position";

    @Override
    public void onBeforeListUpdate(DBHelper dbHelper, IDBConnection db, DataSourceRequest dataSourceRequest, int position, ContentValues contentValues) {
        contentValues.put(ID, position);
        contentValues.put(POSITION, position);
        String title = contentValues.getAsString(CONTENT_TITLE);
        String description = contentValues.getAsString(CONTENT_DESCRIPTION);
        String comment = contentValues.getAsString(CONTENT_COMMENT);
        if (!StringUtil.isEmpty(title)) {
            contentValues.put(CONTENT_TEXT, title);
        } else if (!StringUtil.isEmpty(description)) {
            contentValues.put(CONTENT_TEXT, description);
        } else if (!StringUtil.isEmpty(comment)) {
            contentValues.put(CONTENT_TEXT, comment);
        }
    }
}
