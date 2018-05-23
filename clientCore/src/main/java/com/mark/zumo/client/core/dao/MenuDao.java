package com.mark.zumo.client.core.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.mark.zumo.client.core.entity.Menu;

import java.util.List;

import io.reactivex.Maybe;

/**
 * Created by mark on 18. 4. 30.
 */

@Dao
public interface MenuDao {
    String TABLE_NAME = "menu_item";

    @Query("SELECT * FROM " + TABLE_NAME)
    Maybe<List<Menu>> getAll();

    @Query("SELECT * FROM " + TABLE_NAME + " WHERE menu_uuid LIKE :uuid LIMIT 1")
    Maybe<Menu> findByUuid(String uuid);

    @Query("SELECT * FROM " + TABLE_NAME + " WHERE store_uuid LIKE :storeUuid")
    Maybe<List<Menu>> findByStoreUuid(String storeUuid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Menu... menus);

    @Update
    void update(Menu user);

    @Delete
    void delete(Menu user);
}
