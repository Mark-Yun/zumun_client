package com.mark.zumo.client.core.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.mark.zumo.client.core.entity.user.User;

import java.util.List;

/**
 * Created by mark on 18. 4. 30.
 */

@Dao
public interface UserDao {
    @Query("SELECT * FROM user")
    List<User> getAll();

    @Query("SELECT * FROM user WHERE id IN (:userIds)")
    List<User> getAllByIds(int[] userIds);

    @Query("SELECT * FROM user WHERE name LIKE :name LIMIT 1")
    User findByName(String name);

    @Query("SELECT * FROM user WHERE id LIKE :id LIMIT 1")
    User findById(long id);

    @Insert
    void insertAll(User... users);

    @Delete
    void delete(User user);
}
