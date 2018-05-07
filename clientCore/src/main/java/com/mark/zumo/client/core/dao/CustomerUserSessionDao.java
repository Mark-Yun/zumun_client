package com.mark.zumo.client.core.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.mark.zumo.client.core.entity.session.CustomerUserSession;

import java.util.List;

import io.reactivex.Flowable;

/**
 * Created by mark on 18. 4. 30.
 */

@Dao
public interface CustomerUserSessionDao {
    String TABLE_NAME = "customer_user_session";

    @Query("SELECT * FROM " + TABLE_NAME)
    Flowable<List<CustomerUserSession>> getAll();

    @Query("SELECT * FROM " + TABLE_NAME + " WHERE id LIKE :id LIMIT 1")
    Flowable<CustomerUserSession> findById(long id);

    @Insert
    void insertAll(CustomerUserSession... users);

    @Update
    void update(CustomerUserSession user);

    @Delete
    void delete(CustomerUserSession user);
}
