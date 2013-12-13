package by.istin.android.xcore.issues.issue12.model;

import android.content.ContentValues;
import android.provider.BaseColumns;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;

import by.istin.android.xcore.annotations.JsonEntityConverter;
import by.istin.android.xcore.annotations.dbLong;
import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.db.entity.IGenerateID;
import by.istin.android.xcore.db.impl.DBHelper;
import by.istin.android.xcore.gson.IGsonEntityConverter;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.utils.HashUtils;

/**
 * Created by IstiN on 13.11.13.
 */
@JsonEntityConverter(converter = DayEntity.DayEntityConverter.class)
public class DayEntity implements BaseColumns, IGenerateID {

    public static class DayEntityConverter implements IGsonEntityConverter {

        @Override
        public void convert(ContentValues contentValues, Object parent, JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
            if (jsonElement.isJsonPrimitive()) {
                contentValues.put(VALUE, jsonElement.getAsLong());
            }
        }

    }

    @dbLong
    @SerializedName(value = "id")
    public static final String ID = _ID;

    @dbLong
    @SerializedName(value = "value")
    public static final String VALUE = "value";

    @Override
    public long generateId(DBHelper dbHelper, IDBConnection db, DataSourceRequest dataSourceRequest, ContentValues contentValues) {
        return HashUtils.generateId(contentValues.getAsString(VALUE));
    }
}
