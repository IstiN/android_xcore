package by.istin.android.xcore.gson.external;

import by.istin.android.xcore.gson.ContentValuesAdapter;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: IstiN
 * Date: 25.10.13
 */
public class ArrayAdapterFactory implements TypeAdapterFactory {

    private final ContentValuesAdapter contentValuesAdapter;

    public ArrayAdapterFactory(ContentValuesAdapter contentValuesAdapter) {
        this.contentValuesAdapter = contentValuesAdapter;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> type) {

        TypeAdapter<T> typeAdapter = null;

        try {
            if (type.getRawType() == List.class) {
                typeAdapter = new ArrayAdapter(
                        (Class) ((ParameterizedType) type.getType())
                                .getActualTypeArguments()[0], contentValuesAdapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return typeAdapter;


    }

}
