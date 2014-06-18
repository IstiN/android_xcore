package by.istin.android.xcore.callable;

/**
 * Created by IstiN on 14.7.13.
 */
public interface IError<T extends Throwable> {

    void onError(T error);

}
