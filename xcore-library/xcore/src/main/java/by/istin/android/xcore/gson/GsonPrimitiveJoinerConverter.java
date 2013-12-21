package by.istin.android.xcore.gson;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import by.istin.android.xcore.utils.Log;
import by.istin.android.xcore.utils.StringUtil;

/**
 * Created by IstiN on 6.12.13.
 */
public abstract class GsonPrimitiveJoinerConverter implements IGsonEntitiesConverter {

    public static final String KEY_VALUE = "value";

    @Override
    public void convert(Params params) {
        StringBuilder tagsBuilder = new StringBuilder();
        JsonArray jsonArray = params.getJsonArray();
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonElement item = jsonArray.get(i);
            tagsBuilder.append(item.getAsString());
            if (i != jsonArray.size()-1) {
                tagsBuilder.append(getSplitter());
            }
        }
        String result = tagsBuilder.toString();
        Log.xd(this, "tagsJsonConverter " + result);
        if (!StringUtil.isEmpty(result)) {
            params.getContentValues().put(getEntityKey(), result);
        }
    }

    public abstract String getSplitter();

    public abstract String getEntityKey();


}
