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

    private static final int MAX_CACHE_SIZE = 20 * 1024 * 1024;
    public NetworkRepository networkRepository;

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
                .build();
    }

    @NonNull
    private static HttpLoggingInterceptor logger() {
        return new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC);
    }

    @NonNull
    private static Cache cache() {
        File cacheDir = ContextHolder.getContext().getCacheDir();
        return new Cache(cacheDir, MAX_CACHE_SIZE);
    }

    private static Interceptor interceptor(@NonNull Bundle bundle) {
        return getInterceptor(bundle);
    }

    @NonNull
    private static Interceptor getInterceptor(final @NonNull Bundle bundle) {
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
        //TODO: Impl
        Bundle bundle = new Bundle();
        bundle.putString(AndroidSdk.KEY, String.valueOf(Build.VERSION.SDK_INT));
        bundle.putString(Model.KEY, Build.MODEL);
        bundle.putString(Manufacturer.KEY, getAppVersion());
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

    public NetworkRepository buildSessionHeader(final Bundle bundle) {
        Bundle mergedBundle = buildDefaultHeader();
        mergedBundle.putAll(bundle);
        return networkRepository = buildNetworkRepository(mergedBundle);
    }

    private NetworkRepository buildDefaultService() {
        return buildNetworkRepository(buildDefaultHeader());
    }

    private NetworkRepository buildNetworkRepository(final Bundle bundle) {
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
