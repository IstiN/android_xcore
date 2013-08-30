package by.istin.android.xcore.model;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import by.istin.android.xcore.utils.CursorUtils;

/**
 * Created by Uladzimir_Klyshevich on 8/30/13.
 */
public class CursorModel implements Cursor {

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

}
