/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.appserver;

import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.core.entity.MenuOption;
import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.entity.OrderDetail;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.entity.user.GuestUser;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by mark on 18. 4. 30.
 */

public interface NetworkRepository {

    String URL = "https://akxjj18zh8.execute-api.ap-northeast-2.amazonaws.com/api/";

    @POST("users/guest")
    Maybe<GuestUser> createGuestUser();

    @GET("menu")
    Observable<List<Menu>> getMenuList(@Query("store_uuid") String storeUuid);

    @GET("store/{store_uuid}")
    Observable<Store> getStore(@Path("store_uuid") String storeUuid);

    @GET("option")
    Observable<List<MenuOption>> getMenuOptionList(@Query("menu_uuid") String uuid);

    @POST("order")
    Observable<MenuOrder> createOrder(@Body List<OrderDetail> orderDetailCollection);

    @POST("order/{menu_order_uuid}")
    Maybe<MenuOrder> getMenuOrder(@Path("menu_order_uuid") String uuid);

    @PUT("store/{store_uuid}")
    Maybe<Store> updateStore(@Path("store_uuid") String storeUuid,
                             @Body Store store);
}