package by.istin.android.xcore.callable;

/**
 * Interface that can be used to delegate Throwable to top of callers.
 *
 * @param <T> is type of throwable
 */
public interface IError<T extends Throwable> {

    /**
     * Need to be call when throws Throwable.
     *
     * @param error Throwable
     */
    void onError(T error);

}
