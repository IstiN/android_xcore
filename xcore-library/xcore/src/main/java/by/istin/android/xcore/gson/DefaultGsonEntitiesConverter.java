package by.istin.android.xcore.gson;

import android.content.ContentValues;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import by.istin.android.xcore.annotations.dbEntities;

/**
 * Created by IstiN on 6.12.13.
 */
public class DefaultGsonEntitiesConverter implements IGsonEntitiesConverter {

    public static final String KEY_VALUE = "value";

    @Override
    public void convert(IGsonEntitiesConverter.Params params) {
        DBContentValuesAdapter contentValuesAdapter = new DBContentValuesAdapter(params.getClazz(), params.getDataSourceRequest(), params.getDbConnection(), params.getDbHelper());
        for (int i = 0; i < params.getJsonArray().size(); i++) {
            JsonElement item = params.getJsonArray().get(i);
            ContentValues subEntity;
            if (item.isJsonPrimitive()) {
                dbEntities entity = params.getEntity();
                if (entity.ignorePrimitive()) continue;
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
            if (params.getBeforeListUpdate() != null) {
                params.getBeforeListUpdate().onBeforeListUpdate(params.getDbHelper(), params.getDbConnection(), params.getDataSourceRequest(), i, subEntity);
            }
            params.getDbHelper().updateOrInsert(params.getDataSourceRequest(), params.getDbConnection(), params.getClazz(), subEntity);
        }
    }

}
