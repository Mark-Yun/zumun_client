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

    private StoreRepository storeRepository;

    StoreManager() {
        storeRepository = StoreRepository.INSTANCE;
    }

    public Maybe<Store> registerToken(Store store, String token) {
        Store newStore = new Store(store.uuid, store.name, store.latitude, store.longitude, store.coverImageUrl, store.thumbnailUrl, token);
        return storeRepository.updateStore(newStore)
                .subscribeOn(Schedulers.io());
    }

}
