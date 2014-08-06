package by.istin.android.xcore.issues.issue12.model;

import android.content.ContentValues;
import android.provider.BaseColumns;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

import by.istin.android.xcore.annotations.Config;
import by.istin.android.xcore.annotations.converter.IConverter;
import by.istin.android.xcore.annotations.converter.gson.GsonConverter;
import by.istin.android.xcore.annotations.dbEntity;
import by.istin.android.xcore.annotations.dbLong;
import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.db.entity.IGenerateID;
import by.istin.android.xcore.db.impl.DBHelper;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.utils.HashUtils;

/**
 * Created by IstiN on 13.11.13.
 */
@dbEntity(clazz = DayEntity.class, value = @Config(dbType = Config.DBType.ENTITY, transformer = DayEntity.DayEntityConverter.class))
public class DayEntity implements BaseColumns, IGenerateID {

    public static class DayEntityConverter extends Config.AbstractTransformer<GsonConverter.Meta> {

        @Override
        public IConverter<GsonConverter.Meta> converter() {
            return new IConverter<GsonConverter.Meta>() {
                @Override
                public void convert(ContentValues contentValues, String fieldValue, Object parent, GsonConverter.Meta meta) {
                    JsonElement jsonElement = meta.getJsonElement();
                    if (jsonElement.isJsonPrimitive()) {
                        contentValues.put(VALUE, jsonElement.getAsLong());
                    }
                }
            };
        }

    }

    @dbLong
    @SerializedName("id")
    public static final String ID = _ID;

    @dbLong
    @SerializedName("value")
    public static final String VALUE = "value";

    @Override
    public long generateId(DBHelper dbHelper, IDBConnection db, DataSourceRequest dataSourceRequest, ContentValues contentValues) {
        return HashUtils.generateId(contentValues.getAsString(VALUE));
    }
}
