package com.mark.zumo.client.customer.viewmodel;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.mark.zumo.client.core.entity.MenuItem;
import com.mark.zumo.client.customer.model.MenuItemManager;
import com.mark.zumo.client.customer.model.UserManager;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * Created by mark on 18. 5. 10.
 */
public class MenuItemViewModel extends AndroidViewModel {
    private MenuItemManager menuItemManager;
    private UserManager userManager;
    private Disposable disposable;

    MenuItemViewModel(@NonNull final Application application) {
        super(application);
        menuItemManager = MenuItemManager.INSTANCE;
        userManager = UserManager.INSTANCE;
    }

    public LiveData<List<MenuItem>> getMenuItemList(Activity activity) {
        MutableLiveData<List<MenuItem>> listMutableLiveData = new MutableLiveData<>();
        disposable = userManager.getCurrentUser()
                .flatMapSingle(customerUser -> menuItemManager.acquireMenuItem(activity, customerUser))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listMutableLiveData::setValue);

        return listMutableLiveData;
    }

    @Override
    protected void onCleared() {
        disposable.dispose();
        menuItemManager.clearClient();
    }
}
