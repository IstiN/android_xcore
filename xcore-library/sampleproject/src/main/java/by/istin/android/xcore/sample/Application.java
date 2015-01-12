package by.istin.android.xcore.sample;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import by.istin.android.xcore.CoreApplication;
import by.istin.android.xcore.error.ErrorHandler;
import by.istin.android.xcore.plugin.uil.ImageLoaderPlugin;
import by.istin.android.xcore.sample.core.processor.SampleEntityProcessor;
import by.istin.android.xcore.sample.core.provider.ContentProvider;
import by.istin.android.xcore.source.impl.http.HttpAndroidDataSource;

/**
 * Created by IstiN on 13.11.13.
 */
public class Application extends CoreApplication {

    public static DisplayImageOptions BITMAP_DISPLAYER_OPTIONS = new DisplayImageOptions.Builder()
            .resetViewBeforeLoading(true)
            .delayBeforeLoading(300)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .displayer(new SimpleBitmapDisplayer())
            .build();

    @Override
    public void onCreate() {
        super.onCreate();
        registerAppService(new SampleEntityProcessor(ContentProvider.getDBContentProviderSupport(this)));
        registerAppService(new HttpAndroidDataSource(
                new HttpAndroidDataSource.DefaultHttpRequestBuilder(),
                new HttpAndroidDataSource.DefaultResponseStatusHandler())
        );
        registerAppService(new ErrorHandler(
                "Error",
                "Check your internet connection",
                "Server error",
                "Developer error",
                "istin2007@gmail.com"
        ));
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .defaultDisplayImageOptions(BITMAP_DISPLAYER_OPTIONS).build();
        addPlugin(new ImageLoaderPlugin(config));
    }

}
