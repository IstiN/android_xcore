package by.istin.android.xcore.processor;

import android.content.ContentValues;
import android.support.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.db.impl.DBHelper;
import by.istin.android.xcore.source.DataSourceRequest;

/**
 * Interface needs to be implemented if you want to manipulate with content values before inserting to DataBase during parsing entity with AbstractGsonBatchProcessor.
 */
public interface IOnProceedEntity {

    /**
     * Call for every item before insert in database. Return true if you make custom insert item to database and don't need todo it automatically
     *
     * @param dbHelper          instance of DBHelper
     * @param db                current WritableTransaction
     * @param dataSourceRequest current dataSourceRequest
     * @param parent            parent for current entity
     * @param contentValues     current ContentValues that will be inserting to the DataBase
     * @param position          current position if it's jsonarray, position = -1 if it's single entity
     * @param jsonElement       current jsonObject
     * @return false if you want to insert or update item automatically, true if you do this by yourself
     */
    boolean onProceedEntity(DBHelper dbHelper, IDBConnection db, DataSourceRequest dataSourceRequest, @Nullable ContentValues parent, ContentValues contentValues, int position, JsonElement jsonElement);

}
