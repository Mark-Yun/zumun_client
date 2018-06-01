/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.core.entity.MenuOption;
import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.entity.OrderDetail;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.entity.user.GuestUser;

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
    void insert(Store store);


    @Query("SELECT * FROM " + OrderDetail.TABLE + " WHERE order_detail_uuid LIKE :uuid LIMIT 1")
    Maybe<OrderDetail> getOrderDetail(String uuid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrderDetailList(List<OrderDetail> orderDetailList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(OrderDetail orderDetailList);


    @Query("SELECT * FROM " + MenuOrder.TABLE + " WHERE menu_order_uuid LIKE :menuOrderUuid LIMIT 1")
    Maybe<MenuOrder> getMenuOrder(String menuOrderUuid);

    @Insert
    void insertMenuOrderList(List<MenuOrder> userList);


    @Query("SELECT * FROM " + MenuOption.TABLE + " WHERE menu_uuid LIKE :menuUuid LIMIT 1")
    Maybe<List<MenuOption>> getMenuOptionList(String menuUuid);

    @Query("SELECT * FROM " + MenuOption.TABLE + " WHERE menu_option_uuid LIKE :menuOptionUuid LIMIT 1")
    Maybe<MenuOption> getMenuOption(String menuOptionUuid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMenuOptionList(List<MenuOption> menuOptions);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MenuOption menuOption);


    @Query("SELECT * FROM " + Menu.MENU_TABLE + " WHERE menu_uuid LIKE :uuid LIMIT 1")
    Maybe<Menu> getMenu(String uuid);

    @Query("SELECT * FROM " + Menu.MENU_TABLE + " WHERE store_uuid LIKE :storeUuid")
    Maybe<List<Menu>> getMenuList(String storeUuid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMenuList(List<Menu> menus);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Menu menu);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MenuOrder menuOrder);
}
