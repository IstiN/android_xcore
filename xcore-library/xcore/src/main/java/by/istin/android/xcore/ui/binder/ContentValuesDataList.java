package by.istin.android.xcore.ui.binder;

import android.content.ContentValues;
import androidx.annotation.NonNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by uladzimir_klyshevich on 7/18/15.
 */
class ContentValuesDataList implements List<Binder.IData> {

    private List<ContentValues> mContentValueList;

    private Binder.IData mData;

    public ContentValuesDataList(List<ContentValues> contentValueList) {
        mContentValueList = contentValueList;
        mData = Binder.getData(mContentValueList.isEmpty() ? null : mContentValueList.get(0));
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
        return mContentValueList.contains(object);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return mContentValueList.containsAll(collection);
    }

    @Override
    public Binder.IData get(int location) {
        mData.setData(mContentValueList.get(location));
        return mData;
    }

    @Override
    public int indexOf(Object object) {
        return mContentValueList.indexOf(object);
    }

    @Override
    public boolean isEmpty() {
        return mContentValueList.isEmpty();
    }

    @NonNull
    @Override
    public Iterator<Binder.IData> iterator() {
        final Iterator iterator = mContentValueList.iterator();
        return new Iterator<Binder.IData>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Binder.IData next() {
                ContentValues contentValues = (ContentValues) iterator.next();
                mData.setData(contentValues);
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
        final ListIterator contentValuesListIterator = mContentValueList.listIterator();
        return createIterator(contentValuesListIterator);
    }

    private ListIterator<Binder.IData> createIterator(final ListIterator contentValuesListIterator) {
        return new ListIterator<Binder.IData>() {
            @Override
            public void add(Binder.IData object) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean hasNext() {
                return contentValuesListIterator.hasNext();
            }

            @Override
            public boolean hasPrevious() {
                return contentValuesListIterator.hasPrevious();
            }

            @Override
            public Binder.IData next() {
                ContentValues next = (ContentValues) contentValuesListIterator.next();
                mData.setData(next);
                return mData;
            }

            @Override
            public int nextIndex() {
                return contentValuesListIterator.nextIndex();
            }

            @Override
            public Binder.IData previous() {
                ContentValues previous = (ContentValues) contentValuesListIterator.previous();
                mData.setData(previous);
                return mData;
            }

            @Override
            public int previousIndex() {
                return contentValuesListIterator.previousIndex();
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
        return mContentValueList.size();
    }

    @NonNull
    @Override
    public ListIterator<Binder.IData> listIterator(int location) {
        final ListIterator contentValuesListIterator = mContentValueList.listIterator(location);
        return createIterator(contentValuesListIterator);
    }

    @Override
    public int lastIndexOf(Object object) {
        return mContentValueList.lastIndexOf(object);
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
        return mContentValueList.retainAll(collection);
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
        return mContentValueList.toArray();
    }

    @NonNull
    @Override
    public <T> T[] toArray(T[] array) {
        return mContentValueList.toArray(array);
    }
}
