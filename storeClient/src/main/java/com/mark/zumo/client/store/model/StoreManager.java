/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.model;

import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.repository.StoreRepository;

import io.reactivex.Maybe;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 4. 30.
 */

public enum StoreManager {

    INSTANCE;

    private final StoreRepository storeRepository;

    StoreManager() {
        storeRepository = StoreRepository.INSTANCE;
    }

    public Maybe<Store> updateStoreName(Store store, String newName) {
        Store newStore = new Store(store.uuid, newName, store.latitude, store.longitude, store.coverImageUrl, store.thumbnailUrl, store.fcmToken);
        return storeRepository.updateStore(newStore)
                .subscribeOn(Schedulers.io());
    }
}
