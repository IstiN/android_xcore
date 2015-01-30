package by.istin.android.xcore.source.sync.helper;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;

import by.istin.android.xcore.utils.AppUtils;
import by.istin.android.xcore.utils.Log;


public class SyncHelper implements ISyncHelper {

    public static final String ACCOUNT_TYPE = ".account";

    private final String mAccountName;

    private final long mPeriod;

    private final Context mContext;

    private final String mModelContentProviderAuthority;

    private final String mType;

    public static ISyncHelper get(Context context) {
        return (ISyncHelper) AppUtils.get(context, APP_SERVICE_KEY);
    }

    public SyncHelper(Context context, String accountName, long period, String modelContentProviderAuthority) {
        mAccountName = accountName;
        mPeriod = period;
        mContext = context;
        mModelContentProviderAuthority = modelContentProviderAuthority;
        mType = mContext.getApplicationContext().getPackageName() + ACCOUNT_TYPE;
    }

    @Override
    public void addSyncAccount() {
        Context applicationContext = mContext.getApplicationContext();
        AccountManager am = AccountManager.get(applicationContext);
        if (isExists(am)) {
            Log.xd(this, "account already added");
            return;
        }
        Log.xd(this, "add sync account");
        Account account = new Account(mAccountName, mType);
        Bundle params = new Bundle();
        params.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, false);
        params.putBoolean(ContentResolver.SYNC_EXTRAS_DO_NOT_RETRY, false);
        params.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, false);
        ContentResolver.setSyncAutomatically(account,
                mModelContentProviderAuthority, true);
        ContentResolver.addPeriodicSync(account, mModelContentProviderAuthority,
                params, mPeriod);
        ContentResolver.setIsSyncable(account, mModelContentProviderAuthority, 1);
        try {
            am.addAccountExplicitly(account, null, null);
        } catch (SecurityException ex) {
            Log.e("SyncHelper", ex);
            //TODO handle
        }
    }

    protected boolean isExists(AccountManager am) {
        Account[] accountsByType = am.getAccountsByType(mType);
        return accountsByType != null && accountsByType.length > 0;
    }

    @Override
    public void removeSyncAccount() {
        Context applicationContext = mContext.getApplicationContext();
        AccountManager am = AccountManager
                .get(applicationContext);
        if (!isExists(am)) {
            Log.xd(this, "no sync accounts for remove");
            return;
        }
        Log.xd(this, "remove sync account");
        Account account = new Account(mAccountName, mType);
        Bundle params = new Bundle();
        params.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, false);
        params.putBoolean(ContentResolver.SYNC_EXTRAS_DO_NOT_RETRY, false);
        params.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, false);
        ContentResolver.removePeriodicSync(account, mModelContentProviderAuthority,
                params);
        ContentResolver.setIsSyncable(account, mModelContentProviderAuthority, 0);
        try {
            am.addAccountExplicitly(account, null, null);
        } catch (SecurityException ex) {
            Log.e("SyncHelper", ex);
            //TODO handle
        }
    }

    @Override
    public void removeAccount() {
        AccountManager am = AccountManager.get(mContext);
        if (!isExists(am)) {
            Log.xd(this, "no sync accounts for remove");
            return;
        }
        Log.xd(this, "remove account");
        Account account = new Account(mAccountName, mType);
        am.removeAccount(account, null, null);
    }

    @Override
    public String getAppServiceKey() {
        return APP_SERVICE_KEY;
    }
}
