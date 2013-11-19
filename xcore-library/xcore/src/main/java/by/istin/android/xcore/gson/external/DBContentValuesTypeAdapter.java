package by.istin.android.xcore.gson.external;

import android.content.ContentValues;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import by.istin.android.xcore.gson.TypeContentValues;
import by.istin.android.xcore.gson.DBContentValuesAdapter;

/**
 * Created with IntelliJ IDEA.
 * User: IstiN
 * Date: 25.10.13
 * Time: 18.26
 */
public class DBContentValuesTypeAdapter extends TypeAdapter<TypeContentValues> {

    private final DBContentValuesAdapter contentValuesAdapter;

    private Class<?> adapterClass;


    public DBContentValuesTypeAdapter(Class<?> adapterClass, DBContentValuesAdapter contentValuesAdapter) {
        this.adapterClass = adapterClass;
        this.contentValuesAdapter = contentValuesAdapter;
    }

    @Override
    public void write(JsonWriter jsonWriter, TypeContentValues tdbContentValues) throws IOException {

    }

    @Override
    public TypeContentValues read(JsonReader reader) throws IOException {
        Gson gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(ContentValues.class, contentValuesAdapter)
                .registerTypeAdapterFactory(new ArrayAdapterFactory(contentValuesAdapter))
                .create();

        if (reader.peek() == JsonToken.BEGIN_OBJECT) {
            gson.fromJson(reader, adapterClass);
        } else if (reader.peek() == JsonToken.BEGIN_ARRAY) {
            reader.beginArray();
            while (reader.hasNext()) {
                gson.fromJson(reader, adapterClass);
            }
            reader.endArray();
        }
        return null;
    }
}
