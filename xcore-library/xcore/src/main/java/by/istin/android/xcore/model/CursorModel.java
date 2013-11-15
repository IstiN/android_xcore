package by.istin.android.xcore.model;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import by.istin.android.xcore.utils.CursorUtils;

/**
 * Created by Uladzimir_Klyshevich on 8/30/13.
 */
public class CursorModel implements Cursor, List<Cursor> {

    public static interface CursorModelCreator {
        CursorModel create(Cursor cursor);

        public static CursorModelCreator DEFAULT = new CursorModelCreator() {
            @Override
            public CursorModel create(Cursor cursor) {
                return new CursorModel(cursor);
            }
        };
    }

    private Cursor mCursor;

    public CursorModel(Cursor cursor) {
        this.mCursor = cursor;
        this.mCursor.moveToFirst();
    }

    @Override
    public int getCount() {
        return mCursor.getCount();
    }

    @Override
    public int getPosition() {
        return mCursor.getPosition();
    }

    @Override
    public boolean move(int i) {
        return mCursor.move(i);
    }

    @Override
    public boolean moveToPosition(int i) {
        return mCursor.moveToPosition(i);
    }

    @Override
    public boolean moveToFirst() {
        return mCursor.moveToFirst();
    }

    @Override
    public boolean moveToLast() {
        return mCursor.moveToLast();
    }

    @Override
    public boolean moveToNext() {
        return mCursor.moveToNext();
    }

    @Override
    public boolean moveToPrevious() {
        return mCursor.moveToPrevious();
    }

    @Override
    public boolean isFirst() {
        return mCursor.isFirst();
    }

    @Override
    public boolean isLast() {
        return mCursor.isLast();
    }

    @Override
    public boolean isBeforeFirst() {
        return mCursor.isBeforeFirst();
    }

    @Override
    public boolean isAfterLast() {
        return mCursor.isAfterLast();
    }

    @Override
    public int getColumnIndex(String s) {
        return mCursor.getColumnIndex(s);
    }

    @Override
    public int getColumnIndexOrThrow(String s) throws IllegalArgumentException {
        return mCursor.getColumnIndexOrThrow(s);
    }

    @Override
    public String getColumnName(int i) {
        return mCursor.getColumnName(i);
    }

    @Override
    public String[] getColumnNames() {
        return mCursor.getColumnNames();
    }

    @Override
    public int getColumnCount() {
        return mCursor.getColumnCount();
    }

    @Override
    public byte[] getBlob(int i) {
        return mCursor.getBlob(i);
    }

    @Override
    public String getString(int i) {
        return mCursor.getString(i);
    }

    @Override
    public void copyStringToBuffer(int i, CharArrayBuffer charArrayBuffer) {
        mCursor.copyStringToBuffer(i, charArrayBuffer);
    }

    @Override
    public short getShort(int i) {
        return mCursor.getShort(i);
    }

    @Override
    public int getInt(int i) {
        return mCursor.getInt(i);
    }

    @Override
    public long getLong(int i) {
        return mCursor.getLong(i);
    }

    @Override
    public float getFloat(int i) {
        return mCursor.getFloat(i);
    }

    @Override
    public double getDouble(int i) {
        return mCursor.getDouble(i);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public int getType(int i) {
        return mCursor.getType(i);
    }

    @Override
    public boolean isNull(int i) {
        return mCursor.isNull(i);
    }

    @Override
    public void deactivate() {
        mCursor.deactivate();
    }

    @Override
    public boolean requery() {
        return mCursor.requery();
    }

    @Override
    public void close() {
        mCursor.close();
    }

    @Override
    public boolean isClosed() {
        return mCursor.isClosed();
    }

    @Override
    public void registerContentObserver(ContentObserver contentObserver) {
        mCursor.registerContentObserver(contentObserver);
    }

    @Override
    public void unregisterContentObserver(ContentObserver contentObserver) {
        mCursor.unregisterContentObserver(contentObserver);
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {
        mCursor.registerDataSetObserver(dataSetObserver);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
        mCursor.unregisterDataSetObserver(dataSetObserver);
    }

    @Override
    public void setNotificationUri(ContentResolver contentResolver, Uri uri) {
        mCursor.setNotificationUri(contentResolver, uri);
    }

    @Override
    public boolean getWantsAllOnMoveCalls() {
        return mCursor.getWantsAllOnMoveCalls();
    }

    @Override
    public Bundle getExtras() {
        return mCursor.getExtras();
    }

    @Override
    public Bundle respond(Bundle bundle) {
        return mCursor.respond(bundle);
    }

    public Long getLong(String key) {
        return CursorUtils.getLong(key, mCursor);
    }

    public byte[] getBlob(String key) {
        return CursorUtils.getBlob(key, mCursor);
    }

    public Integer getInt(String key) {
        return CursorUtils.getInt(key, mCursor);
    }

    public Byte getByte(String key) {
        return CursorUtils.getByte(key, mCursor);
    }

    public Double getDouble(String key) {
        return CursorUtils.getDouble(key, mCursor);
    }

    public Float getFloat(String key) {
        return CursorUtils.getFloat(key, mCursor);
    }

    public Short getShort(String key) {
        return CursorUtils.getShort(key, mCursor);
    }

    public String getString(String key) {
        return CursorUtils.getString(key, mCursor);
    }

    @Override
    public boolean add(Cursor object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends Cursor> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEmpty() {
        return CursorUtils.isEmpty(mCursor);
    }

    @Override
    public Iterator<Cursor> iterator() {
        return new Iterator<Cursor>() {
            @Override
            public boolean hasNext() {
                return !CursorUtils.isEmpty(mCursor) && !mCursor.isLast();
            }

            @Override
            public Cursor next() {
                mCursor.moveToNext();
                return mCursor;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public boolean remove(Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return CursorUtils.getSize(mCursor);
    }

    @Override
    public ContentValues[] toArray() {
        List<ContentValues> list = new ArrayList<ContentValues>();
        CursorUtils.convertToContentValues(mCursor, list, CursorUtils.Converter.get());
        return list.toArray(new ContentValues[list.size()]);
    }

    @Override
    public <T> T[] toArray(T[] array) {
        throw new UnsupportedOperationException("use toArray method instead of");
    }

    @Override
    public void add(int location, Cursor object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int location, Collection<? extends Cursor> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CursorModel get(int location) {
        mCursor.moveToPosition(location);
        return this;
    }

    @Override
    public int indexOf(Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int lastIndexOf(Object object) {
        throw new UnsupportedOperationException();
    }

    private class CursorListIterator implements ListIterator<Cursor> {

        private int currentPostion = 0;

        private CursorListIterator(int intialPostion) {
            this.currentPostion = intialPostion;
            mCursor.moveToPosition(currentPostion);
        }

        @Override
        public void add(Cursor object) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean hasNext() {
            return currentPostion < CursorUtils.getSize(mCursor);
        }

        @Override
        public boolean hasPrevious() {
            return currentPostion != 0;
        }

        @Override
        public Cursor next() {
            currentPostion = nextIndex();
            mCursor.moveToPosition(currentPostion);
            return mCursor;
        }

        @Override
        public int nextIndex() {
            return currentPostion+1;
        }

        @Override
        public Cursor previous() {
            currentPostion = previousIndex();
            mCursor.moveToPosition(currentPostion);
            return mCursor;
        }

        @Override
        public int previousIndex() {
            return currentPostion -1;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void set(Cursor object) {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public ListIterator<Cursor> listIterator() {
        return new CursorListIterator(0);
    }

    @Override
    public ListIterator<Cursor> listIterator(int location) {
        return new CursorListIterator(location);
    }

    @Override
    public Cursor remove(int location) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Cursor set(int location, Cursor object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Cursor> subList(int start, int end) {
        throw new UnsupportedOperationException();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public Uri getNotificationUri() {
        return mCursor.getNotificationUri();
    }
}
