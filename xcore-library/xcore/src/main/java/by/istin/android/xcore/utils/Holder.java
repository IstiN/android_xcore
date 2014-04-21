package by.istin.android.xcore.utils;

/**
 * Created by Uladzimir_Klyshevich on 4/21/2014.
 */
public class Holder<T> {

    private T t;

    public Holder() {

    }

    public Holder(T t) {
        this.t = t;
    }

    public T get() {
        return t;
    }

    public void set(T t) {
        this.t = t;
    }

    public boolean isNull() {
        return t == null;
    }
}
