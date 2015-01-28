package by.istin.android.xcore.model;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * example https://github.com/wrt-mobi/OReader-Android/blob/f461b3961e5a4a282eae356fb936848090071f6c/app/src/main/java/mobi/wrt/oreader/app/clients/feedly/processor/ContentProcessor.java
 */
public abstract class ParcelableModel implements Parcelable {

    @Override
    public int describeContents() {
        return 0;
    }

    public static ContentValues[] readContentValuesArray(final Parcel source) {
        Parcelable[] parcelables = source.readParcelableArray(ContentValues.class.getClassLoader());
        if (parcelables == null) {
            return null;
        }
        ContentValues[] items = new ContentValues[parcelables.length];
        int i = 0;
        for (Parcelable parcelable : parcelables) {
            items[i] = (ContentValues) parcelable;
            i++;
        }
        return items;
    }

    public static List<ContentValues> readContentValuesList(final Parcel source) {
        Parcelable[] parcelables = source.readParcelableArray(ContentValues.class.getClassLoader());
        if (parcelables == null) {
            return null;
        }
        List<ContentValues> items = new ArrayList<>();
        for (Parcelable parcelable : parcelables) {
            items.add((ContentValues) parcelable);
        }
        return items;
    }

    public static ContentValues readContentValues(final Parcel source) {
        Parcelable parcelables = source.readParcelable(ContentValues.class.getClassLoader());
        if (parcelables == null) {
            return null;
        }
        return (ContentValues) parcelables;
    }

    public static void writeContentValuesArray(Parcel dest, ContentValues[] contentValueses) {
        dest.writeParcelableArray(contentValueses, 0);
    }

    public static void writeContentValues(Parcel dest, ContentValues contentValueses) {
        dest.writeParcelable(contentValueses, 0);
    }
}
