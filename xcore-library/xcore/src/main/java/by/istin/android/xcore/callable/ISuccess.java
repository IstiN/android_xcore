package by.istin.android.xcore.callable;

/**
 * Interface that can be used to delegate Result of operation to top of callers.
 *
 * @param <Result> result of background operation
 */
public interface ISuccess<Result> {

    /**
     * Call this method with result that you want to delegate to top caller.
     *
     * @param result success result of execution operation
     */
    void success(Result result);
}
