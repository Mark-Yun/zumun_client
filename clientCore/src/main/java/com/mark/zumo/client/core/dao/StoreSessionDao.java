package com.mark.zumo.client.core.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.mark.zumo.client.core.entity.session.StoreSession;

import java.util.List;

import io.reactivex.Flowable;

/**
 * Created by mark on 18. 4. 30.
 */

@Dao
public interface StoreSessionDao {
    String TABLE_NAME = "store_session";

    @Query("SELECT * FROM " + TABLE_NAME)
    Flowable<List<StoreSession>> getAll();

    @Query("SELECT * FROM " + TABLE_NAME + " WHERE id LIKE :id LIMIT 1")
    Flowable<StoreSession> findById(long id);

    @Insert
    void insertAll(StoreSession... users);

    @Update
    void update(StoreSession user);

    @Delete
    void delete(StoreSession user);
}
