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
import android.util.Log;

import com.google.gson.GsonBuilder;
import com.mark.zumo.client.core.app.BuildConfig;
import com.mark.zumo.client.core.util.context.ContextHolder;

import java.util.Set;
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

    private NetworkRepository networkRepository;
    private PaymentService paymentService;

    private Bundle headerBundle;
    private Bundle currentHeaderBundle;

    AppServerServiceProvider() {
        headerBundle = buildDefaultHeader();

        currentHeaderBundle = headerBundle;
        currentHeaderBundle.putAll(headerBundle);

        networkRepository = buildNetworkRepository(currentHeaderBundle);
        paymentService = buildPaymentService(currentHeaderBundle);
    }

    private static OkHttpClient okHttpClient(final Interceptor interceptor) {
        return new OkHttpClient.Builder()
                .cache(new Cache(ContextHolder.getContext().getCacheDir(), MAX_CACHE_SIZE))
                .addInterceptor(interceptor)
                .addInterceptor(new HttpLoggingInterceptor().setLevel(BuildConfig.BUILD_TYPE.httpLoggerLevel))
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    private static Interceptor createInterceptor(@NonNull Bundle bundle) {
        return chain -> {
            Request original = chain.request();
            Request.Builder builder = original.newBuilder();

            Set<String> keySet = bundle.keySet();
            if (keySet != null) {
                for (String key : keySet) {
                    String value = bundle.getString(key);
                    if (value == null) {
                        continue;
                    }

                    builder.header(key, value);
                }
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
        bundle.putString(ApiKey.KEY, BuildConfig.BUILD_TYPE.appServerApiKey);
        bundle.putString(Model.KEY, Build.MODEL);
        bundle.putString(Manufacturer.KEY, Build.MANUFACTURER);
        bundle.putString(AppVersion.KEY, getAppVersion());
        return bundle;
    }

    private static String serverVersionInfo() {
        Log.d(TAG, "serverVersionInfo: appVersion-" + getAppVersion());
        String[] split = getAppVersion().split("\\.");
        int majorVersion = 100 * Integer.parseInt(split[0]) + Integer.parseInt(split[1]);
        return "v" + String.valueOf(majorVersion) + "/";
    }

    private static String getAppVersion() {
        try {
            Context context = ContextHolder.getContext();
            String packageName = context.getPackageName();
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "getAppVersion: ", e);
            return "None";
        }
    }

    public NetworkRepository networkRepository() {
        if (!currentHeaderBundle.equals(headerBundle)) {
            currentHeaderBundle.putAll(headerBundle);
        }

        networkRepository = buildNetworkRepository(currentHeaderBundle);
        return networkRepository;
    }

    public PaymentService paymentService() {
        if (!currentHeaderBundle.equals(headerBundle)) {
            currentHeaderBundle.putAll(headerBundle);
        }

        paymentService = buildPaymentService(currentHeaderBundle);
        return paymentService;
    }

    public void putSessionHeader(Bundle bundle) {
        headerBundle.putAll(bundle);
    }

    public void clearSessionHeader(String key) {
        headerBundle.remove(key);
    }

    private NetworkRepository buildNetworkRepository(final Bundle bundle) {
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.BUILD_TYPE.appServerUrl + serverVersionInfo())
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient(createInterceptor(bundle)))
                .build()
                .create(NetworkRepository.class);
    }

    private PaymentService buildPaymentService(final Bundle bundle) {
        return paymentService = new Retrofit.Builder()
                .baseUrl(BuildConfig.BUILD_TYPE.paymentServiceUrl + serverVersionInfo())
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient(createInterceptor(bundle)))
                .build()
                .create(PaymentService.class);
    }

    private interface ContentType {
        String KEY = "Content-type";
        String VALUE = "application/json";
    }

    private interface ApiKey {
        String KEY = "x-api-key";
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
