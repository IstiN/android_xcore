package by.istin.android.xcore.model;

import android.provider.BaseColumns;

import com.google.gson.annotations.SerializedName;

import by.istin.android.xcore.annotations.dbLong;
import by.istin.android.xcore.annotations.dbString;
import by.istin.android.xcore.db.impl.DBHelper;

public class BigTestSubEntity implements BaseColumns {

    @dbLong
    @SerializedName(value = "id")
    public static String ID = _ID;

    @dbLong
    public static String TEST_ENTITY_ID = DBHelper.getForeignKey(BigTestEntity.class);

    @dbString
    public static String STRING_VALUE = "STRING_VALUE";

}