package by.istin.android.xcore.sample;

import android.content.Context;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.squareup.picasso.RequestCreator;

import by.istin.android.xcore.XCoreHelper;
import by.istin.android.xcore.error.ErrorHandler;
import by.istin.android.xcore.provider.IDBContentProviderSupport;
import by.istin.android.xcore.sample.core.processor.ContentEntityProcessor;
import by.istin.android.xcore.sample.core.processor.SampleEntityProcessor;
import by.istin.android.xcore.sample.core.provider.ContentProvider;
import by.istin.android.xcore.source.impl.http.HttpAndroidDataSource;

/**
 * Created by Uladzimir_Klyshevich on 1/28/2015.
 */
public class SimpleAppModule implements XCoreHelper.Module {

    public static DisplayImageOptions BITMAP_DISPLAYER_OPTIONS = new DisplayImageOptions.Builder()
            .resetViewBeforeLoading(true)
            .delayBeforeLoading(300)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .displayer(new SimpleBitmapDisplayer())
            .build();

    @Override
    public void onCreate(Context context, XCoreHelper coreHelper) {
        IDBContentProviderSupport dbContentProviderSupport = ContentProvider.getDBContentProviderSupport(context);
        coreHelper.registerAppService(new SampleEntityProcessor(dbContentProviderSupport));
        coreHelper.registerAppService(new HttpAndroidDataSource(
                        new HttpAndroidDataSource.DefaultHttpRequestBuilder(),
                        new HttpAndroidDataSource.DefaultResponseStatusHandler())
        );
        coreHelper.registerAppService(new ContentEntityProcessor(dbContentProviderSupport));
        coreHelper.registerAppService(new ErrorHandler(
                "Error",
                "Check your internet connection",
                "Server error",
                "Developer error",
                "istin2007@gmail.com"
        ));
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .defaultDisplayImageOptions(BITMAP_DISPLAYER_OPTIONS).build();
        //addPlugin(new ImageLoaderPlugin(config));
        coreHelper.addPlugin(new by.istin.android.xcore.plugin.picasso.ImageLoaderPlugin(context) {

            @Override
            public void onRequestCreated(RequestCreator requestCreator) {
                //customize if needs
            }

        });
    }
}
