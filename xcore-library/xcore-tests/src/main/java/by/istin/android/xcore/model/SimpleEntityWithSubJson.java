package by.istin.android.xcore.model;

import android.provider.BaseColumns;

import com.google.gson.annotations.SerializedName;

import by.istin.android.xcore.annotations.dbLong;
import by.istin.android.xcore.annotations.dbString;

/**
 * Created by IstiN on 13.11.13.
 */
public class SimpleEntityWithSubJson implements BaseColumns {

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

    @dbLong
    @SerializedName(value = "sub_entity:id")
    public static final String SUB_ID = "sub_entity_id";

    @dbString
    @SerializedName(value = "sub_entity:title")
    public static final String SUB_TITLE = "sub_entity_title";

    @dbString
    @SerializedName(value = "sub_entity:about")
    public static final String SUB_ABOUT = "sub_entity_about";

    @dbString
    @SerializedName(value = "sub_entity:image_url")
    public static final String SUB_IMAGE_URL = "sub_entity_image_url";

}
