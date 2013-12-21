package by.istin.android.xcore.gson.external;

import android.content.ContentValues;
import by.istin.android.xcore.gson.ContentValuesAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: IstiN
 * Date: 25.10.13
 * Time: 18.26
 */
public class ArrayAdapter<T> extends TypeAdapter<List<T>> {

    private final ContentValuesAdapter contentValuesAdapter;

    private Class<T> adapterclass;


    public ArrayAdapter(Class<T> adapterclass, ContentValuesAdapter contentValuesAdapter) {
        this.adapterclass = adapterclass;
        this.contentValuesAdapter = contentValuesAdapter;
    }

    public List<T> read(JsonReader reader) throws IOException {

        List<T> list = new ArrayList<T>();

        Gson gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(ContentValues.class, contentValuesAdapter)
                .registerTypeAdapterFactory(new ArrayAdapterFactory(contentValuesAdapter))
                .create();

        if (reader.peek() == JsonToken.BEGIN_OBJECT) {
            T inning = gson.fromJson(reader, adapterclass);
            list.add(inning);

        } else if (reader.peek() == JsonToken.BEGIN_ARRAY) {
            reader.beginArray();
            while (reader.hasNext()) {
                T inning = gson.fromJson(reader, adapterclass);
                list.add(inning);
            }
            reader.endArray();
        }

        return list;
    }

    public void write(JsonWriter writer, List<T> value) throws IOException {

    }


}
