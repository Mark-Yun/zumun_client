/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.payment.kakao.server;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mark.zumo.client.core.util.context.ContextHolder;

import java.io.File;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by mark on 18. 5. 30.
 */
public enum KakaoPayServiceProvider {
    INSTANCE;

    private static final String TAG = "KakaoPayServiceProvider";

    private static final int MAX_CACHE_SIZE = 5 * 1024 * 1024;

    public KakaoPayService service;

    KakaoPayServiceProvider() {
        service = buildService("");
    }

    @NonNull
    private static HttpLoggingInterceptor logger() {
        return new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    public KakaoPayService buildService(String token) {
        Bundle bundle = buildDefaultHeader(token);
        Interceptor interceptor = interceptor(bundle);
        OkHttpClient okHttpClient = okHttpClient(interceptor);
        return service = buildKakaoPayService(okHttpClient);
    }

    private Bundle buildDefaultHeader(String token) {
        Bundle bundle = new Bundle();
        bundle.putString(ContentType.KEY, ContentType.VALUE);
        bundle.putString(Authorization.KEY, Authorization.TOKEN_VALUE + token);
        return bundle;
    }

    private Interceptor interceptor(@NonNull Bundle bundle) {
        return getInterceptor(bundle);
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

    private KakaoPayService buildKakaoPayService(final OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(KakaoPayService.URL)
                .addConverterFactory(scalarsConverterFactory())
                .addConverterFactory(gsonConverterFactory())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build()
                .create(KakaoPayService.class);
    }

    @NonNull
    private ScalarsConverterFactory scalarsConverterFactory() {
        return ScalarsConverterFactory.create();
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

    private OkHttpClient okHttpClient(final Interceptor interceptor) {
        return new OkHttpClient.Builder()
                .cache(cache())
                .addInterceptor(interceptor)
                .addInterceptor(logger())
                .build();
    }

    private interface ContentType {
        String KEY = "Content-type";
        String VALUE = "application/x-www-form-urlencoded;charset=utf-8";
    }

    private interface Authorization {
        //TODO: replace to token
        String KEY = "Authorization";
        String AK_VALUE = "KakaoAK d17ffca1e2c89e95fcb9de08a41eee12";
        String TOKEN_VALUE = "Bearer ";
    }

}
