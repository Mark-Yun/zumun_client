/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.model;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.mark.zumo.client.core.appserver.request.registration.StoreRegistrationRequest;
import com.mark.zumo.client.core.appserver.request.registration.result.StoreRegistrationResult;
import com.mark.zumo.client.core.appserver.response.store.registration.StoreRegistrationResponse;
import com.mark.zumo.client.core.database.entity.SessionStore;
import com.mark.zumo.client.core.database.entity.SnsToken;
import com.mark.zumo.client.core.database.entity.Store;
import com.mark.zumo.client.core.repository.StoreRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import io.reactivex.Maybe;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 18. 4. 30.
 */

public enum StoreStoreManager {

    INSTANCE;

    private static final String TAG = "StoreStoreManager";

    private final StoreRepository storeRepository;

    StoreStoreManager() {
        storeRepository = StoreRepository.INSTANCE;

        registerToken();
    }

    public Observable<List<StoreRegistrationRequest>> getCombinedStoreRegistrationRequestByStoreUserUuid(String storeUserUuid) {
        return Observable.create((ObservableOnSubscribe<List<StoreRegistrationRequest>>) e -> {
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
                .distinctUntilChanged().subscribeOn(Schedulers.io());
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

    public Observable<Store> getStoreSessionObservable() {
        return storeRepository.getStoreSessionObservable()
                .subscribeOn(Schedulers.io());
    }

    public Maybe<Store> saveSessionStore(Store store) {
        return Maybe.just(SessionStore.from(store))
                .map(sessionStore -> {
                    storeRepository.saveSessionStore(sessionStore);
                    storeRepository.putSessionHeader(buildStoreSessionHeader(sessionStore));
                    return store.uuid;
                }).flatMap(this::createSnsToken)
                .flatMap(storeRepository::registerSnsToken)
                .map(x -> store)
                .subscribeOn(Schedulers.io());
    }

    private Maybe<String> getFCMToken() {
        return Maybe.create((MaybeOnSubscribe<String>) emitter ->
                FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "getFCMToken: getInstanceId failed", task.getException());
                        emitter.onComplete();
                        return;
                    }

                    InstanceIdResult result = task.getResult();
                    if (result == null) {
                        Log.w(TAG, "getFCMToken: getResult failed");
                        emitter.onComplete();
                        return;
                    }

                    String token = result.getToken();
                    Log.d(TAG, "getFCMToken: token=" + token);
                    emitter.onSuccess(token);
                    emitter.onComplete();
                })
        ).subscribeOn(Schedulers.io());
    }

    private Maybe<SnsToken> createSnsToken(String storeUuid) {
        return getFCMToken()
                .map(token -> new SnsToken(storeUuid, SnsToken.TokenType.ANDROID, token));
    }

    public void registerToken() {
        getStoreSessionMaybe()
                .map(store -> store.uuid)
                .flatMap(this::createSnsToken)
                .flatMap(storeRepository::registerSnsToken)
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    private Bundle buildStoreSessionHeader(final SessionStore sessionStore) {
        Bundle bundle = new Bundle();
        bundle.putString(Store.Schema.uuid, sessionStore.uuid);

        return bundle;
    }

    public void signOut() {
        Maybe.fromAction(storeRepository::removeStoreSession)
                .flatMap(x -> Maybe.fromAction(storeRepository::clearSessionHeader))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public Maybe<Store> getStoreSessionMaybe() {
        return storeRepository.getStoreSessionMaybe()
                .subscribeOn(Schedulers.io());
    }

    public Observable<Store> getStore(String storeUuid) {
        return storeRepository.getStore(storeUuid)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<StoreRegistrationResponse> createStoreRegistrationRequest(StoreRegistrationRequest storeRegistrationRequest) {
        return storeRepository.createStoreRegistrationRequest(storeRegistrationRequest)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<Store> updateSessionStoreName(String newName) {

        return storeRepository.getStoreSessionMaybe()
                .map(store -> Store.Builder.from(store)
                        .setName(newName)
                        .build())
                .flatMap(storeRepository::updateStore)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<Store> updateSessionStoreLocation(LatLng latLng) {

        return storeRepository.getStoreSessionMaybe()
                .map(store -> Store.Builder.from(store)
                        .setLatitude(latLng.latitude)
                        .setLongitude(latLng.longitude)
                        .build())
                .flatMap(storeRepository::updateStore)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<Store> updateSessionStoreCoverImageUrl(String coverImageUrl) {
        return storeRepository.getStoreSessionMaybe()
                .map(store -> Store.Builder.from(store)
                        .setCoverImageUrl(coverImageUrl)
                        .build())
                .flatMap(storeRepository::updateStore)
                .subscribeOn(Schedulers.io());

    }

    public Maybe<Store> updateSessionStoreThumbnailImageUrl(String thumbnailImageUrl) {
        return storeRepository.getStoreSessionMaybe()
                .map(store -> Store.Builder.from(store)
                        .setThumbnailUrl(thumbnailImageUrl)
                        .build())
                .flatMap(storeRepository::updateStore)
                .subscribeOn(Schedulers.io());
    }

}
