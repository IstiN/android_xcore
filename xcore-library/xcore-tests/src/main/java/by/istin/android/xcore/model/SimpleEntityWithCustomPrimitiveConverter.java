package by.istin.android.xcore.model;

import android.provider.BaseColumns;

import com.google.gson.annotations.SerializedName;

import by.istin.android.xcore.annotations.dbEntities;
import by.istin.android.xcore.annotations.dbLong;
import by.istin.android.xcore.annotations.dbString;
import by.istin.android.xcore.gson.GsonPrimitiveJoinerConverter;

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

    //for processing primitive types
    @dbEntities(clazz = Object.class, contentValuesKey = "stubValues", jsonConverter = TagJsonConverter.class)
    @SerializedName(value = "tags")
    public static final String TAGS_FOR_PROCESSING = "tags_for_processing";

    @dbString
    //for insert to DB primitive types
    public static final String TAGS = "tags";


    public static class TagJsonConverter extends GsonPrimitiveJoinerConverter {

        @Override
        public String getSplitter() {
            return ", ";
        }

        @Override
        public String getEntityKey() {
            return TAGS;
        }

    }


}
