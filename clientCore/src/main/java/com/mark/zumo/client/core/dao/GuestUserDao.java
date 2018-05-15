package com.mark.zumo.client.core.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.mark.zumo.client.core.entity.user.GuestUser;

import java.util.List;

import io.reactivex.Maybe;

/**
 * Created by mark on 18. 4. 30.
 */

@Dao
public interface GuestUserDao {
    String TABLE_NAME = "guest_user";

    @Query("SELECT * FROM " + TABLE_NAME)
    Maybe<List<GuestUser>> getAll();

    @Query("SELECT * FROM " + TABLE_NAME + " WHERE uuid LIKE :uuid LIMIT 1")
    Maybe<GuestUser> findByUuid(String uuid);

    @Insert
    void insertAll(GuestUser... users);

    @Update
    void update(GuestUser user);

    @Delete
    void delete(GuestUser user);
}
