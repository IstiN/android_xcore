package by.istin.android.xcore.utils;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Spanned;
import android.text.TextUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class BytesUtils {

    public static byte[] toByteArray(ContentValues contentValues) {
        Parcel obtain = Parcel.obtain();
        contentValues.writeToParcel(obtain, 0);
        byte[] byteArray = obtain.marshall();
        obtain.recycle();
        return byteArray;
    }

    public static byte[] toByteArray(Bundle bundle) {
        Parcel obtain = Parcel.obtain();
        bundle.writeToParcel(obtain, 0);
        byte[] byteArray = obtain.marshall();
        obtain.recycle();
        return byteArray;
    }

    public static byte[] toByteArray(Intent intent) {
        Parcel obtain = Parcel.obtain();
        intent.writeToParcel(obtain, 0);
        byte[] byteArray = obtain.marshall();
        obtain.recycle();
        return byteArray;
    }

    public static byte[] toByteArray(Parcelable parcelable) {
        Parcel obtain = Parcel.obtain();
        parcelable.writeToParcel(obtain, 0);
        byte[] byteArray = obtain.marshall();
        obtain.recycle();
        return byteArray;
    }

    public static byte[] toByteArray(Spanned spanned) {
        Parcel obtain = Parcel.obtain();
        TextUtils.writeToParcel(spanned, obtain, 0);
        byte[] byteArray = obtain.marshall();
        obtain.recycle();
        return byteArray;
    }

    public static byte[] arrayToByteArray(ContentValues[] contentValues) {
        Parcel obtain = Parcel.obtain();
        obtain.writeParcelableArray(contentValues, 0);
        byte[] byteArray = obtain.marshall();
        obtain.recycle();
        return byteArray;
    }

    public static ContentValues contentValuesFromByteArray(byte[] byteArray) {
        Parcel obtain = Parcel.obtain();
        obtain.unmarshall(byteArray, 0, byteArray.length);
        obtain.setDataPosition(0);
        ContentValues createFromParcel = ContentValues.CREATOR.createFromParcel(obtain);
        obtain.recycle();
        return createFromParcel;
    }

    public static <T extends Parcelable> T parcelableFromByteArray(Parcelable.Creator<T> creator, byte[] byteArray) {
        Parcel obtain = Parcel.obtain();
        obtain.unmarshall(byteArray, 0, byteArray.length);
        obtain.setDataPosition(0);
        T createFromParcel = creator.createFromParcel(obtain);
        obtain.recycle();
        return createFromParcel;
    }

    public static Intent intentFromByteArray(byte[] byteArray) {
        Parcel obtain = Parcel.obtain();
        obtain.unmarshall(byteArray, 0, byteArray.length);
        obtain.setDataPosition(0);
        Intent createFromParcel = Intent.CREATOR.createFromParcel(obtain);
        obtain.recycle();
        return createFromParcel;
    }

    public static Spanned spannedFromByteArray(byte[] byteArray) {
        if (byteArray == null) {
            return null;
        }
        Parcel obtain = Parcel.obtain();
        obtain.unmarshall(byteArray, 0, byteArray.length);
        obtain.setDataPosition(0);
        Spanned result = (Spanned) TextUtils.CHAR_SEQUENCE_CREATOR.
                createFromParcel(obtain);
        obtain.recycle();
        return result;
    }

    public static Bundle bundleFromByteArray(byte[] byteArray) {
        Parcel obtain = Parcel.obtain();
        obtain.unmarshall(byteArray, 0, byteArray.length);
        obtain.setDataPosition(0);
        Bundle result = Bundle.CREATOR.createFromParcel(obtain);
        obtain.recycle();
        return result;
    }

    public static ContentValues[] arrayContentValuesFromByteArray(byte[] byteArray) {
        Parcel obtain = Parcel.obtain();
        obtain.unmarshall(byteArray, 0, byteArray.length);
        obtain.setDataPosition(0);
        Parcelable[] contentValues = obtain.readParcelableArray(ContentValues.class.getClassLoader());
        ContentValues[] values = new ContentValues[contentValues.length];
        for (int i = 0; i < contentValues.length; i++) {
            values[i] = (ContentValues) contentValues[i];
        }
        obtain.recycle();
        return values;
    }

    public static byte[] toByteArray(Serializable serializable) {
        if (serializable == null) {
            return null;
        }
        ByteArrayOutputStream bos = null;
        ObjectOutput out = null;
        try {
            bos = new ByteArrayOutputStream();
            out = new ObjectOutputStream(bos);
            out.writeObject(serializable);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        } finally {
            IOUtils.close(bos);
            IOUtils.close(out);
        }
    }

    public static Serializable serializableFromByteArray(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        ByteArrayInputStream bis = null;
        ObjectInput in = null;
        try {
            bis = new ByteArrayInputStream(bytes);
            in = new ObjectInputStream(bis);
            Serializable o;

            o = (Serializable) in.readObject();
            return o;
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        } finally {
            IOUtils.close(bis);
            IOUtils.close(in);
        }
    }

}