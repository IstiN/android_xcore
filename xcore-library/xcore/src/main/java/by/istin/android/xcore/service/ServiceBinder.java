package by.istin.android.xcore.service;

import java.lang.ref.WeakReference;

import android.os.Binder;

public class ServiceBinder<S> extends Binder {

	private final WeakReference<S> mService;
    
    public ServiceBinder(S service){
        mService = new WeakReference<S>(service);
    }

    public S getService() {
        return mService.get();
    }
	
}
