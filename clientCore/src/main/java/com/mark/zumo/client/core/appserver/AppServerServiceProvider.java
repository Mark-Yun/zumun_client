/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.appserver;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mark.zumo.client.core.util.context.ContextHolder;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by mark on 18. 4. 30.
 */

public enum AppServerServiceProvider {
    INSTANCE;

    private static final String TAG = "AppServerServiceProvider";

    private static final int MAX_CACHE_SIZE = 5 * 1024 * 1024;

    public NetworkRepository networkRepository;
    public PaymentService paymentService;

    private Bundle headerBundle;

    AppServerServiceProvider() {
        networkRepository = buildDefaultService();
    }

    @NonNull
    private static GsonConverterFactory gsonConverterFactory() {
        return GsonConverterFactory.create(gson());
    }

    @NonNull
    private static Gson gson() {
        return new GsonBuilder()
                .setLenient()
                .create();
    }

    private static OkHttpClient okHttpClient(final Interceptor interceptor) {
        return new OkHttpClient.Builder()
                .cache(cache())
                .addInterceptor(interceptor)
                .addInterceptor(logger())
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .build();
    }

    @NonNull
    private static HttpLoggingInterceptor logger() {
        return new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    @NonNull
    private static Cache cache() {
        File cacheDir = ContextHolder.getContext().getCacheDir();
        return new Cache(cacheDir, MAX_CACHE_SIZE);
    }

    private static Interceptor interceptor(@NonNull Bundle bundle) {
        return chain -> {
            Request original = chain.request();
            Request.Builder builder = original.newBuilder();

            for (String key : bundle.keySet()) {
                String value = bundle.getString(key);
                if (value == null) continue;

                builder.header(key, value);
            }
            builder.header(ContentType.KEY, ContentType.VALUE);

            Request request = builder
                    .method(original.method(), original.body())
                    .build();

            return chain.proceed(request);
        };
    }

    private static Bundle buildDefaultHeader() {
        Bundle bundle = new Bundle();
        bundle.putString(AndroidSdk.KEY, String.valueOf(Build.VERSION.SDK_INT));
        bundle.putString(Model.KEY, Build.MODEL);
        bundle.putString(Manufacturer.KEY, Build.MANUFACTURER);
        bundle.putString(AppVersion.KEY, getAppVersion());
        return bundle;
    }

    private static String getAppVersion() {
        try {
            Context context = ContextHolder.getContext();
            String packageName = context.getPackageName();
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "None";
        }
    }

    public NetworkRepository buildNetworkRepository(final Bundle bundle) {
        Bundle mergedBundle = buildDefaultHeader();
        mergedBundle.putAll(bundle);
        headerBundle = mergedBundle;
        return networkRepository = buildNetworkRepositoryInternal(mergedBundle);
    }

    private NetworkRepository buildDefaultService() {
        return buildNetworkRepositoryInternal(buildDefaultHeader());
    }

    private NetworkRepository buildNetworkRepositoryInternal(final Bundle bundle) {
        Interceptor interceptor = interceptor(bundle);
        OkHttpClient okHttpClient = okHttpClient(interceptor);

        return new Retrofit.Builder()
                .baseUrl(NetworkRepository.URL)
                .addConverterFactory(gsonConverterFactory())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build()
                .create(NetworkRepository.class);
    }

    public PaymentService buildPaymentService() {
        Interceptor interceptor = interceptor(headerBundle);
        OkHttpClient okHttpClient = okHttpClient(interceptor);

        return paymentService = new Retrofit.Builder()
                .baseUrl(PaymentService.URL)
                .addConverterFactory(gsonConverterFactory())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build()
                .create(PaymentService.class);
    }

    private interface ContentType {
        String KEY = "Content-type";
        String VALUE = "application/json";
    }

    private interface AndroidSdk {
        String KEY = "android_sdk";
    }

    private interface Model {
        String KEY = "model";
    }

    private interface Manufacturer {
        String KEY = "manufacturer";
    }

    private interface AppVersion {
        String KEY = "app_version";
    }
}
