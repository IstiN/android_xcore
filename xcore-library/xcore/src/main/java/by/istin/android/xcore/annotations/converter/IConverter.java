package by.istin.android.xcore.annotations.converter;

import android.content.ContentValues;

/**
 * Converter used in the Config annotation to convert data to the specific ContentValues data.
 * @param <Meta> meta additional information for conversion
 */
public interface IConverter<Meta> {

    /**
     * As result need to add some additional values to the ContentValues, or doesn't do anything if not need it.
     * @param contentValues current contentValues
     * @param fieldValue current field value that specified in ModelContract entity
     * @param parent parent entity, in most cases need to be ContentValues
     * @param meta specific Meta information for different parsers like Gson, Json, Xml and etc.
     */
    void convert(ContentValues contentValues, String fieldValue, Object parent, Meta meta);
}
