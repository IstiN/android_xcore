/*******************************************************************************
 * Copyright 2013 Uladzimir Klyshevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package by.istin.android.xcore.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import by.istin.android.xcore.service.DataSourceService;
import by.istin.android.xcore.service.StatusResultReceiver;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.utils.StringUtil;


/**
 * Created by Uladzimir_Klyshevich on 7/5/13.
 */
public class DataSourceExecuteHelper {

    public static final Handler HANDLER = new Handler(Looper.getMainLooper());

    public static interface IDataSourceListener {

        String getUrl();

        boolean isCacheable();

        long getCacheExpiration();

        String getProcessorKey();

        String getDataSourceKey();

        void hideProgress();

        void showProgress();

        void setServiceWork(boolean isWork);

        void onError(Exception e, DataSourceRequest dataSourceRequest);

        void onReceiverOnCached(Bundle resultData);

        void onReceiverOnDone(Bundle resultData);
    }

    public static void load(final Activity activity, boolean isForceUpdate, final IDataSourceListener dataSourceFragmentHelper) {
        load(activity, null, isForceUpdate, dataSourceFragmentHelper);
    }

    public static void load(final Activity activity, String url, boolean isForceUpdate, final IDataSourceListener dataSourceListener) {
        if (StringUtil.isEmpty(url)) {
            url = dataSourceListener.getUrl();
        }
        if (StringUtil.isEmpty(url)) {
            return;
        }
        final DataSourceRequest dataSourceRequest = new DataSourceRequest(url);
        dataSourceRequest.setCacheable(dataSourceListener.isCacheable());
        dataSourceRequest.setCacheExpiration(dataSourceListener.getCacheExpiration());
        dataSourceRequest.setForceUpdateData(isForceUpdate);
        DataSourceService.execute(activity, dataSourceRequest, dataSourceListener.getProcessorKey(), dataSourceListener.getDataSourceKey(), new StatusResultReceiver(HANDLER) {

            @Override
            protected void onAddToQueue(Bundle resultData) {
                super.onAddToQueue(resultData);
                dataSourceListener.setServiceWork(true);
            }

            @Override
            public void onStart(Bundle resultData) {
                dataSourceListener.setServiceWork(true);
            }

            @Override
            public void onError(Exception exception) {
                dataSourceListener.setServiceWork(false);
                exception.printStackTrace();
                if (activity == null) {
                    return;
                }
                dataSourceListener.onError(exception, dataSourceRequest);
            }

            @Override
            public void onDone(Bundle resultData) {
                dataSourceListener.setServiceWork(false);
                if (activity == null) {
                    return;
                }
                dataSourceListener.onReceiverOnDone(resultData);
            }

            @Override
            protected void onCached(Bundle resultData) {
                dataSourceListener.setServiceWork(false);
                super.onCached(resultData);
                dataSourceListener.onReceiverOnCached(resultData);
            }

        });
    }

}
