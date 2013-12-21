package by.istin.android.xcore.model;

import android.provider.BaseColumns;

import com.google.gson.annotations.SerializedName;

import by.istin.android.xcore.annotations.dbLong;
import by.istin.android.xcore.annotations.dbString;
import by.istin.android.xcore.db.impl.DBHelper;

/**
 * Created by IstiN on 13.11.13.
 */
public class SimpleEntityWithParent2 implements BaseColumns {

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

    //local
    @dbLong
    public static final String SIMPLE_ENTITY_PARENT = DBHelper.getForeignKey(SimpleEntityWithSubEntities.class);

}
