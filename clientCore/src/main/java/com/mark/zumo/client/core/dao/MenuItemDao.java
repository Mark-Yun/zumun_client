package com.mark.zumo.client.core.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.mark.zumo.client.core.entity.MenuItem;

import java.util.List;

import io.reactivex.Flowable;

/**
 * Created by mark on 18. 4. 30.
 */

@Dao
public interface MenuItemDao {
    String TABLE_NAME = "menu_item";

    @Query("SELECT * FROM " + TABLE_NAME)
    Flowable<List<MenuItem>> getAll();

    @Query("SELECT * FROM " + TABLE_NAME + " WHERE id LIKE :id LIMIT 1")
    Flowable<MenuItem> findById(long id);

    @Insert
    void insertAll(MenuItem... users);

    @Update
    void update(MenuItem user);

    @Delete
    void delete(MenuItem user);
}
