package by.istin.android.xcore.model;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

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
            items[i] = (ContentValues)parcelable;
            i++;
        }
        return items;
    }
    public static void writeContentValuesArray(Parcel dest, ContentValues[] contentValueses) {
        dest.writeParcelableArray(contentValueses, 0);
    }
}
