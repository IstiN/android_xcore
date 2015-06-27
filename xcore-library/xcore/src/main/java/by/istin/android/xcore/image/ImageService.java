package by.istin.android.xcore.image;

import android.content.Context;
import android.view.View;

import by.istin.android.xcore.XCoreHelper;
import by.istin.android.xcore.utils.AppUtils;

/**
 * Created by uladzimir_klyshevich on 6/25/15.
 */
public abstract class ImageService implements XCoreHelper.IAppServiceKey {

    public static final String APP_SERVICE_KEY = "xcore:imageservice";

    public static ImageService get(Context context) {
        return AppUtils.get(context, APP_SERVICE_KEY);
    }

    public abstract void load(View view, String value);

    public void load(String strategy, View view, String value) {
        load(view, value);
    }

    @Override
    public String getAppServiceKey() {
        return APP_SERVICE_KEY;
    }

}
