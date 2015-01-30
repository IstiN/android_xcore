package by.istin.android.xcore.model;

import android.provider.BaseColumns;

import com.google.gson.annotations.SerializedName;

import by.istin.android.xcore.annotations.dbEntities;
import by.istin.android.xcore.annotations.dbLong;
import by.istin.android.xcore.annotations.dbString;

/**
 * Created by IstiN on 13.11.13.
 */
public class SimpleEntityWithSubEntities implements BaseColumns {

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

    @dbEntities(clazz = SimpleEntityWithParent1.class)
    @SerializedName(value = "sub_entities1")
    public static final String SUB_ENTITIES1 = "sub_entities1";

    @dbEntities(clazz = SimpleEntityWithParent2.class)
    @SerializedName(value = "sub_entities2")
    public static final String SUB_ENTITIES2 = "sub_entities2";


}
