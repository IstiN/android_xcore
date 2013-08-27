/*
 * Copyright (C) 2010 The Android Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package by.istin.android.xcore.source.sync.service;

import android.accounts.Account;
import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;

import by.istin.android.xcore.provider.ModelContract;
import by.istin.android.xcore.service.SyncDataSourceService;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.SyncDataSourceRequestEntity;
import by.istin.android.xcore.source.sync.helper.SyncHelper;
import by.istin.android.xcore.utils.CursorUtils;

/**
 * Service to handle Account sync. This is invoked with an intent with action
 * ACTION_AUTHENTICATOR_INTENT. It instantiates the syncadapter and returns its
 * IBinder.
 */
public abstract class SyncService extends Service {

    private static final Object sSyncAdapterLock = new Object();

    private static AbstractThreadedSyncAdapter sSyncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = getSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
    
    public AbstractThreadedSyncAdapter getSyncAdapter(Context context, boolean autoInitialize) {
        return new AbstractThreadedSyncAdapter(context, autoInitialize){

            @Override
            public void onPerformSync(Account account, Bundle extras, String authority,
                                      ContentProviderClient provider, SyncResult syncResult) {
                SyncService.this.onPerformSync(account, extras, authority, provider, syncResult);
            }
        };
    }

    protected void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Cursor cursor = getContentResolver().query(ModelContract.getUri(SyncDataSourceRequestEntity.class), null, null, null, SyncDataSourceRequestEntity.LAST_CHANGED + " ASC");
        if (CursorUtils.isEmpty(cursor)) {
            CursorUtils.close(cursor);
            SyncHelper syncHelper = SyncHelper.get(this);
            syncHelper.removeSyncAccount();
            syncHelper.removeAccount();
            return;
        }
        cursor.moveToFirst();
        do {
            String processorKey = CursorUtils.getString(SyncDataSourceRequestEntity.PROCESSOR_KEY, cursor);
            String dataSourceService = CursorUtils.getString(SyncDataSourceRequestEntity.DATASOURCE_KEY, cursor);
            DataSourceRequest dataSourceRequest = new DataSourceRequest(CursorUtils.getString(SyncDataSourceRequestEntity.URI, cursor));
            dataSourceRequest.setCacheExpiration(CursorUtils.getLong(SyncDataSourceRequestEntity.EXPIRATION, cursor));
            dataSourceRequest.setCacheable(CursorUtils.getInt(SyncDataSourceRequestEntity.CACHEABLE, cursor) == 1);
            dataSourceRequest.setParentUri(CursorUtils.getString(SyncDataSourceRequestEntity.PARENT_URI, cursor));
            SyncDataSourceService.execute(this, dataSourceRequest, processorKey, dataSourceService);
        } while (cursor.moveToNext());
    }


}
