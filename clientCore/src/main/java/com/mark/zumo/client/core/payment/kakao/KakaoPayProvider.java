/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.payment.kakao;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mark.zumo.client.core.appserver.NetworkRepository;
import com.mark.zumo.client.core.util.context.ContextHolder;

import java.io.File;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by mark on 18. 5. 30.
 */
public enum KakaoPayProvider {
    INSTANCE;

    private static final String TAG = "KakaoPayProvider";

    private static final int MAX_CACHE_SIZE = 5 * 1024 * 1024;

    public KakaoPayService service;

    KakaoPayProvider() {

        buildDefaultHeader()
                .flatMap(this::interceptor)
                .flatMap(this::okHttpClient)
                .flatMap(this::buildNetworkRepository)
                .doOnSuccess(this::setService)
                .doOnError(throwable -> Log.e(TAG, "KakaoPayProvider: ", throwable))
                .subscribe();
    }

    private void setService(final KakaoPayService service) {
        this.service = service;
    }

    private Single<Bundle> buildDefaultHeader() {
        return Single.fromCallable(() -> {
            Bundle bundle = new Bundle();
            bundle.putString(ContentType.KEY, ContentType.VALUE);
            bundle.putString(Authorization.KEY, Authorization.VALUE);
            return bundle;
        }).subscribeOn(Schedulers.io());
    }

    private Single<Interceptor> interceptor(@NonNull Bundle bundle) {
        return Single.fromCallable(() -> getInterceptor(bundle))
                .subscribeOn(Schedulers.io());
    }

    @NonNull
    private Gson gson() {
        return new GsonBuilder()
                .setLenient()
                .create();
    }

    @NonNull
    private Cache cache() {
        File cacheDir = ContextHolder.getContext().getCacheDir();
        return new Cache(cacheDir, MAX_CACHE_SIZE);
    }

    @NonNull
    private GsonConverterFactory gsonConverterFactory() {
        return GsonConverterFactory.create(gson());
    }

    private Single<KakaoPayService> buildNetworkRepository(final OkHttpClient okHttpClient) {
        return Single.fromCallable
                (() -> new Retrofit.Builder()
                        .baseUrl(NetworkRepository.URL)
                        .addConverterFactory(gsonConverterFactory())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .client(okHttpClient)
                        .build()
                        .create(KakaoPayService.class)
                ).subscribeOn(Schedulers.io());
    }

    @NonNull
    private Interceptor getInterceptor(final @NonNull Bundle bundle) {
        return chain -> {
            Request original = chain.request();
            Request.Builder builder = original.newBuilder();

            for (String key : bundle.keySet()) {
                String value = bundle.getString(key);
                builder.header(key, value);
            }

            Request request = builder
                    .method(original.method(), original.body())
                    .build();

            return chain.proceed(request);
        };
    }

    private Single<OkHttpClient> okHttpClient(final Interceptor interceptor) {
        return Single.fromCallable
                (() -> new OkHttpClient.Builder()
                        .cache(cache())
                        .addInterceptor(interceptor)
                        .build()
                ).subscribeOn(Schedulers.io());
    }

    private interface ContentType {
        String KEY = "Content-type";
        String VALUE = "application/x-www-form-urlencoded;charset=utf-8";
    }

    private interface Authorization {
        //TODO: replace to token
        String KEY = "Authorization";
        String VALUE = "d17ffca1e2c89e95fcb9de08a41eee12";
    }

}
