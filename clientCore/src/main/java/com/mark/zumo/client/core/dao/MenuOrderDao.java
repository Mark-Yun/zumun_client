package com.mark.zumo.client.core.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.mark.zumo.client.core.entity.MenuOrder;

import java.util.List;

import io.reactivex.Flowable;

/**
 * Created by mark on 18. 4. 30.
 */

@Dao
public interface MenuOrderDao {
    String TABLE_NAME = "menu_order";

    @Query("SELECT * FROM " + TABLE_NAME)
    Flowable<List<MenuOrder>> getAll();

    @Query("SELECT * FROM " + TABLE_NAME + " WHERE id LIKE :id LIMIT 1")
    Flowable<MenuOrder> findById(long id);

    @Insert
    void insertAll(MenuOrder... users);

    @Update
    void update(MenuOrder user);

    @Delete
    void delete(MenuOrder user);
}
