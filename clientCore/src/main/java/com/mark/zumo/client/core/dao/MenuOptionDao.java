package com.mark.zumo.client.core.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.mark.zumo.client.core.entity.MenuOption;

import java.util.List;

import io.reactivex.Maybe;

/**
 * Created by mark on 18. 5. 23.
 */
@Dao
public interface MenuOptionDao {
    String TABLE_NAME = "menu_option";

    @Query("SELECT * FROM " + TABLE_NAME + " WHERE menu_option_uuid LIKE :menuUuid LIMIT 1")
    Maybe<List<MenuOption>> findByMenuUuid(String menuUuid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<MenuOption> menuOptions);
}
