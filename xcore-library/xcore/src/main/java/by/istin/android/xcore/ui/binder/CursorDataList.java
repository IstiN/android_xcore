package by.istin.android.xcore.ui.binder;

import android.database.Cursor;
import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import by.istin.android.xcore.model.CursorModel;
import by.istin.android.xcore.utils.CursorUtils;

/**
 * Created by uladzimir_klyshevich on 7/18/15.
 */
class CursorDataList implements List<Binder.IData> {

    private CursorModel mCursorModel;

    private Binder.IData mData;

    public CursorDataList(Cursor cursorModel) {
        mCursorModel = new CursorModel(cursorModel);
        mData = Binder.getData(mCursorModel);
    }

    public void close() {
        CursorUtils.close(mCursorModel);
        mCursorModel = null;
    }

    @Override
    public void add(int location, Binder.IData object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(Binder.IData object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int location, Collection<? extends Binder.IData> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends Binder.IData> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(Object object) {
        return mCursorModel.contains(object);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return mCursorModel.containsAll(collection);
    }

    @Override
    public Binder.IData get(int location) {
        mData.setData(mCursorModel.get(location));
        return mData;
    }

    @Override
    public int indexOf(Object object) {
        return mCursorModel.indexOf(object);
    }

    @Override
    public boolean isEmpty() {
        return mCursorModel.isEmpty();
    }

    @NonNull
    @Override
    public Iterator<Binder.IData> iterator() {
        final Iterator iterator = mCursorModel.iterator();
        return new Iterator<Binder.IData>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Binder.IData next() {
                Cursor cursor = (Cursor) iterator.next();
                mData.setData(cursor);
                return mData;
            }

            @Override
            public void remove() {
                iterator.remove();
            }
        };
    }

    @NonNull
    @Override
    public ListIterator<Binder.IData> listIterator() {
        final ListIterator cursorListIterator = mCursorModel.listIterator();
        return createIterator(cursorListIterator);
    }

    private ListIterator<Binder.IData> createIterator(final ListIterator cursorListIterator) {
        return new ListIterator<Binder.IData>() {
            @Override
            public void add(Binder.IData object) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean hasNext() {
                return cursorListIterator.hasNext();
            }

            @Override
            public boolean hasPrevious() {
                return cursorListIterator.hasPrevious();
            }

            @Override
            public Binder.IData next() {
                Cursor next = (Cursor) cursorListIterator.next();
                mData.setData(next);
                return mData;
            }

            @Override
            public int nextIndex() {
                return cursorListIterator.nextIndex();
            }

            @Override
            public Binder.IData previous() {
                Cursor previous = (Cursor) cursorListIterator.previous();
                mData.setData(previous);
                return mData;
            }

            @Override
            public int previousIndex() {
                return cursorListIterator.previousIndex();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void set(Binder.IData object) {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public int size() {
        return mCursorModel.size();
    }

    @NonNull
    @Override
    public ListIterator<Binder.IData> listIterator(int location) {
        final ListIterator cursorListIterator = mCursorModel.listIterator(location);
        return createIterator(cursorListIterator);
    }

    @Override
    public int lastIndexOf(Object object) {
        return mCursorModel.lastIndexOf(object);
    }

    @Override
    public Binder.IData remove(int location) {
        throw new UnsupportedOperationException();
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
        return mCursorModel.retainAll(collection);
    }

    @Override
    public Binder.IData set(int location, Binder.IData object) {
        throw new UnsupportedOperationException();
    }


    @NonNull
    @Override
    public List<Binder.IData> subList(int start, int end) {
        throw new UnsupportedOperationException();
    }

    @NonNull
    @Override
    public Object[] toArray() {
        return mCursorModel.toArray();
    }

    @NonNull
    @Override
    public <T> T[] toArray(T[] array) {
        return mCursorModel.toArray(array);
    }
}
