package com.mark.zumo.client.core.appserver;

import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.core.entity.MenuOption;
import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.entity.OrderDetail;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.entity.user.GuestUser;

import java.util.Collection;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by mark on 18. 4. 30.
 */

public interface NetworkRepository {

    String URL = "https://faca5l5t89.execute-api.ap-northeast-2.amazonaws.com/zumo_api/";

    @GET("users/guest/create")
    Observable<GuestUser> createGuestUser();

    @POST("users/guest/delete")
    Single<Void> deleteGuestUser(@Query("menu_uuid") String uuid);

    @GET("menu/get")
    Observable<List<Menu>> getMenuList(@Query("store_uuid") String storeUuid);

    @POST("menu/create")
    Single<Menu> createMenuItem(@Body Menu menu);

    @POST("store/create")
    Single<Store> createStore(@Body Store store);

    @GET("store/get")
    Observable<Store> getStore(@Query("store_uuid") String storeUuid);

    @GET("option/get")
    Observable<List<MenuOption>> getMenuOptionList(@Query("menu_uuid") String uuid);

    @POST("order/create")
    Observable<MenuOrder> createOrder(@Body Collection<OrderDetail> orderDetailCollection);
}