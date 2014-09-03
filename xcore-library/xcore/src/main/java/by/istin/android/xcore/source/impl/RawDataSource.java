/**
 * 
 */
package by.istin.android.xcore.source.impl;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.IDataSource;
import by.istin.android.xcore.utils.Holder;


/**
 * Class for load data from file.
 * 
 * @author Uladzimir_Klyshevich
 * 
 */
public class RawDataSource implements IDataSource<InputStream> {

    public static final String SYSTEM_SERVICE_KEY = "xcore:rawdatasource";

    private final Context mContext;

    public RawDataSource(Context context) {
        this.mContext = context;
    }

    @Override
    public InputStream getSource(DataSourceRequest dataSourceRequest, Holder<Boolean> isCached) throws IOException {
        return mContext.getResources().openRawResource(Integer.valueOf(dataSourceRequest.getUri()));
    }

    @Override
    public String getAppServiceKey() {
        return SYSTEM_SERVICE_KEY;
    }
	
}
