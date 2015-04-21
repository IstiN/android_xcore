package by.istin.android.xcore.analytics;

import android.app.Activity;
import android.app.Application;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by uladzimir_klyshevich on 4/21/15.
 */
public class Tracker implements ITracker {

    private Set<ITracker> mTrackers = new HashSet<>();

    @Override
    public void track(HashMap params) {
        for (ITracker tracker : mTrackers) {
            tracker.track(params);
        }
    }

    @Override
    public void track(String action) {
        for (ITracker tracker : mTrackers) {
            tracker.track(action);
        }
    }

    @Override
    public void track(String action, HashMap<String, String> params) {
        for (ITracker tracker : mTrackers) {
            tracker.track(action, params);
        }
    }

    @Override
    public void onCreate(Activity activity) {
        for (ITracker tracker : mTrackers) {
            tracker.onCreate(activity);
        }
    }

    @Override
    public void onResume(Activity activity) {
        for (ITracker tracker : mTrackers) {
            tracker.onResume(activity);
        }
    }

    @Override
    public void onPause(Activity activity) {
        for (ITracker tracker : mTrackers) {
            tracker.onPause(activity);
        }
    }

    @Override
    public void onStop(Activity activity) {
        for (ITracker tracker : mTrackers) {
            tracker.onStop(activity);
        }
    }

    @Override
    public void onStart(Activity activity) {
        for (ITracker tracker : mTrackers) {
            tracker.onStart(activity);
        }
    }

    @Override
    public void onCreate(Application application) {
        for (ITracker tracker : mTrackers) {
            tracker.onCreate(application);
        }
    }

    @Override
    public void addTracker(ITracker tracker) {
        mTrackers.add(tracker);
    }

    @Override
    public String getAppServiceKey() {
        return ITracker.APP_SERVICE_KEY;
    }
}
