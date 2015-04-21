package by.istin.android.xcore.analytics;

/**
 * Created by uladzimir_klyshevich on 4/21/15.
 */
public abstract class AbstractTracker implements ITracker {

    @Override
    public final void addTracker(ITracker tracker) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final String getAppServiceKey() {
        throw new UnsupportedOperationException();
    }
}
