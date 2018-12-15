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
import android.arch.persistence.room.RoomWarnings;

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
import com.mark.zumo.client.core.entity.user.store.StoreOwner;
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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertStoreOwner(StoreOwner storeOwner);

    @Query("SELECT * FROM " + Store.TABLE + " WHERE store_uuid LIKE :uuid LIMIT 1")
    Maybe<Store> getStore(String uuid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertStore(Store store);


    @Query("SELECT * FROM " + OrderDetail.TABLE + " WHERE order_detail_uuid LIKE :uuid LIMIT 1")
    Maybe<OrderDetail> getOrderDetail(String uuid);

    @Query("SELECT * FROM " + OrderDetail.TABLE + " WHERE menu_order_uuid LIKE :menuOrderUuid ")
    Maybe<List<OrderDetail>> getOrderDetailListByMenuOrderUuid(String menuOrderUuid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrderDetailList(List<OrderDetail> orderDetailList);


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

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT * FROM " + MenuOption.Schema.table + " WHERE menu_option_category_uuid LIKE :menuOptionCategoryUuid")
    Maybe<List<MenuOption>> getMenuOptionListByMenuOptionCategoryUuid(String menuOptionCategoryUuid);

    @Query("SELECT * FROM " + MenuOption.Schema.table + " WHERE store_uuid LIKE :storeUuid")
    Maybe<List<MenuOption>> getMenuOptionListByStoreUuid(String storeUuid);

    @Query("SELECT * FROM " + MenuOptionDetail.Schema.table + " WHERE store_uuid LIKE :storeUuid")
    Maybe<List<MenuOptionDetail>> getMenuOptionDetailListByStoreUuid(String storeUuid);

    @Query("SELECT * FROM " + MenuOptionDetail.Schema.table + " WHERE menu_uuid LIKE :menuUuid")
    Maybe<List<MenuOptionDetail>> getMenuOptionDetailListByMenuUuid(String menuUuid);

    @Query("SELECT * FROM " + MenuOptionDetail.Schema.table +
            " WHERE menu_option_category_uuid LIKE :menuOptionCategoryUuid" +
            " AND menu_uuid LIKE :menuUuid")
    Maybe<MenuOptionDetail> getMenuOptionDetail(String menuOptionCategoryUuid, String menuUuid);

    @Query("SELECT * FROM " + MenuOptionDetail.Schema.table +
            " WHERE menu_option_detail_uuid LIKE :menuOptionDetailUuid")
    Maybe<MenuOptionDetail> getMenuOptionDetail(String menuOptionDetailUuid);

    @Query("DELETE FROM " + MenuOptionDetail.Schema.table + " WHERE menu_option_category_uuid LIKE :menuOptionCategoryUuid")
    void deleteMenuOptionDetailOfMenuOptionCategory(String menuOptionCategoryUuid);

    @Query("DELETE FROM " + MenuOptionDetail.Schema.table + " WHERE menu_uuid LIKE :menuUuid")
    void deleteMenuOptionDetailByMenuUuid(String menuUuid);

    @Query("DELETE FROM " + MenuOptionDetail.Schema.table + " WHERE store_uuid LIKE :storeUuid")
    void deleteMenuOptionDetailOfStore(String storeUuid);

    @Query("SELECT * FROM " + MenuOption.Schema.table + " WHERE menu_option_uuid LIKE :menuOptionUuid LIMIT 1")
    Maybe<MenuOption> getMenuOption(String menuOptionUuid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMenuOptionList(List<MenuOption> menuOptions);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMenuOptionCategoryList(List<MenuOptionCategory> menuOptionCategoryList);

    @Query("DELETE FROM " + MenuOptionCategory.Schema.table + " WHERE store_uuid LIKE :storeUuid")
    void deleteMenuOptionCategoryListByStoreUuid(String storeUuid);

    @Delete
    void deleteMenuOptionCategoryList(List<MenuOptionCategory> menuOptionCategoryList);

    @Delete
    void deleteMenuOptionList(List<MenuOption> menuOptionList);

    @Delete
    void deleteMenuOptionDetail(MenuOptionDetail menuOptionDetail);

    @Query("SELECT * FROM " + MenuOptionCategory.Schema.table + " WHERE store_uuid LIKE :storeUuid")
    Maybe<List<MenuOptionCategory>> getMenuOptionCategoryListByStoreUuid(String storeUuid);

    @Query("SELECT * FROM " + MenuOptionCategory.Schema.table + " WHERE menu_option_category_uuid LIKE :menuOptionCategoryUuid")
    Maybe<MenuOptionCategory> getMenuOptionCategory(String menuOptionCategoryUuid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMenuOptionDetailList(List<MenuOptionDetail> menuOptionDetails);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMenuOption(MenuOption menuOption);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMenuOptionCategory(MenuOptionCategory menuOptionCategory);


    @Query("SELECT * FROM " + Menu.MENU_TABLE + " WHERE menu_uuid LIKE :uuid LIMIT 1")
    Maybe<Menu> getMenu(String uuid);

    @Query("SELECT * FROM " + Menu.MENU_TABLE + " WHERE store_uuid LIKE :storeUuid")
    Maybe<List<Menu>> getMenuList(String storeUuid);

    @Query("DELETE FROM " + Menu.MENU_TABLE + " WHERE store_uuid LIKE :storeUuid")
    void deleteMenuOfStore(String storeUuid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMenuList(List<Menu> menus);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMenu(Menu menu);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMenuOrder(MenuOrder menuOrder);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMenuCategory(MenuCategory menuCategory);

    @Query("DELETE FROM " + MenuCategory.Schema.table + " WHERE store_uuid LIKE :storeUuid")
    void deleteCategoriesOfStore(String storeUuid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMenuCategoryList(List<MenuCategory> menuCategoryList);

    @Query("SELECT * FROM " + MenuCategory.Schema.table + " WHERE store_uuid LIKE :storeUuid")
    Maybe<List<MenuCategory>> getMenuCategoryList(String storeUuid);

    @Query("SELECT * FROM " + MenuCategory.Schema.table + " WHERE menu_category_uuid LIKE :menu_category_uuid LIMIT 1")
    Maybe<MenuCategory> getMenuCategory(String menu_category_uuid);

    @Delete
    void deleteMenuCategory(MenuCategory menuCategory);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPaymentToken(PaymentToken paymentToken);

    @Query("SELECT * FROM " + PaymentToken.Schema.table + " WHERE menu_order_uuid LIKE :menuOrderUuid LIMIT 1")
    Maybe<PaymentToken> getPaymentToken(String menuOrderUuid);

    @Delete
    void removePaymentToken(PaymentToken paymentToken);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSnsToken(SnsToken snsToken);

    @Query("SELECT * FROM " + SnsToken.Schema.table + " ORDER BY " + SnsToken.Schema.createdDate + " DESC LIMIT 1")
    Maybe<SnsToken> getLatestSnsToken();


    @Query("SELECT * FROM " + MenuDetail.Schema.TABLE + " WHERE store_uuid LIKE :storeUuid")
    Maybe<List<MenuDetail>> getMenuDetailByStoreUuid(String storeUuid);

    @Query("SELECT * FROM " + MenuDetail.Schema.TABLE + " WHERE menu_category_uuid LIKE :categoryUuid")
    Maybe<List<MenuDetail>> getMenuDetailByCategoryUuid(String categoryUuid);

    @Query("SELECT * FROM " + MenuDetail.Schema.TABLE + " WHERE menu_detail_uuid LIKE :menuDetailUuid")
    Maybe<MenuDetail> getMenuDetail(String menuDetailUuid);

    @Query("DELETE FROM " + MenuDetail.Schema.TABLE + " WHERE menu_category_uuid LIKE :categoryUuid")
    void deleteMenuDetailListByCategoryUuid(String categoryUuid);

    @Delete
    void deleteMenuDetailList(List<MenuDetail> menuDetailList);

    @Query("SELECT * FROM " + MenuDetail.Schema.TABLE +
            " WHERE store_uuid LIKE :storeUuid" +
            " AND menu_uuid LIKE :menuUuid")
    Maybe<List<MenuDetail>> getMenuDetailByStringMenuUuid(String storeUuid, String menuUuid);

    @Query("SELECT * FROM " + MenuDetail.Schema.TABLE +
            " WHERE menu_category_uuid LIKE :categoryUuid" +
            " AND menu_uuid LIKE :menuUuid LIMIT 1")
    Maybe<MenuDetail> getMenuDetailByCategoryUuidAndMenuUuid(String categoryUuid, String menuUuid);

    @Query("DELETE FROM " + MenuDetail.Schema.TABLE + " WHERE menu_uuid LIKE :menuUuid")
    void deleteMenuDetailListByMenuUuid(String menuUuid);

    @Query("DELETE FROM " + MenuDetail.Schema.TABLE + " WHERE store_uuid LIKE :storeUuid")
    void deleteMenuDetailListByStoreUuid(String storeUuid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMenuDetailList(List<MenuDetail> menuDetailList);
}
