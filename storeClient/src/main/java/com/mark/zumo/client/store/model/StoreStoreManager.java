/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.model;

import com.google.android.gms.maps.model.LatLng;
import com.mark.zumo.client.core.appserver.request.registration.StoreRegistrationRequest;
import com.mark.zumo.client.core.appserver.request.registration.result.StoreRegistrationResult;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.repository.SessionRepository;
import com.mark.zumo.client.core.repository.StoreRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 4. 30.
 */

public enum StoreStoreManager {

    INSTANCE;

    private final SessionRepository sessionRepository;
    private final Maybe<StoreRepository> storeRepositoryMaybe;

    StoreStoreManager() {
        sessionRepository = SessionRepository.INSTANCE;
        storeRepositoryMaybe = sessionRepository.getStoreSession()
                .map(StoreRepository::getInstance);
    }

    public Maybe<Store> updateStoreName(Store store, String newName) {
        Store newStore = Store.Builder.from(store)
                .setName(newName)
                .build();

        return storeRepositoryMaybe.flatMap(storeRepository -> storeRepository.updateStore(newStore))
                .subscribeOn(Schedulers.io());
    }

    public Maybe<Store> updateStoreLocation(Store store, LatLng latLng) {
        Store newStore = Store.Builder.from(store)
                .setLatitude(latLng.latitude)
                .setLongitude(latLng.longitude)
                .build();

        return storeRepositoryMaybe.flatMap(storeRepository -> storeRepository.updateStore(newStore))
                .subscribeOn(Schedulers.io());
    }

    public Maybe<Store> updateStoreCoverImageUrl(Store store, String coverImageUrl) {
        Store newStore = Store.Builder.from(store)
                .setCoverImageUrl(coverImageUrl)
                .build();

        return storeRepositoryMaybe.flatMap(storeRepository -> storeRepository.updateStore(newStore))
                .subscribeOn(Schedulers.io());

    }

    public Maybe<Store> updateStoreThumbnailImageUrl(Store store, String thumbnailImageUrl) {
        Store newStore = Store.Builder.from(store)
                .setThumbnailUrl(thumbnailImageUrl)
                .build();

        return storeRepositoryMaybe.flatMap(storeRepository -> storeRepository.updateStore(newStore))
                .subscribeOn(Schedulers.io());
    }

    public Observable<List<StoreRegistrationRequest>> getCombinedStoreRegistrationRequestByStoreUserUuid(String storeUserUuid) {
        return storeRepositoryMaybe.flatMapObservable(storeRepository ->
                Observable.create((ObservableOnSubscribe<List<StoreRegistrationRequest>>) e -> {
                    List<StoreRegistrationRequest> requestList = new CopyOnWriteArrayList<>();
                    List<StoreRegistrationResult> resultList = new CopyOnWriteArrayList<>();

                    Set<Class> nextToken = new CopyOnWriteArraySet<>();
                    Set<Class> completeToken = new CopyOnWriteArraySet<>();

                    nextToken.add(StoreRegistrationRequest.class);
                    completeToken.add(StoreRegistrationRequest.class);
                    storeRepository.getStoreRegistrationRequestListByStoreUserUuid(storeUserUuid)
                            .subscribeOn(Schedulers.newThread())
                            .doOnNext(storeRegistrationRequests -> {
                                requestList.clear();
                                requestList.addAll(storeRegistrationRequests);
                                nextToken.remove(StoreRegistrationRequest.class);
                                if (nextToken.isEmpty()) {
                                    List<StoreRegistrationRequest> mappedRequestList = mapStoreRegistrationRequest(requestList, resultList);
                                    e.onNext(mappedRequestList);
                                }
                            })
                            .doOnComplete(() -> {
                                completeToken.remove(StoreRegistrationRequest.class);
                                if (completeToken.isEmpty()) {
                                    e.onComplete();
                                }
                            })
                            .subscribe();

                    nextToken.add(StoreRegistrationResult.class);
                    completeToken.add(StoreRegistrationResult.class);
                    storeRepository.getStoreRegistrationResultListByStoreUserUuid(storeUserUuid)
                            .subscribeOn(Schedulers.newThread())
                            .doOnNext(registrationResults -> {
                                resultList.clear();
                                resultList.addAll(registrationResults);
                                nextToken.remove(StoreRegistrationResult.class);
                                if (nextToken.isEmpty()) {
                                    List<StoreRegistrationRequest> mappedRequestList = mapStoreRegistrationRequest(requestList, resultList);
                                    e.onNext(mappedRequestList);
                                }
                            })
                            .doOnComplete(() -> {
                                completeToken.remove(StoreRegistrationResult.class);
                                if (completeToken.isEmpty()) {
                                    e.onComplete();
                                }
                            })
                            .subscribe();
                })
        ).distinctUntilChanged().subscribeOn(Schedulers.io());
    }

    private List<StoreRegistrationRequest> mapStoreRegistrationRequest(final List<StoreRegistrationRequest> storeRegistrationRequests,
                                                                       final List<StoreRegistrationResult> storeRegistrationResults) {
        List<StoreRegistrationRequest> requestList = new ArrayList<>();
        Map<String, List<StoreRegistrationResult>> resultMap = new HashMap<>();
        for (StoreRegistrationResult storeRegistrationResult : storeRegistrationResults) {
            String requestUuid = storeRegistrationResult.storeRegistrationRequestUuid;
            if (!resultMap.containsKey(requestUuid)) {
                resultMap.put(requestUuid, new ArrayList<>());
            }
            resultMap.get(requestUuid).add(storeRegistrationResult);
        }

        for (StoreRegistrationRequest registrationRequest : storeRegistrationRequests) {
            registrationRequest.resultList = new ArrayList<>();
            if (resultMap.containsKey(registrationRequest.uuid)) {
                registrationRequest.resultList.addAll(resultMap.get(registrationRequest.uuid));
            }
            requestList.add(registrationRequest);
        }

        return requestList;
    }

    public Observable<Store> getStore(String storeUuid) {
        return storeRepositoryMaybe.flatMapObservable(storeRepository ->
                storeRepository.getStore(storeUuid)
        ).subscribeOn(Schedulers.io());
    }

    public Maybe<StoreRegistrationRequest> createStoreRegistrationRequest(StoreRegistrationRequest storeRegistrationRequest) {
        return storeRepositoryMaybe.flatMap(storeRepository ->
                storeRepository.createStoreRegistrationRequest(storeRegistrationRequest)
        ).subscribeOn(Schedulers.io());
    }
}
