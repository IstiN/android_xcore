/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package by.istin.android.xcore.testapp;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.view.View;
import android.view.WindowManager;

import by.istin.android.xcore.Core;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.wearable.CoreWearable;

/**
 * Shows events and photo from the Wearable APIs.
 */
public class MainActivity extends Activity {


    private Handler mHandler;

    private CoreWearable mCoreWearable;

    private Core.IExecuteOperation mExecuteOperation;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        mHandler = new Handler();
        setContentView(R.layout.main_activity);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mCoreWearable = CoreWearable.get(this);
        updateExecuteOpration();
        findViewById(R.id.intro).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCoreWearable.execute(mExecuteOperation);
            }
        });
    }

    private void updateExecuteOpration() {
        Core.ExecuteOperationBuilder executeOperationBuilder = new Core.ExecuteOperationBuilder();
        DataSourceRequest pDataSourceRequest = new DataSourceRequest("http://android.com");
        pDataSourceRequest.setCacheExpiration(DateUtils.DAY_IN_MILLIS);
        pDataSourceRequest.setCacheable(true);
        executeOperationBuilder
                .setDataSourceRequest(pDataSourceRequest)
                .setActivity(this)
                .setDataSourceKey("sampledatasourcekey")
                .setProcessorKey("sampleprocessorkey")
                .setResultQueryUri(Uri.parse("content://sampleresulturi"))
                .setSelectionArgs(new String[]{"arg1", "arg2"});
        mExecuteOperation = executeOperationBuilder.build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCoreWearable.execute(mExecuteOperation);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCoreWearable.cancel(mExecuteOperation);
    }

}
