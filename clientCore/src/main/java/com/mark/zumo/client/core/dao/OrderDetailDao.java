package com.mark.zumo.client.core.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.mark.zumo.client.core.entity.OrderDetail;

import io.reactivex.Maybe;

/**
 * Created by mark on 18. 5. 23.
 */
@Dao
public interface OrderDetailDao {

    String TABLE_NAME = "order_detail";

    @Query("SELECT * FROM " + TABLE_NAME + " WHERE order_detail_uuid LIKE :uuid LIMIT 1")
    Maybe<OrderDetail> findByUuid(String uuid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(OrderDetail... orderDetails);
}
