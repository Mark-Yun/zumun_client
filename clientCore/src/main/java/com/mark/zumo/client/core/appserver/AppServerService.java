package com.mark.zumo.client.core.appserver;

import com.mark.zumo.client.core.entity.user.User;

import io.reactivex.Single;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by mark on 18. 4. 30.
 */

public interface AppServerService {
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://faca5l5t89.execute-api.ap-northeast-2.amazonaws.com/zumo_api")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build();

    @GET("users/guest/create")
    Single<String> createGuestUser();

    @POST("users/guest/delete")
    Single<Void> deleteGuestUser();
}