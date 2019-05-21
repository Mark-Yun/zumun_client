package com.mark.zumo.client.core.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RoomWarnings;

import com.mark.zumo.client.core.database.entity.MenuOption;
import com.mark.zumo.client.core.database.entity.MenuOptionDetail;

import java.util.List;

import io.reactivex.Maybe;

/**
 * Created by mark on 19. 5. 18.
 */
@Dao
public interface MenuOptionDao {

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT * FROM " + MenuOption.Schema.table +
            " WHERE menu_option_category_uuid" +
            " LIKE :menuOptionCategoryUuid")
    Maybe<List<MenuOption>> getMenuOptionListByMenuOptionCategoryUuid(String menuOptionCategoryUuid);

    @Query("SELECT * FROM " + MenuOption.Schema.table +
            " WHERE store_uuid" +
            " LIKE :storeUuid")
    Maybe<List<MenuOption>> getMenuOptionListByStoreUuid(String storeUuid);

    @Query("SELECT * FROM " + MenuOptionDetail.Schema.table +
            " WHERE store_uuid" +
            " LIKE :storeUuid")
    Maybe<List<MenuOptionDetail>> getMenuOptionDetailListByStoreUuid(String storeUuid);

    @Query("SELECT * FROM " + MenuOptionDetail.Schema.table +
            " WHERE menu_uuid" +
            " LIKE :menuUuid")
    Maybe<List<MenuOptionDetail>> getMenuOptionDetailListByMenuUuid(String menuUuid);

    @Query("SELECT * FROM " + MenuOptionDetail.Schema.table +
            " WHERE menu_option_category_uuid" +
            " LIKE :menuOptionCategoryUuid" +
            " AND menu_uuid" +
            " LIKE :menuUuid")
    Maybe<MenuOptionDetail> getMenuOptionDetail(String menuOptionCategoryUuid, String menuUuid);

    @Query("SELECT * FROM " + MenuOptionDetail.Schema.table +
            " WHERE menu_option_detail_uuid" +
            " LIKE :menuOptionDetailUuid")
    Maybe<MenuOptionDetail> getMenuOptionDetail(String menuOptionDetailUuid);

    @Query("DELETE FROM " + MenuOptionDetail.Schema.table +
            " WHERE menu_option_category_uuid" +
            " LIKE :menuOptionCategoryUuid")
    void deleteMenuOptionDetailOfMenuOptionCategory(String menuOptionCategoryUuid);

    @Query("DELETE FROM " + MenuOptionDetail.Schema.table +
            " WHERE menu_uuid" +
            " LIKE :menuUuid")
    void deleteMenuOptionDetailByMenuUuid(String menuUuid);

    @Query("DELETE FROM " + MenuOptionDetail.Schema.table +
            " WHERE store_uuid" +
            " LIKE :storeUuid")
    void deleteMenuOptionDetailOfStore(String storeUuid);

    @Query("DELETE FROM " + MenuOptionDetail.Schema.table +
            " WHERE menu_uuid" +
            " LIKE :menuUuid")
    void deleteMenuOptionDetailOfMenu(String menuUuid);

}
