package com.mark.zumo.client.core.repository;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.mark.zumo.client.core.appserver.AppServerServiceProvider;
import com.mark.zumo.client.core.appserver.NetworkRepository;
import com.mark.zumo.client.core.dao.AppDatabaseProvider;
import com.mark.zumo.client.core.dao.DiskRepository;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.entity.util.EntityComparator;
import com.mark.zumo.client.core.util.DebugUtil;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 4. 30.
 */

public class StoreRepository {

    public static final String TAG = "StoreRepository";
    private volatile static StoreRepository instance;

    private NetworkRepository networkRepository;
    private DiskRepository diskRepository;

    private StoreRepository(Context context) {
        networkRepository = AppServerServiceProvider.INSTANCE.networkRepository;
        diskRepository = AppDatabaseProvider.INSTANCE.appDatabase.diskRepository();
    }

    public static StoreRepository from(Context context) {
        if (instance == null) {
            synchronized (StoreRepository.class) {
                if (instance == null) instance = new StoreRepository(context);
            }
        }
        return instance;
    }

    public Observable<List<Store>> nearByStore(Location location) {
        return Observable.create(e -> {
            e.onNext(DebugUtil.storeList());
        });
    }

    public Observable<List<Store>> latestVisitStore() {
        return Observable.create(e -> {
            e.onNext(DebugUtil.storeList());
        });
    }

    public Observable<Store> getStore(String storeUuid) {
        Observable<Store> storeDB = diskRepository.getStore(storeUuid).toObservable();
        Observable<Store> storeApi = networkRepository.getStore(storeUuid)
                .doOnNext(diskRepository::insert);

        return Observable.merge(storeDB, storeApi)
                .doOnError(this::onErrorOccurred)
                .subscribeOn(Schedulers.io())
                .distinctUntilChanged(new EntityComparator<>());
    }

    private void onErrorOccurred(Throwable throwable) {
        Log.e(TAG, "onErrorOccurred: ", throwable);
    }
}
