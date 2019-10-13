package by.istin.android.xcore.image;

import android.content.Context;
import android.util.Pair;
import android.view.View;

import by.istin.android.xcore.XCoreHelper;
import by.istin.android.xcore.callable.ISuccess;
import by.istin.android.xcore.utils.AppUtils;

/**
 * Created by uladzimir_klyshevich on 6/25/15.
 */
public abstract class ImageService implements XCoreHelper.IAppServiceKey {

    public static class ImageSize {

        private int height;

        private int width;

        public ImageSize(int height, int width) {
            this.height = height;
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public int getWidth() {
            return width;
        }

        @Override
        public String toString() {
            return "ImageSize{" +
                    "height=" + height +
                    ", width=" + width +
                    '}';
        }
    }

    public static final String APP_SERVICE_KEY = "xcore:imageservice";

    public static ImageService get(Context context) {
        return AppUtils.get(context, APP_SERVICE_KEY);
    }

    public abstract void load(View view, String value);

    public abstract void loadImageSize(String uri, ISuccess<ImageSize> success);

    public void load(String strategy, View view, String value) {
        load(view, value);
    }

    @Override
    public String getAppServiceKey() {
        return APP_SERVICE_KEY;
    }

}
