/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.supervisor.model;

import android.os.Bundle;

import com.mark.zumo.client.core.appserver.request.registration.StoreRegistrationRequest;
import com.mark.zumo.client.core.appserver.request.registration.result.StoreRegistrationResult;
import com.mark.zumo.client.core.repository.StoreRepository;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 12. 30.
 */
public enum StoreRegistrationManager {
    INSTANCE;

    private StoreRepository storeRepository;

    StoreRegistrationManager() {
        storeRepository = StoreRepository.getInstance(new Bundle());
    }

    public Observable<List<StoreRegistrationRequest>> getStoreRegistrationRequestList() {
        return storeRepository.getStoreRegistrationRequestListAll(30)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<StoreRegistrationRequest> getStoreRegistrationRequestByUuidFromDisk(String requestUuid) {
        return storeRepository.getStoreRegistrationRequestByUuidFromDisk(requestUuid)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<StoreRegistrationResult> approveStoreRegistration(StoreRegistrationRequest storeRegistrationRequest) {
        return storeRepository.approveStoreRegistration(storeRegistrationRequest)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<StoreRegistrationResult> rejectStoreRegistration(StoreRegistrationRequest storeRegistrationRequest) {
        return storeRepository.rejectStoreRegistration(storeRegistrationRequest)
                .subscribeOn(Schedulers.io());
    }
}
