package com.mark.zumo.client.core.appserver;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mark.zumo.client.core.repository.SessionRepository;
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
 * Created by mark on 18. 4. 30.
 */

public enum AppServerServiceProvider {
    INSTANCE;

    private static final String TAG = "AppServerServiceProvider";

    private static final int MAX_CACHE_SIZE = 20 * 1024 * 1024;
    public NetworkRepository networkRepository;

    AppServerServiceProvider() {
        buildSessionHeader("");
    }

    public void buildSessionHeader(String uuid) {
        buildDefaultHeader()
                .flatMap(bundle -> addGuestUuid(bundle, uuid))
                .flatMap(this::interceptor)
                .flatMap(this::okHttpClient)
                .flatMap(this::buildNetworkRepository)
                .doOnSuccess(this::setNetworkRepository)
                .doOnError(throwable -> Log.e(TAG, "AppServerServiceProvider: ", throwable))
                .subscribe();
    }

    private Single<NetworkRepository> buildNetworkRepository(final OkHttpClient okHttpClient) {
        return Single.fromCallable
                (() -> new Retrofit.Builder()
                        .baseUrl(NetworkRepository.URL)
                        .addConverterFactory(gsonConverterFactory())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .client(okHttpClient)
                        .build()
                        .create(NetworkRepository.class)
                ).subscribeOn(Schedulers.io());
    }

    private void setNetworkRepository(final NetworkRepository networkRepository) {
        this.networkRepository = networkRepository;
    }

    @NonNull
    private GsonConverterFactory gsonConverterFactory() {
        return GsonConverterFactory.create(gson());
    }

    @NonNull
    private Gson gson() {
        return new GsonBuilder()
                .setLenient()
                .create();
    }

    private Single<OkHttpClient> okHttpClient(final Interceptor interceptor) {
        return Single.fromCallable
                (() -> new OkHttpClient.Builder()
                        .cache(cache())
                        .addInterceptor(interceptor)
                        .build()
                ).subscribeOn(Schedulers.io());
    }

    @NonNull
    private Cache cache() {
        File cacheDir = ContextHolder.getContext().getCacheDir();
        return new Cache(cacheDir, MAX_CACHE_SIZE);
    }

    private Single<Interceptor> interceptor(@NonNull Bundle bundle) {
        return Single.fromCallable(() -> getInterceptor(bundle))
                .subscribeOn(Schedulers.io());
    }

    @NonNull
    private Interceptor getInterceptor(final @NonNull Bundle bundle) {
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

    private Single<Bundle> buildDefaultHeader() {
        //TODO: Impl
        return Single.fromCallable(() -> {
            Bundle bundle = new Bundle();
            bundle.putString(AndroidSdk.KEY, String.valueOf(Build.VERSION.SDK_INT));
            bundle.putString(Model.KEY, Build.MODEL);
            bundle.putString(Manufacturer.KEY, getAppVersion());
            return bundle;
        }).subscribeOn(Schedulers.io());
    }

    private String getAppVersion() {
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

    private Single<Bundle> addGuestUuid(Bundle bundle, String uuid) {
        return Single.fromCallable
                (() -> {
                    if (!uuid.isEmpty()) {
                        bundle.putString(SessionRepository.KEY_GUEST_USER_UUID, uuid);
                    }
                    return bundle;
                }).subscribeOn(Schedulers.io());
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
}
