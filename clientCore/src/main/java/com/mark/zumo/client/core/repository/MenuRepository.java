package com.mark.zumo.client.core.repository;

import android.content.Context;
import android.util.Log;

import com.mark.zumo.client.core.appserver.AppServerServiceProvider;
import com.mark.zumo.client.core.appserver.NetworkRepository;
import com.mark.zumo.client.core.dao.AppDatabaseProvider;
import com.mark.zumo.client.core.dao.DiskRepository;
import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.core.entity.MenuOption;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.entity.util.EntityComparator;
import com.mark.zumo.client.core.entity.util.ListComparator;
import com.mark.zumo.client.core.util.DebugUtil;
import com.mark.zumo.client.core.util.context.ContextHolder;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.observables.GroupedObservable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 4. 30.
 */

public enum MenuRepository {
    INSTANCE;

    public static final String TAG = "MenuRepository";

    private DiskRepository diskRepository;
    private Context context;
    private NetworkRepository networkRepository;

    MenuRepository() {
        context = ContextHolder.getContext();

        diskRepository = AppDatabaseProvider.INSTANCE.appDatabase.diskRepository();
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
                .subscribeOn(Schedulers.io())
                .distinctUntilChanged(new ListComparator<>());
    }

    private Observable<List<MenuOption>> getMenuOptionsOfMenu(String menuUuid) {
//        Observable<List<MenuOption>> menuOptionListDB = diskRepository.getMenuOptionList(menuUuid).toObservable();
//        Observable<List<MenuOption>> menuOptionListApi = networkRepository.getMenuOptionList(menuUuid)
//                .doOnNext(diskRepository::insertMenuOptionList);
//
//        return Observable.merge(menuOptionListDB, menuOptionListApi)
//                .doOnError(this::onErrorOccurred)
//                .subscribeOn(Schedulers.io())
//                .distinctUntilChanged(new ListComparator<>());
        //TODO: remove test data
        return Observable.just(DebugUtil.menuOptionList(menuUuid));
    }

    private Observable<MenuOption> getMenuOptionFromList(List<MenuOption> menuOptionList) {
        return Observable.create(e -> {
            for (int i = 0; i < menuOptionList.size(); i++)
                e.onNext(menuOptionList.get(i));
            e.onComplete();
        });
    }

    public Observable<GroupedObservable<String, MenuOption>> getMenuOptionGroupByMenu(String menuUuid) {
        return getMenuOptionsOfMenu(menuUuid)
                .flatMap(this::getMenuOptionFromList)
                .groupBy(menuOption -> menuOption.name);
    }

    public Observable<Menu> getMenu(final String uuid) {
        return Observable.create((ObservableOnSubscribe<Menu>) e -> {
            diskRepository.getMenu(uuid)
                    .doOnSuccess(e::onNext)
                    .subscribe();
        }).distinctUntilChanged(new EntityComparator<>());
    }
}
