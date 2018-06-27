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
import com.mark.zumo.client.core.entity.SnsToken;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.entity.user.GuestUser;

import java.util.List;

import io.reactivex.Maybe;
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
    Maybe<List<Menu>> getMenuList(@Query(Menu.Schema.storeUuid) String storeUuid);

    @GET("store/{" + Store.Schema.uuid + "}")
    Maybe<Store> getStore(@Path(Store.Schema.uuid) String storeUuid);

    @GET("menu/option")
    Maybe<List<MenuOption>> getMenuOptionListByMenuUuid(@Query(MenuOption.Schema.menuUuid) String menuUuid);

    @POST("order")
    Maybe<MenuOrder> createOrder(@Body List<OrderDetail> orderDetailCollection);

    @GET("order/{" + MenuOrder.Schema.uuid + "}")
    Maybe<MenuOrder> getMenuOrder(@Path(MenuOrder.Schema.uuid) String uuid);

    @PUT("store/{" + Store.Schema.uuid + "}")
    Maybe<Store> updateStore(@Path(Store.Schema.uuid) String storeUuid,
                             @Body Store store);

    @POST("token")
    Maybe<SnsToken> createSnsToken(@Body SnsToken snsToken);

    @GET("order/detail")
    Maybe<List<OrderDetail>> getOrderDetailList(@Query(OrderDetail.Schema.menuOrderUuid) String menuOrderUuid);

    @GET("menu/option/{" + MenuOption.Schema.uuid + "}")
    Maybe<MenuOption> getMenuOptionList(@Path(MenuOption.Schema.uuid) String menuOptionUuid);

    @GET("order")
    Maybe<List<MenuOrder>> getMenuOrderListByCustomerUuid(@Query(MenuOrder.Schema.customerUuid) String customerUuid,
                                                          @Query("offset") int offset,
                                                          @Query("limit") int limit);

    @GET("order")
    Maybe<List<MenuOrder>> getMenuOrderListByStoreUuid(@Query(MenuOrder.Schema.storeUuid) String customerUuid,
                                                       @Query("offset") int offset,
                                                       @Query("limit") int limit);

    @PUT("order/{menu_order_uuid}/state")
    Maybe<MenuOrder> updateMenuOrderState(@Path("menu_order_uuid") String menuOrderUuid,
                                          @Body int state);
}