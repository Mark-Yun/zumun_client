/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RoomWarnings;

import com.mark.zumo.client.core.appserver.request.registration.StoreRegistrationRequest;
import com.mark.zumo.client.core.appserver.request.registration.result.StoreRegistrationResult;
import com.mark.zumo.client.core.database.entity.MenuCategory;
import com.mark.zumo.client.core.database.entity.MenuDetail;
import com.mark.zumo.client.core.database.entity.MenuOption;
import com.mark.zumo.client.core.database.entity.MenuOptionCategory;
import com.mark.zumo.client.core.database.entity.MenuOptionDetail;
import com.mark.zumo.client.core.database.entity.SessionStore;
import com.mark.zumo.client.core.database.entity.SnsToken;
import com.mark.zumo.client.core.database.entity.user.GuestUser;
import com.mark.zumo.client.core.database.entity.user.store.StoreOwner;
import com.mark.zumo.client.core.database.entity.user.store.StoreUserContract;
import com.mark.zumo.client.core.database.entity.user.store.StoreUserSession;
import com.mark.zumo.client.core.payment.kakao.entity.PaymentToken;

import java.util.List;

import io.reactivex.Flowable;
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

    @Query("DELETE FROM " + MenuOptionDetail.Schema.table + " WHERE menu_uuid LIKE :menuUuid")
    void deleteMenuOptionDetailOfMenu(String menuUuid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertStoreUserSession(StoreUserSession storeUserSession);

    @Query("SELECT * FROM " + StoreUserSession.Schema.table + " ORDER BY " + StoreUserSession.Schema.createdDate + " DESC LIMIT 1")
    Flowable<StoreUserSession> getStoreUserSessionFlowable();

    @Query("SELECT * FROM " + StoreUserSession.Schema.table + " ORDER BY " + StoreUserSession.Schema.createdDate + " DESC LIMIT 1")
    Maybe<StoreUserSession> getStoreUserSessionMaybe();

    @Query("SELECT * FROM " + StoreOwner.Schema.TABLE + " WHERE " + StoreOwner.Schema.uuid + " LIKE :storeUserUuid")
    Maybe<StoreOwner> getStoreOwner(String storeUserUuid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertStoreUser(StoreOwner storeOwner);

    @Query("DELETE FROM " + StoreUserSession.Schema.table)
    void removeAllStoreUserSession();

    @Query("DELETE FROM " + SessionStore.Schema.TABLE)
    void removeAllStoreSession();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSessionStore(SessionStore sessionStore);

    @Query("SELECT * FROM " + SessionStore.Schema.TABLE + " ORDER BY " + StoreUserSession.Schema.createdDate + " DESC LIMIT 1")
    Maybe<SessionStore> getSessionStore();

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

    @Query("SELECT * FROM " + StoreUserContract.Schema.table + " WHERE store_user_uuid LIKE :storeUserUuid")
    Maybe<List<StoreUserContract>> getStoreUserContractListbyStoreUserUuid(String storeUserUuid);

    @Query("SELECT * FROM " + StoreRegistrationRequest.Schema.table + " WHERE store_user_uuid LIKE :storeUserUuid")
    Maybe<List<StoreRegistrationRequest>> getStoreRegistrationRequestListByStoreUserUuid(String storeUserUuid);

    @Query("SELECT * FROM " + StoreRegistrationRequest.Schema.table + " ORDER BY created_date LIMIT :limit")
    Maybe<List<StoreRegistrationRequest>> getStoreRegistrationRequestAll(int limit);

    @Query("SELECT * FROM " + StoreRegistrationRequest.Schema.table + " WHERE store_registration_request_uuid = :uuid")
    Maybe<StoreRegistrationRequest> getStoreRegistrationRequestByUuid(String uuid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertStoreRegistrationRequest(StoreRegistrationRequest storeRegistrationRequest);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertStoreRegistrationRequestList(List<StoreRegistrationRequest> storeRegistrationRequestList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertStoreRegistrationResult(StoreRegistrationResult storeRegistrationResult);

    @Query("SELECT * FROM " + StoreRegistrationResult.Schema.table + " WHERE store_user_uuid LIKE :storeUserUuid")
    Maybe<List<StoreRegistrationResult>> getStoreRegistrationResultByStoreUserUuid(String storeUserUuid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertStoreUserConractList(List<StoreUserContract> storeUserContractList);
}
