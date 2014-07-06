package by.istin.android.xcore.gson;

import android.content.ContentValues;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;

import java.lang.reflect.Type;

/**
 * Created by IstiN on 6.12.13.
 */
public interface IConverter {

    void convert(ContentValues contentValues, String fieldValue, Object parent, JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext);
}
