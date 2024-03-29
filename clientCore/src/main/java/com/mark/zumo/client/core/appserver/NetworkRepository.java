/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.appserver;

import com.mark.zumo.client.core.appserver.request.bank.DepositRequest;
import com.mark.zumo.client.core.appserver.request.crypto.CryptoRequest;
import com.mark.zumo.client.core.appserver.request.login.StoreUserSignInRequest;
import com.mark.zumo.client.core.appserver.request.registration.StoreRegistrationRequest;
import com.mark.zumo.client.core.appserver.request.registration.result.StoreRegistrationResult;
import com.mark.zumo.client.core.appserver.request.signup.StoreOwnerSignUpRequest;
import com.mark.zumo.client.core.appserver.response.DepositResponse;
import com.mark.zumo.client.core.appserver.response.InquiryAccountResponse;
import com.mark.zumo.client.core.appserver.response.StoreUserHandShakeResponse;
import com.mark.zumo.client.core.appserver.response.crypto.CryptoResponse;
import com.mark.zumo.client.core.appserver.response.store.registration.StoreRegistrationResponse;
import com.mark.zumo.client.core.appserver.response.store.user.signin.StoreUserSignInResponse;
import com.mark.zumo.client.core.appserver.response.store.user.signup.StoreOwnerSignUpResponse;
import com.mark.zumo.client.core.database.entity.Menu;
import com.mark.zumo.client.core.database.entity.MenuCategory;
import com.mark.zumo.client.core.database.entity.MenuDetail;
import com.mark.zumo.client.core.database.entity.MenuOption;
import com.mark.zumo.client.core.database.entity.MenuOptionCategory;
import com.mark.zumo.client.core.database.entity.MenuOptionDetail;
import com.mark.zumo.client.core.database.entity.MenuOrder;
import com.mark.zumo.client.core.database.entity.OrderDetail;
import com.mark.zumo.client.core.database.entity.SnsToken;
import com.mark.zumo.client.core.database.entity.Store;
import com.mark.zumo.client.core.database.entity.user.GuestUser;
import com.mark.zumo.client.core.database.entity.user.store.StoreOwner;
import com.mark.zumo.client.core.database.entity.user.store.StoreUser;
import com.mark.zumo.client.core.database.entity.user.store.StoreUserContract;

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

    @GET("users/store/handshake")
    Maybe<StoreUserHandShakeResponse> signInHandShake(@Query(StoreUser.Schema.email) String storeUserEmail);


    @POST("users/store")
    Maybe<StoreOwnerSignUpResponse> createStoreOwner(@Body StoreOwnerSignUpRequest request);

    @GET("users/store/{" + StoreOwner.Schema.uuid + "}")
    Maybe<StoreOwner> getStoreOwner(@Path(StoreOwner.Schema.uuid) String storeUserUuid);

    @PUT("users/store/{" + StoreOwner.Schema.uuid + "}")
    Maybe<StoreOwner> updateStoreOwner(@Path(StoreOwner.Schema.uuid) final String storeUserUuid,
                                       @Body final StoreOwner storeOwner);


    @POST("login/store")
    Maybe<StoreUserSignInResponse> storeUserLogin(@Body StoreUserSignInRequest request);


    @PUT("store/{" + Store.Schema.uuid + "}")
    Maybe<Store> updateStore(@Path(Store.Schema.uuid) String storeUuid,
                             @Body Store store);

    @GET("store/{" + Store.Schema.uuid + "}")
    Maybe<Store> getStore(@Path(Store.Schema.uuid) String storeUuid);

    @GET("store")
    Maybe<List<Store>> getNearByStore(@Query(Store.Schema.latitude) final double latitude,
                                      @Query(Store.Schema.longitude) final double longitude,
                                      @Query("distance") final double distanceKm);


    @POST("store/registration/request")
    Maybe<StoreRegistrationResponse> createStoreRegistrationRequest(@Body StoreRegistrationRequest storeRegistrationRequest);

    @GET("store/registration/request")
    Maybe<List<StoreRegistrationRequest>> getStoreRegistrationRequestByStoreUserUuid(@Query(StoreRegistrationRequest.Schema.storeUserUuid) String storeUserUuid);

    @GET("store/registration/request")
    Maybe<List<StoreRegistrationRequest>> getStoreRegistrationRequestAll(@Query("limit") int limit);

    @GET("store/registration/result")
    Maybe<List<StoreRegistrationResult>> getStoreRegistrationResultByStoreUserUuid(@Query(StoreUser.Schema.uuid) String storeUserUuid);

    @POST("store/registration/approve")
    Maybe<StoreRegistrationResult> approveStoreRegistration(@Body StoreRegistrationRequest storeRegistrationRequest);

    @POST("store/registration/reject")
    Maybe<StoreRegistrationResult> rejectStoreRegistration(@Body StoreRegistrationRequest storeRegistrationRequest);

    @GET("store/registration/contract")
    Maybe<List<StoreUserContract>> getStoreUserContractListByStoreUserUuid(@Query(StoreUser.Schema.uuid) String storeUserUuid);


    @POST("menu")
    Maybe<Menu> createMenu(@Body Menu menu);

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
    Maybe<List<MenuDetail>> updateCategoriesOfMenu(@Query(MenuDetail.Schema.menuUuid) String menuUuid,
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
    Maybe<List<MenuOptionDetail>> getMenuOptionDetailListByStoreUuid(@Query(MenuOptionDetail.Schema.storeUuid) String storeUuid);

    @GET("menu/option/detail")
    Maybe<List<MenuOptionDetail>> getMenuOptionDetailListByMenuUuid(@Query(MenuOptionDetail.Schema.menuUuid) String menuUuid);

    @PUT("menu/option/detail")
    Maybe<List<MenuOptionDetail>> updateMenuOptionDetailList(@Body List<MenuOptionDetail> menuOptionDetailList);

    @PUT("menu/option/detail/")
    Maybe<List<MenuOptionDetail>> updateMenuOptionCategoriesOfMenu(@Query(MenuOptionDetail.Schema.menuUuid) String menuUuid,
                                                                   @Body List<MenuOptionDetail> menuOptionDetailList);

    @DELETE("menu/option/detail/{" + MenuOptionDetail.Schema.uuid + "}")
    Maybe<MenuOptionDetail> deleteMenuOptionDetail(@Path(MenuOptionDetail.Schema.uuid) String menuOptionDetailUuid);


    @POST("menu/option/category")
    Maybe<MenuOptionCategory> createMenuOptionCategory(final @Body MenuOptionCategory menuOptionCategory);

    @GET("menu/option/category")
    Maybe<List<MenuOptionCategory>> getMenuOptionCategoryListByStoreUuid(final @Query(MenuOptionCategory.Schema.storeUuid) String storeUuid);

    @GET("menu/option/category/{" + MenuOptionCategory.Schema.uuid + "}")
    Maybe<MenuOptionCategory> getMenuOptionCategory(final @Path(MenuOptionCategory.Schema.uuid) String menuOptionCategoryUuid);

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
    Maybe<MenuCategory> updateMenuCategory(@Path(MenuCategory.Schema.uuid) final String menuCategoryUuid,
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


    @POST("bank/transfer/deposit")
    Maybe<DepositResponse> depsit(DepositRequest depositRequest);

    @POST("bank/inquiry/account/name")
    Maybe<CryptoResponse<InquiryAccountResponse>> inquiryBankAccount(@Body CryptoRequest cryptoRequest);
}