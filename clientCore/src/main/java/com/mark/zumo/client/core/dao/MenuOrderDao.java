package com.mark.zumo.client.core.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.mark.zumo.client.core.entity.MenuOrder;

import java.util.List;

import io.reactivex.Maybe;

/**
 * Created by mark on 18. 4. 30.
 */

@Dao
public interface MenuOrderDao {
    String TABLE_NAME = "menu_order";

    @Query("SELECT * FROM " + TABLE_NAME)
    Maybe<List<MenuOrder>> getAll();

    @Query("SELECT * FROM " + TABLE_NAME + " WHERE menu_order_uuid LIKE :uuid LIMIT 1")
    Maybe<MenuOrder> findByUuid(String uuid);

    @Insert
    void insertAll(MenuOrder... users);

    @Update
    void update(MenuOrder user);

    @Delete
    void delete(MenuOrder user);
}
