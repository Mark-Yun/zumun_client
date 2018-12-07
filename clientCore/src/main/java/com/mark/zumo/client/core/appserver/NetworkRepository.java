/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.appserver;

import com.mark.zumo.client.core.appserver.request.RequestUpdateCategoriesOfMenu;
import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.core.entity.MenuCategory;
import com.mark.zumo.client.core.entity.MenuDetail;
import com.mark.zumo.client.core.entity.MenuOption;
import com.mark.zumo.client.core.entity.MenuOptionCategory;
import com.mark.zumo.client.core.entity.MenuOptionDetail;
import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.entity.OrderDetail;
import com.mark.zumo.client.core.entity.SnsToken;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.entity.user.GuestUser;

import java.util.List;

import io.reactivex.Maybe;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by mark on 18. 4. 30.
 */

public interface NetworkRepository {

    @POST("users/customer/guest")
    Maybe<GuestUser> createGuestUser();


    @PUT("store/{" + Store.Schema.uuid + "}")
    Maybe<Store> updateStore(@Path(Store.Schema.uuid) String storeUuid,
                             @Body Store store);

    @GET("store/{" + Store.Schema.uuid + "}")
    Maybe<Store> getStore(@Path(Store.Schema.uuid) String storeUuid);

    @GET("store")
    Maybe<List<Store>> getNearByStore(@Query("latitude") final double latitude,
                                      @Query("longitude") final double longitude,
                                      @Query("distance") final double distanceKm);


    @GET("menu")
    Maybe<List<Menu>> getMenuList(@Query(Menu.Schema.storeUuid) String storeUuid);

    @GET("menu/{" + Menu.Schema.uuid + "}")
    Maybe<Menu> getMenu(@Path(Menu.Schema.uuid) String menuUuid);

    @PUT("menu/{" + Menu.Schema.uuid + "}")
    Maybe<Menu> updateMenu(@Path(Menu.Schema.uuid) String menuUuid,
                           @Body Menu menu);

    @PUT("menu/{" + Menu.Schema.uuid + "}/category")
    Maybe<Menu> updateCategoryInMenu(@Path(Menu.Schema.uuid) String menuUuid,
                                     @Body MenuCategory menuCategory);


    @POST("menu/detail")
    Maybe<List<MenuDetail>> createMenuDetailList(@Body List<MenuDetail> menuDetailList);

    @DELETE("menu/detail/{" + MenuDetail.Schema.uuid + "}")
    Maybe<MenuDetail> deleteMenuDetail(@Path(MenuDetail.Schema.uuid) String menuDetailUuid);

    @PUT("menu/detail/")
    Maybe<List<MenuDetail>> updateCategoriesOfMenu(@Query(RequestUpdateCategoriesOfMenu.Schema.menuUuid) String menuUuid,
                                                   @Body List<MenuDetail> menuDetailList);

    @PUT("menu/detail")
    Maybe<List<MenuDetail>> updateMenusOfCategory(@Query(MenuCategory.Schema.uuid) String categoryUuid,
                                                  @Body List<MenuDetail> menuDetailList);

    @GET("menu/detail")
    Maybe<List<MenuDetail>> getMenuDetailByStoreUuid(@Query(MenuDetail.Schema.storeUuid) String storeUuid);

    @GET("menu/detail")
    Maybe<List<MenuDetail>> getMenuDetailByCategoryUuid(@Query(MenuDetail.Schema.menuCategoryUuid) String menuCategoryUuid);


    @POST("menu/option")
    Maybe<MenuOption> createMenuOption(final @Body MenuOption menuOption);

    @POST("menu/option")
    Maybe<List<MenuOption>> createMenuOptionList(final @Body List<MenuOption> menuOptionList);

    @GET("menu/option/{" + MenuOption.Schema.uuid + "}")
    Maybe<MenuOption> getMenuOption(@Path(MenuOption.Schema.uuid) String menuOptionUuid);

    @GET("menu/option")
    Maybe<List<MenuOption>> getMenuOptionListByStoreUuid(@Query(MenuOptionDetail.Schema.storeUuid) String storeUuid);

    @GET("menu/option")
    Maybe<List<MenuOption>> getMenuOptionListByMenuOptionCategoryUuid(final @Query(MenuOption.Schema.menuOptionCategoryUuid) String menuOptionCategoryUuid);

    @PUT("menu/option/{" + MenuOption.Schema.uuid + "}")
    Maybe<MenuOption> updateMenuOption(final @Body MenuOption menuOption);

    @PUT("menu/option")
    Maybe<List<MenuOption>> updateMenuOptions(final @Body List<MenuOption> menuOptionList);

    @DELETE("menu/option/{" + MenuOption.Schema.uuid + "}")
    Maybe<MenuOption> deleteMenuOption(final @Path(MenuOption.Schema.uuid) String menuOptionUuid);


    @POST("menu/option/detail")
    Maybe<List<MenuOptionDetail>> createMenuOptionDetailList(@Body List<MenuOptionDetail> menuOptionDetailList);

    @GET("menu/option/detail")
    Maybe<List<MenuOptionDetail>> getMenuOptionDetailListByMenuOptionByStoreUuid(@Query(MenuOptionDetail.Schema.storeUuid) String storeUuid);

    @GET("menu/option/detail")
    Maybe<List<MenuOptionDetail>> getMenuOptionDetailListByStoreUuid(@Query(MenuOptionDetail.Schema.storeUuid) String storeUuid);

    @PUT("menu/option/detail")
    Maybe<List<MenuOptionDetail>> updateMenuOptionDetailList(@Body List<MenuOptionDetail> menuOptionDetailList);

    @DELETE("menu/option/detail/{" + MenuOptionDetail.Schema.id + "}")
    Maybe<MenuOptionDetail> deleteMenuOptionDetail(@Path(MenuOptionDetail.Schema.id) long id);


    @POST("menu/option/category")
    Maybe<MenuOptionCategory> createMenuOptionCategory(final @Body MenuOptionCategory menuOptionCategory);

    @GET("menu/option/category")
    Maybe<List<MenuOptionCategory>> getMenuOptionCategoryListByStoreUuid(final @Query(MenuOptionCategory.Schema.storeUuid) String storeUuid);

    @PUT("menu/option/category/{" + MenuOptionCategory.Schema.uuid + "}")
    Maybe<MenuOptionCategory> updateMenuOptionCategory(final @Path(MenuOptionCategory.Schema.uuid) String menuOptionCategoryUuid,
                                                       final @Body MenuOptionCategory menuOptionCategory);

    @PUT("menu/option/category")
    Maybe<List<MenuOptionCategory>> updateMenuOptionCategories(final @Body List<MenuOptionCategory> menuOptionCategoryList);

    @DELETE("menu/option/category/{" + MenuOptionCategory.Schema.uuid + "}")
    Maybe<MenuOptionCategory> deleteMenuOptionCategory(final @Path(MenuOptionCategory.Schema.uuid) String menuOptionCategoryUuid);



    @GET("category")
    Maybe<List<MenuCategory>> getMenuCategoryListByStoreUuid(@Query(MenuCategory.Schema.storeUuid) String storeUuid);

    @DELETE("category/{" + MenuCategory.Schema.uuid + "}")
    Maybe<MenuCategory> deleteCategory(@Path(MenuCategory.Schema.uuid) String categoryUuid);

    @PUT("category/{" + MenuCategory.Schema.uuid + "}")
    Maybe<MenuCategory> updateCategoriesOfMenu(@Path(MenuCategory.Schema.uuid) final String menuCategoryUuid,
                                               @Body final MenuCategory menuCategory);

    @PUT("category")
    Maybe<List<MenuCategory>> updateMenuCategoryList(@Body final List<MenuCategory> menuCategoryList);

    @GET("category/{" + MenuCategory.Schema.uuid + "}")
    Maybe<MenuCategory> getMenuCategory(@Path(MenuCategory.Schema.uuid) final String menuCategoryUuid);

    @POST("category")
    Maybe<MenuCategory> createMenuCategory(@Body MenuCategory menuCategory);



    @POST("order")
    Maybe<MenuOrder> createOrder(@Body List<OrderDetail> orderDetailCollection);

    @GET("order/{" + MenuOrder.Schema.uuid + "}")
    Maybe<MenuOrder> getMenuOrder(@Path(MenuOrder.Schema.uuid) String uuid);

    @GET("order")
    Maybe<List<MenuOrder>> getMenuOrderListByCustomerUuid(@Query(MenuOrder.Schema.customerUuid) String customerUuid,
                                                          @Query("offset") int offset,
                                                          @Query("limit") int limit);
    @GET("order")
    Maybe<List<MenuOrder>> getMenuOrderListByStoreUuid(@Query(MenuOrder.Schema.storeUuid) String customerUuid,
                                                       @Query("offset") int offset,
                                                       @Query("limit") int limit);

    @PUT("order/{" + MenuOrder.Schema.uuid + "}/state")
    Maybe<MenuOrder> updateMenuOrderState(@Path(MenuOrder.Schema.uuid) String uuid,
                                          @Body MenuOrder menuOrder);


    @GET("order/detail")
    Maybe<List<OrderDetail>> getOrderDetailList(@Query(OrderDetail.Schema.menuOrderUuid) String menuOrderUuid);


    @POST("token")
    Maybe<SnsToken> createSnsToken(@Body SnsToken snsToken);
}