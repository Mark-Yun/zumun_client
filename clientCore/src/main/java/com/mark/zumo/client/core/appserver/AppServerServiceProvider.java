package com.mark.zumo.client.core.appserver;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by mark on 18. 4. 30.
 */

public enum AppServerServiceProvider {
    INSTANCE;

    public final AppServerService service;

    AppServerServiceProvider() {
        service = new Retrofit.Builder()
                .baseUrl(AppServerService.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient())
                .build()
                .create(AppServerService.class);
    }

    private OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(interceptor())
                .build();
    }

    private Interceptor interceptor() {
        return chain -> {
            Request original = chain.request();

            //TODO change header
            Request request = original.newBuilder()
                    .header("User-Agent", "Your-App-Name")
                    .header("Accept", "application/vnd.yourapi.v1.full+json")
                    .method(original.method(), original.body())
                    .build();

            return chain.proceed(request);
        };
    }
}
