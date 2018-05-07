package com.mark.zumo.client.core.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.mark.zumo.client.core.entity.user.User;

import java.util.List;

import io.reactivex.Flowable;

/**
 * Created by mark on 18. 4. 30.
 */

@Dao
public interface UserDao {
    String TABLE_NAME = "user";

    @Query("SELECT * FROM " + TABLE_NAME)
    Flowable<List<User>> getAll();

    @Query("SELECT * FROM " + TABLE_NAME + " WHERE id LIKE :id LIMIT 1")
    Flowable<User> findById(long id);

    @Insert
    void insertAll(User... users);

    @Update
    void update(User user);

    @Delete
    void delete(User user);
}
