package com.mark.zumo.client.customer.model;

import android.app.Activity;

import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.core.entity.MenuOption;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.entity.user.GuestUser;
import com.mark.zumo.client.core.p2p.P2pClient;
import com.mark.zumo.client.core.repository.MenuRepository;
import com.mark.zumo.client.core.util.DebugUtil;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.observables.GroupedObservable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 4. 30.
 */

public enum MenuManager {
    INSTANCE;

    private MenuRepository menuRepository;

    private P2pClient p2pClient;

    MenuManager() {
        menuRepository = MenuRepository.INSTANCE;
    }

    public Observable<List<Menu>> acquireMenuItem(Activity activity, GuestUser guestUser) {
        //TODO: remove Test data
//        return p2pClient(activity, guestUser)
//                .flatMap(P2pClient::acquireMenuItems);

        return currentStore()
                .flatMapObservable(menuRepository::getMenuItemsOfStore)
                .subscribeOn(Schedulers.io());
    }

    public Observable<Menu> getMenuFromDisk(String uuid) {
        return menuRepository.getMenuFromDisk(uuid)
                .subscribeOn(Schedulers.io());
    }

    private Single<P2pClient> p2pClient(Activity activity, GuestUser guestUser) {
        return Single.fromCallable(() -> new P2pClient(activity, guestUser));
    }

    private Single<Store> currentStore() {
        //TODO: impl
        return Single.fromCallable(DebugUtil::store);
    }

    public void clearClient() {
        if (p2pClient == null) {
            return;
        }

        p2pClient.stopDiscovery();
        p2pClient = null;
    }

    public Observable<GroupedObservable<String, MenuOption>> getMenuOptionList(String menuUuid) {
        return menuRepository.getMenuOptionGroupByMenu(menuUuid)
                .subscribeOn(Schedulers.computation());
    }

    public Observable<MenuOption> getMenuOptionFromDisk(String menuOptionUuid) {
        return menuRepository.getMenuOptionFromDisk(menuOptionUuid)
                .subscribeOn(Schedulers.io());
    }
}
