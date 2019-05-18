package com.mark.zumo.client.core.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.mark.zumo.client.core.entity.Menu;

import java.util.List;

import io.reactivex.Maybe;

/**
 * Created by mark on 19. 5. 18.
 */
@Dao
public interface MenuDao {

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
}
