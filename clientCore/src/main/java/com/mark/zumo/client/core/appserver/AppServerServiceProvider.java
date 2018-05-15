package com.mark.zumo.client.core.appserver;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.mark.zumo.client.core.entity.user.GuestUser;
import com.mark.zumo.client.core.security.EncryptionUtil;
import com.mark.zumo.client.core.security.SecurePreferences;
import com.mark.zumo.client.core.util.context.ContextHolder;

import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
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

    private static final String KEY_GUEST_USER_UUID = "guest_user_uuid";
    private static final String TAG = "AppServerServiceProvider";
    private static final String KEY_ANDROID_SDK = "android_sdk";
    private static final String KEY_MODEL = "model";
    private static final String KEY_MANUFACTURER = "manufacturer";

    public AppServerService service;

    AppServerServiceProvider() {
        service = new Retrofit.Builder()
                .baseUrl(AppServerService.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(AppServerService.class);

        Single.zip(buildHeaderData(),
                context().flatMap(this::securePreferences)
                        .flatMap(this::getGuestUserUuid)
                        .onErrorResumeNext(this::acquireGuestUserUuid)
                , this::addGuestUuid)
                .flatMap(this::interceptor)
                .flatMap(this::okHttpClient)
                .flatMap(this::appServerService)
                .subscribeOn(Schedulers.newThread())
                .subscribe(appServerService -> this.service = appServerService
                        , throwable -> Log.e(TAG, "AppServerServiceProvider: ", throwable));
    }

    private Single<AppServerService> appServerService(final OkHttpClient okHttpClient) {
        return Single.fromCallable(() -> new Retrofit.Builder()
                .baseUrl(AppServerService.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build()
                .create(AppServerService.class));
    }

    private Single<OkHttpClient> okHttpClient(final Interceptor interceptor) {
        return Single.fromCallable(() ->
                new OkHttpClient.Builder()
                        .addInterceptor(interceptor)
                        .build()
        );
    }

    private Single<Interceptor> interceptor(@NonNull Bundle bundle) {
        return Single.fromCallable(() ->
                chain -> {
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
                }
        );
    }

    private Single<Bundle> buildHeaderData() {
        //TODO: Impl
        return Single.fromCallable(() -> {
            Bundle bundle = new Bundle();
            bundle.putString(KEY_ANDROID_SDK, String.valueOf(Build.VERSION.SDK_INT));
            bundle.putString(KEY_MODEL, Build.MODEL);
            bundle.putString(KEY_MANUFACTURER, getAppVersion());
            return bundle;
        });
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

    private Single<SecurePreferences> securePreferences(final Context context) {
        return Single.fromCallable(() -> new SecurePreferences(context, EncryptionUtil.PREFERENCE_NAME, EncryptionUtil.SECURE_KEY, true));
    }

    private Single<Context> context() {
        return Single.fromCallable(ContextHolder::getContext);
    }

    private Single<String> acquireGuestUserUuid(Throwable throwable) {
        Log.d(TAG, "acquireGuestUserUuid: " + throwable.getMessage());

        return Single.zip(
                context().flatMap(this::securePreferences)
                , service.createGuestUser().map(GuestUser::getUuid)
                , this::saveGuestUserUuid);
    }

    private String saveGuestUserUuid(final SecurePreferences securePreferences, final String uuid) {
        Log.d(TAG, "saveGuestUserUuid: uuid-" + uuid);
        securePreferences.put(KEY_GUEST_USER_UUID, uuid);
        return uuid;
    }

    private Single<String> getGuestUserUuid(final SecurePreferences securePreferences) {
        return Single.create(e -> {
            String guestUserUuid = securePreferences.getString(KEY_GUEST_USER_UUID);
            if (guestUserUuid == null || guestUserUuid.isEmpty()) {
                e.onError(new RuntimeException("Not exist"));
            } else {
                e.onSuccess(guestUserUuid);
            }
        });
    }

    private Bundle addGuestUuid(Bundle bundle, String uuid) {
        bundle.putString(KEY_GUEST_USER_UUID, uuid);
        return bundle;
    }
}
