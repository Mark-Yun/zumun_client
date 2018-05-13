package com.mark.zumo.client.customer.model;

import android.app.Activity;
import android.content.Context;

import com.mark.zumo.client.core.entity.MenuItem;
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

    private Context context;
    private MenuItemRepository menuItemRepository;

    private P2pClient p2pClient;

    MenuItemManager() {
        context = ContextHolder.getContext();
        menuItemRepository = MenuItemRepository.from(context);
    }

    public Single<List<MenuItem>> acquireMenuItem(Activity activity, GuestUser guestUser) {
        p2pClient = new P2pClient(activity, guestUser);
        //TODO: remove Test data
        return p2pClient.acquireMenuItems();
    }

    public void clearClient() {
        p2pClient.stopDiscovery();
        p2pClient = null;
    }
}
