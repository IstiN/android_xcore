package by.istin.android.xcore.model;

import android.content.ContentValues;
import android.provider.BaseColumns;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;

import by.istin.android.xcore.annotations.Config;
import by.istin.android.xcore.annotations.db;
import by.istin.android.xcore.annotations.dbLong;
import by.istin.android.xcore.annotations.dbString;
import by.istin.android.xcore.gson.IConverter;
import by.istin.android.xcore.utils.Log;
import by.istin.android.xcore.utils.StringUtil;

/**
 * Created by IstiN on 13.11.13.
 */
public class SimpleEntityWithCustomPrimitiveConverter implements BaseColumns {

    @dbLong
    @SerializedName(value = "id")
    public static final String ID = _ID;

    @dbString
    @SerializedName(value = "title")
    public static final String TITLE = "title";

    @dbString
    @SerializedName(value = "about")
    public static final String ABOUT = "about";

    @dbString
    @SerializedName(value = "image_url")
    public static final String IMAGE_URL = "image_url";

    @db(@Config(dbType = Config.DBType.STRING, transformer = TagsTransformer.class))
    public static final String TAGS = "tags";

    public static class TagsTransformer extends Config.DefaultTransformer {

        @Override
        public IConverter converter() {
            return new IConverter() {
                @Override
                public void convert(ContentValues contentValues, String fieldValue, Object parent, JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
                    if (jsonElement.isJsonNull()) {
                        return;
                    }
                    StringBuilder tagsBuilder = new StringBuilder();
                    JsonArray jsonArray = jsonElement.getAsJsonArray();
                    int size = jsonArray.size();
                    for (int i = 0; i < size; i++) {
                        JsonElement item = jsonArray.get(i);
                        tagsBuilder.append(item.getAsString());
                        if (i != size -1) {
                            tagsBuilder.append(", ");
                        }
                    }
                    String result = tagsBuilder.toString();
                    Log.xd(this, "tagsJsonConverter " + result);
                    if (!StringUtil.isEmpty(result)) {
                        contentValues.put(fieldValue, result);
                    }
                }
            };
        }

    }

}
