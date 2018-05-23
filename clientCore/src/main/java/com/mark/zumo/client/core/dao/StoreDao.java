package com.mark.zumo.client.core.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.mark.zumo.client.core.entity.Store;

import java.util.List;

import io.reactivex.Maybe;

/**
 * Created by mark on 18. 4. 30.
 */

@Dao
public interface StoreDao {
    String TABLE_NAME = "store";

    @Query("SELECT * FROM " + TABLE_NAME)
    Maybe<List<Store>> getAll();

    @Query("SELECT * FROM " + TABLE_NAME + " WHERE store_uuid LIKE :uuid LIMIT 1")
    Maybe<Store> findById(String uuid);

    @Insert
    void insertAll(Store... stores);

    @Insert
    void insert(Store store);

    @Update
    void update(Store store);

    @Delete
    void delete(Store store);
}
