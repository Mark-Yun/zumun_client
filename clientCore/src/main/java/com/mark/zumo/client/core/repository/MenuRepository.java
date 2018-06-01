package com.mark.zumo.client.core.repository;

import android.util.Log;

import com.mark.zumo.client.core.appserver.AppServerServiceProvider;
import com.mark.zumo.client.core.appserver.NetworkRepository;
import com.mark.zumo.client.core.dao.AppDatabaseProvider;
import com.mark.zumo.client.core.dao.DiskRepository;
import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.core.entity.MenuOption;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.entity.util.ListComparator;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.observables.GroupedObservable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 4. 30.
 */

public enum MenuRepository {

    INSTANCE;

    public static final String TAG = "MenuRepository";

    private DiskRepository diskRepository;
    private NetworkRepository networkRepository;

    MenuRepository() {
        diskRepository = AppDatabaseProvider.INSTANCE.diskRepository;
        networkRepository = AppServerServiceProvider.INSTANCE.networkRepository;
    }

    private void onErrorOccurred(Throwable throwable) {
        Log.e(TAG, "onErrorOccurred: ", throwable);
    }

    public Observable<List<Menu>> getMenuItemsOfStore(Store store) {
        String storeUuid = store.uuid;

        Observable<List<Menu>> menuListDB = diskRepository.getMenuList(storeUuid).toObservable();
        Observable<List<Menu>> menuListApi = networkRepository.getMenuList(storeUuid)
                .doOnNext(diskRepository::insertMenuList);

        return Observable.merge(menuListDB, menuListApi)
                .doOnError(this::onErrorOccurred)
                .distinctUntilChanged(new ListComparator<>());
    }

    private Observable<List<MenuOption>> getMenuOptionsOfMenu(String menuUuid) {
        Observable<List<MenuOption>> menuOptionListDB = diskRepository.getMenuOptionList(menuUuid).toObservable();
        Observable<List<MenuOption>> menuOptionListApi = networkRepository.getMenuOptionList(menuUuid)
                .doOnNext(diskRepository::insertMenuOptionList);

        return Observable.merge(menuOptionListDB, menuOptionListApi)
                .doOnError(this::onErrorOccurred)
                .subscribeOn(Schedulers.io())
                .distinctUntilChanged(new ListComparator<>());
    }

    public Observable<GroupedObservable<String, MenuOption>> getMenuOptionGroupByMenu(String menuUuid) {
        return getMenuOptionsOfMenu(menuUuid)
                .flatMap(Observable::fromIterable)
                .groupBy(menuOption -> menuOption.name);
    }

    public Observable<Menu> getMenuFromDisk(final String uuid) {
        return diskRepository.getMenu(uuid).toObservable();
    }

    public Observable<MenuOption> getMenuOptionFromDisk(final String menuOptionUuid) {
        return diskRepository.getMenuOption(menuOptionUuid).toObservable();
    }
}
