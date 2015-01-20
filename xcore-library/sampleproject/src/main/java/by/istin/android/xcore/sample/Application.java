package by.istin.android.xcore.sample;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import by.istin.android.xcore.CoreApplication;
import by.istin.android.xcore.error.ErrorHandler;
import by.istin.android.xcore.plugin.IFragmentPlugin;
import by.istin.android.xcore.plugin.uil.ImageLoaderPlugin;
import by.istin.android.xcore.provider.IDBContentProviderSupport;
import by.istin.android.xcore.sample.core.processor.ContentEntityProcessor;
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
        IDBContentProviderSupport dbContentProviderSupport = ContentProvider.getDBContentProviderSupport(this);
        registerAppService(new SampleEntityProcessor(dbContentProviderSupport));
        registerAppService(new HttpAndroidDataSource(
                new HttpAndroidDataSource.DefaultHttpRequestBuilder(),
                new HttpAndroidDataSource.DefaultResponseStatusHandler())
        );
        registerAppService(new ContentEntityProcessor(dbContentProviderSupport));
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
