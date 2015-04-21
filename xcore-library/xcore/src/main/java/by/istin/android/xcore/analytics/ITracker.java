package by.istin.android.xcore.analytics;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import java.util.HashMap;

import by.istin.android.xcore.XCoreHelper;
import by.istin.android.xcore.utils.AppUtils;

public interface ITracker extends XCoreHelper.IAppServiceKey {

    public final String APP_SERVICE_KEY = "xcore:tracker";

    void track(HashMap params);

    void track(String action);

    void track(String action, HashMap<String, String> params);

    void onCreate(Activity activity);

    void onResume(Activity activity);

    void onPause(Activity activity);

    void onStop(Activity activity);

    void onStart(Activity activity);

    void onCreate(Application application);

    void addTracker(ITracker tracker);

    public static class Impl {

        public static ITracker newInstance() {
            return new Tracker();
        };

        public static ITracker get(Context context) {
            return AppUtils.get(context, APP_SERVICE_KEY);
        }

    }

}
