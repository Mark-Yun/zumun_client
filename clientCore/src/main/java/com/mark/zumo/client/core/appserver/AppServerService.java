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
    Single<Void> deleteGuestUser(uuid);
    
    @POST("menu/get")
    Single<Void> getMenuItem(store_uuid);
    
    @POST("menu/create")
    Single<Void> addMenuItem(store_uuid, menu_name, menu_price);
    
    @POST("store/create")
    Single<Void> createStore(store_name, longitude = 0, latitude = 0);
    
    @POST("store/get") // test, get all store uuid
    Single<Void> getStoreInfo();
}