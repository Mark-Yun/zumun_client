package com.mark.zumo.client.customer.viewmodel;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

/**
 * Created by mark on 18. 5. 10.
 */
public class MainViewModel extends AndroidViewModel {
    private MainViewModel(@NonNull final Application application) {
        super(application);
    }

    public void startDiscovery(Activity activity) {

    }
}
