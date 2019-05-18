package com.mark.zumo.client.core.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.entity.OrderDetail;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;

/**
 * Created by mark on 19. 5. 18.
 */
@Dao
public interface MenuOrderDao {

    @Query("SELECT * FROM " + OrderDetail.TABLE +
            " WHERE order_detail_uuid" +
            " LIKE :uuid" +
            " LIMIT 1")
    Maybe<OrderDetail> getOrderDetail(String uuid);

    @Query("SELECT * FROM " + OrderDetail.TABLE +
            " WHERE menu_order_uuid" +
            " LIKE :menuOrderUuid")
    Maybe<List<OrderDetail>> getOrderDetailListByMenuOrderUuid(String menuOrderUuid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrderDetailList(List<OrderDetail> orderDetailList);


    @Query("SELECT * FROM " + MenuOrder.TABLE + " WHERE menu_order_uuid LIKE :menuOrderUuid LIMIT 1")
    Maybe<MenuOrder> getMenuOrderMaybe(String menuOrderUuid);

    @Query("SELECT * FROM " + MenuOrder.TABLE +
            " WHERE menu_order_uuid" +
            " LIKE :menuOrderUuid" +
            " LIMIT 1")
    Flowable<MenuOrder> getMenuOrderFlowable(String menuOrderUuid);

    @Query("SELECT * FROM " + MenuOrder.TABLE +
            " WHERE customer_uuid LIKE :customerUuid" +
            " ORDER BY created_date DESC" +
            " LIMIT :offset, :limit")
    Maybe<List<MenuOrder>> getMenuOrderByCustomerUuid(String customerUuid,
                                                      int offset,
                                                      int limit);

    @Query("SELECT * FROM " + MenuOrder.TABLE +
            " WHERE store_uuid LIKE :storeUuid" +
            " ORDER BY created_date DESC" +
            " LIMIT :offset, :limit")
    Maybe<List<MenuOrder>> getMenuOrderByStoreUuid(String storeUuid,
                                                   int offset,
                                                   int limit);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMenuOrderList(List<MenuOrder> menuOrderList);

    @Query("DELETE FROM " + MenuOrder.TABLE + " WHERE customer_uuid LIKE :customerUuid")
    void deleteMenuOrderListByCustomerUuid(String customerUuid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMenuOrder(MenuOrder menuOrder);
}
