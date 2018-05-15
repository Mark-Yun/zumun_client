package com.mark.zumo.client.core.appserver;

import com.mark.zumo.client.core.entity.user.GuestUser;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by mark on 18. 4. 30.
 */

public interface AppServerService {
    String URL = "https://faca5l5t89.execute-api.ap-northeast-2.amazonaws.com/zumo_api/";

    @GET("users/guest/create")
    Single<GuestUser> createGuestUser();

    @POST("users/guest/delete")
    Single<Void> deleteGuestUser();
}