package by.istin.android.xcore.gson.external;

import android.content.ContentValues;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import by.istin.android.xcore.gson.AbstractValuesAdapter;

/**
 * Created with IntelliJ IDEA.
 * User: IstiN
 * Date: 25.10.13
 * Time: 18.26
 */
public class ArrayAdapter<T> extends TypeAdapter<List<T>> {

    private final AbstractValuesAdapter contentValuesAdapter;

    private final Class<T> adapterclass;

    private int listBufferSize;

    public ArrayAdapter(int listBufferSize, Class<T> adapterClass, AbstractValuesAdapter contentValuesAdapter) {
        this.adapterclass = adapterClass;
        this.listBufferSize = listBufferSize;
        this.contentValuesAdapter = contentValuesAdapter;
    }

    public List<T> read(JsonReader reader) throws IOException {
        boolean unlimitedBuffer = listBufferSize == -1;
        boolean nullBuffer = listBufferSize == 0;
        List<T> list = null;
        if (unlimitedBuffer) {
            list = new ArrayList<T>();
        } else if (!nullBuffer) {
            list = new ArrayList<T>(listBufferSize);
        }
        Gson gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(ContentValues.class, contentValuesAdapter)
                .registerTypeAdapterFactory(new ArrayAdapterFactory(listBufferSize, contentValuesAdapter))
                .create();

        if (reader.peek() == JsonToken.BEGIN_OBJECT) {
            T inning = gson.fromJson(reader, adapterclass);
            if (!nullBuffer) {
                list.add(inning);
            }
        } else if (reader.peek() == JsonToken.BEGIN_ARRAY) {
            reader.beginArray();
            int currentSize = 0;
            while (reader.hasNext()) {
                T inning = gson.fromJson(reader, adapterclass);
                if (!nullBuffer) {
                    list.add(inning);
                    currentSize++;
                    if (!unlimitedBuffer && currentSize == listBufferSize) {
                        list.clear();
                        currentSize = 0;
                    }
                }
            }
            reader.endArray();
        }

        return list;
    }

    public void write(JsonWriter writer, List<T> value) throws IOException {

    }


}
