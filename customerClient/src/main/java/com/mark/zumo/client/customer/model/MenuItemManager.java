package com.mark.zumo.client.customer.model;

import android.app.Activity;
import android.os.Build;

import com.mark.zumo.client.core.entity.MenuItem;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.entity.user.GuestUser;
import com.mark.zumo.client.core.p2p.P2pClient;
import com.mark.zumo.client.core.repository.MenuItemRepository;
import com.mark.zumo.client.core.util.context.ContextHolder;

import java.util.List;

import io.reactivex.Single;

/**
 * Created by mark on 18. 4. 30.
 */

public enum MenuItemManager {
    INSTANCE;

    private MenuItemRepository menuItemRepository;

    private P2pClient p2pClient;

    MenuItemManager() {
        menuItemRepository = MenuItemRepository.from(ContextHolder.getContext());
    }

    public Single<List<MenuItem>> acquireMenuItem(Activity activity, GuestUser guestUser) {
        //TODO: remove Test data
//        return p2pClient(activity, guestUser)
//                .flatMap(P2pClient::acquireMenuItems);

        return currentStore()
                .flatMap(menuItemRepository::getMenuItemsOfStore);
    }

    private Single<P2pClient> p2pClient(Activity activity, GuestUser guestUser) {
        return Single.fromCallable(() -> new P2pClient(activity, guestUser));
    }

    private Single<Store> currentStore() {
        //TODO: impl
        return Single.fromCallable(() -> new Store(5, Build.MODEL, 0, 0, 31, 31));
    }

    public void clearClient() {
        p2pClient.stopDiscovery();
        p2pClient = null;
    }
}
