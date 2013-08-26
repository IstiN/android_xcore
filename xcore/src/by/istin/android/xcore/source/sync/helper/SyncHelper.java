package by.istin.android.xcore.source.sync.helper;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.provider.ContactsContract;

import by.istin.android.xcore.XCoreHelper;
import by.istin.android.xcore.utils.AppUtils;


public class SyncHelper implements XCoreHelper.IAppServiceKey {
	
	public static final String ACCOUNT_TYPE = ".account";
    public static final String APP_SERVICE_KEY = "xcore:synchelper";

    private String mAccountName;

    private long mPeriod;

    private Context mContext;

    public static SyncHelper get(Context context) {
        return (SyncHelper) AppUtils.get(context, APP_SERVICE_KEY);
    }

    public SyncHelper(Context context, String accountName, long period) {
        mAccountName = accountName;
        mPeriod = period;
        mContext = context;
    }

	public void addSyncAccount(){
		AccountManager am = AccountManager
				.get(mContext);
		Account account = new Account(mAccountName, mContext.getPackageName()+ACCOUNT_TYPE);
		Bundle params = new Bundle();
		params.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, false);
		params.putBoolean(ContentResolver.SYNC_EXTRAS_DO_NOT_RETRY, false);
		params.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, false);
		ContentResolver.setSyncAutomatically(account,
				ContactsContract.AUTHORITY, true);
		ContentResolver.addPeriodicSync(account, ContactsContract.AUTHORITY,
				params, mPeriod);
		ContentResolver.setIsSyncable(account, ContactsContract.AUTHORITY, 1);
		am.addAccountExplicitly(account, null, null);
	}
	
	public void removeSyncAccount(){
		AccountManager am = AccountManager
				.get(mContext);
		Account account = new Account(mAccountName,
                mContext.getPackageName()+ACCOUNT_TYPE);
		Bundle params = new Bundle();
		params.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, false);
		params.putBoolean(ContentResolver.SYNC_EXTRAS_DO_NOT_RETRY, false);
		params.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, false);
		ContentResolver.removePeriodicSync(account, ContactsContract.AUTHORITY,
				params);
		ContentResolver.setIsSyncable(account, ContactsContract.AUTHORITY, 0);
		am.addAccountExplicitly(account, null, null);

	}
	
	public void removeAccount(){
		AccountManager am = AccountManager
				.get(mContext);
		Account account = new Account(mAccountName,
                mContext.getPackageName()+ACCOUNT_TYPE);
		am.removeAccount(account, null, null);
	}

    @Override
    public String getAppServiceKey() {
        return APP_SERVICE_KEY;
    }
}
