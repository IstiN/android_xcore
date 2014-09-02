package by.istin.android.xcore.gson;

import android.content.ContentValues;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import by.istin.android.xcore.annotations.dbEntities;
import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.db.entity.IBeforeArrayUpdate;
import by.istin.android.xcore.db.impl.DBHelper;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.utils.ReflectUtils;

/**
 * Created by IstiN on 6.12.13.
 */
class GsonEntitiesConverter implements IGsonEntitiesConverter {

    public static final GsonEntitiesConverter INSTANCE = new GsonEntitiesConverter();

    @Override
    public void convert(IGsonEntitiesConverter.Params params) {
        DBContentValuesAdapter contentValuesAdapter = new DBContentValuesAdapter(params.getClazz(), params.getDataSourceRequest(), params.getDbConnection(), params.getDbHelper());
        int size = params.getJsonArray().size();
        dbEntities entity = null;
        Boolean ignorePrimitive = null;
        IBeforeArrayUpdate beforeListUpdate = params.getBeforeListUpdate();
        DBHelper dbHelper = params.getDbHelper();
        DataSourceRequest dataSourceRequest = params.getDataSourceRequest();
        IDBConnection dbConnection = params.getDbConnection();
        Class<?> clazz = params.getClazz();
        for (int i = 0; i < size; i++) {
            JsonElement item = params.getJsonArray().get(i);
            ContentValues subEntity;
            if (item.isJsonPrimitive()) {
                if (entity == null) {
                    entity = ReflectUtils.getAnnotation(params.getField(), dbEntities.class);
                    ignorePrimitive = entity.ignorePrimitive();
                }
                if (ignorePrimitive) continue;
                JsonParser parser = new JsonParser();
                String itemAsString = item.getAsString();
                if (itemAsString.contains("\"")) {
                    itemAsString = itemAsString.replace("\"", "&quot;");
                }
                item = parser.parse("{\"value\": \"" + itemAsString + "\"}");
                subEntity = contentValuesAdapter.deserializeContentValues(params.getContentValues(), i, item, params.getType(), params.getJsonDeserializationContext());
            } else {
                subEntity = contentValuesAdapter.deserializeContentValues(params.getContentValues(), i, item, params.getType(), params.getJsonDeserializationContext());
            }
            if (subEntity == null) {
                continue;
            }
            subEntity.put(params.getForeignKey(), params.getId());
            if (beforeListUpdate != null) {
                beforeListUpdate.onBeforeListUpdate(dbHelper, dbConnection, dataSourceRequest, i, subEntity);
            }
            dbHelper.updateOrInsert(dataSourceRequest, dbConnection, clazz, subEntity);
        }
    }

}
