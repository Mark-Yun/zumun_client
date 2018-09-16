/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.core.entity.MenuCategory;
import com.mark.zumo.client.core.entity.MenuDetail;
import com.mark.zumo.client.core.entity.MenuOption;
import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.entity.OrderDetail;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.entity.user.GuestUser;
import com.mark.zumo.client.core.payment.kakao.entity.PaymentToken;

import java.util.List;

import io.reactivex.Maybe;

/**
 * Created by mark on 18. 4. 30.
 */

@Dao
public interface DiskRepository {
    @Query("SELECT * FROM " + GuestUser.TABLE + " WHERE uuid LIKE :uuid LIMIT 1")
    Maybe<GuestUser> getGuestUser(String uuid);


    @Query("SELECT * FROM " + Store.TABLE + " WHERE store_uuid LIKE :uuid LIMIT 1")
    Maybe<Store> getStore(String uuid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertStoreList(List<Store> stores);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertStore(Store store);


    @Query("SELECT * FROM " + OrderDetail.TABLE + " WHERE order_detail_uuid LIKE :uuid LIMIT 1")
    Maybe<OrderDetail> getOrderDetail(String uuid);

    @Query("SELECT * FROM " + OrderDetail.TABLE + " WHERE menu_order_uuid LIKE :menuOrderUuid ")
    Maybe<List<OrderDetail>> getOrderDetailListByMenuOrderUuid(String menuOrderUuid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrderDetailList(List<OrderDetail> orderDetailList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrderDetail(OrderDetail orderDetailList);


    @Query("SELECT * FROM " + MenuOrder.TABLE + " WHERE menu_order_uuid LIKE :menuOrderUuid LIMIT 1")
    Maybe<MenuOrder> getMenuOrder(String menuOrderUuid);

    @Query("SELECT * FROM " + MenuOrder.TABLE +
            " WHERE customer_uuid LIKE :customerUuid" +
            " ORDER BY created_date DESC" +
            " LIMIT :offset, :limit")
    Maybe<List<MenuOrder>> getMenuOrderByCustomerUuid(String customerUuid,
                                                      int offset,
                                                      int limit);

    @Query("SELECT * FROM " + MenuOrder.TABLE +
            " WHERE store_uuid LIKE :storeUuid" +
            " ORDER BY created_date DESC" +
            " LIMIT :offset, :limit")
    Maybe<List<MenuOrder>> getMenuOrderByStoreUuid(String storeUuid,
                                                   int offset,
                                                   int limit);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMenuOrderList(List<MenuOrder> menuOrderList);


    @Query("SELECT * FROM " + MenuOption.Schema.table + " WHERE menu_uuid LIKE :menuUuid")
    Maybe<List<MenuOption>> getMenuOptionListByMenuUuid(String menuUuid);

    @Query("SELECT * FROM " + MenuOption.Schema.table + " WHERE menu_option_uuid LIKE :menuOptionUuid LIMIT 1")
    Maybe<MenuOption> getMenuOption(String menuOptionUuid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMenuOptionList(List<MenuOption> menuOptions);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMenuOption(MenuOption menuOption);


    @Query("SELECT * FROM " + Menu.MENU_TABLE + " WHERE menu_uuid LIKE :uuid LIMIT 1")
    Maybe<Menu> getMenu(String uuid);

    @Query("SELECT * FROM " + Menu.MENU_TABLE + " WHERE store_uuid LIKE :storeUuid")
    Maybe<List<Menu>> getMenuList(String storeUuid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMenuList(List<Menu> menus);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMenu(Menu menu);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMenuOrder(MenuOrder menuOrder);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMenuCategory(MenuCategory menuCategory);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMenuCategoryList(List<MenuCategory> menuCategoryList);

    @Query("SELECT * FROM " + MenuCategory.Schema.table + " WHERE store_uuid LIKE :storeUuid")
    Maybe<List<MenuCategory>> getMenuCategoryList(String storeUuid);

    @Query("SELECT * FROM " + MenuCategory.Schema.table + " WHERE menu_category_uuid LIKE :menu_category_uuid LIMIT 1")
    Maybe<MenuCategory> getMenuCategory(String menu_category_uuid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPaymentToken(PaymentToken paymentToken);

    @Query("SELECT * FROM " + PaymentToken.Schema.table + " WHERE menu_order_uuid LIKE :menuOrderUuid LIMIT 1")
    Maybe<PaymentToken> getPaymentToken(String menuOrderUuid);

    @Delete
    void removePaymentToken(PaymentToken paymentToken);


    @Query("SELECT * FROM " + MenuDetail.Schema.TABLE + " WHERE store_uuid LIKE :storeUuid")
    Maybe<List<MenuDetail>> getMenuDetailByStoreUuid(String storeUuid);

    @Query("SELECT * FROM " + MenuDetail.Schema.TABLE + " WHERE menu_category_uuid LIKE :categoryUuid")
    Maybe<List<MenuDetail>> getMenuDetailByCategoryUuid(String categoryUuid);

    @Query("DELETE FROM " + MenuDetail.Schema.TABLE + " WHERE menu_category_uuid LIKE :categoryUuid")
    void deleteMenuDetailListByCategoryUuid(String categoryUuid);

    @Query("SELECT * FROM " + MenuDetail.Schema.TABLE +
            " WHERE store_uuid LIKE :storeUuid" +
            " AND menu_uuid LIKE :menuUuid")
    Maybe<List<MenuDetail>> getMenuDetailByStringMenuUuidFromDisk(String storeUuid, String menuUuid);

    @Query("DELETE FROM " + MenuDetail.Schema.TABLE + " WHERE menu_uuid LIKE :menuUuid")
    void deleteMenuDetailListByMenuUuid(String menuUuid);

    @Query("DELETE FROM " + MenuDetail.Schema.TABLE + " WHERE store_uuid LIKE :storeUuid")
    void deleteMenuDetailListByStoreUuid(String storeUuid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMenuDetailList(List<MenuDetail> menuDetailList);
}
