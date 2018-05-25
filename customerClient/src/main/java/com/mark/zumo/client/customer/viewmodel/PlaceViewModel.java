package com.mark.zumo.client.customer.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.customer.model.StoreManager;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 5. 19.
 */
public class PlaceViewModel extends AndroidViewModel {

    private StoreManager storeManager;

    public PlaceViewModel(@NonNull final Application application) {
        super(application);

        storeManager = StoreManager.INSTANCE;
    }

    public LiveData<List<Store>> nearByStore() {
        MutableLiveData<List<Store>> nearByStore = new MutableLiveData<>();

        storeManager.nearByStore()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(nearByStore::setValue);

        return nearByStore;
    }

    public LiveData<List<Store>> latestVisitStore() {
        MutableLiveData<List<Store>> latestVisitStore = new MutableLiveData<>();

        storeManager.latestVisitStore()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(latestVisitStore::setValue);

        return latestVisitStore;
    }
}
