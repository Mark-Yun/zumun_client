package com.mark.zumo.client.core.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.mark.zumo.client.core.database.entity.Store;

import io.reactivex.Flowable;
import io.reactivex.Maybe;

/**
 * Created by mark on 19. 5. 18.
 */
@Dao
public interface StoreDao {

    @Query("SELECT * FROM " + Store.TABLE + " WHERE store_uuid LIKE :uuid LIMIT 1")
    Maybe<Store> getStoreMaybe(String uuid);

    @Query("SELECT * FROM " + Store.TABLE + " WHERE store_uuid LIKE :uuid LIMIT 1")
    Flowable<Store> getStoreFlowable(String uuid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertStore(Store store);
}
