/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.mark.zumo.client.core.database.entity.util.EntityComparator;
import com.mark.zumo.client.core.database.entity.util.EntityHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.mark.zumo.client.core.database.entity.OrderDetail.TABLE;

/**
 * Created by mark on 18. 5. 22.
 */
@Entity(tableName = TABLE)
public class OrderDetail implements Serializable {
    public static final String TABLE = "order_detail";

    @PrimaryKey @NonNull @SerializedName(Schema.uuid) @ColumnInfo(name = Schema.uuid)
    public final String uuid;
    @SerializedName(Schema.storeUuid) @ColumnInfo(name = Schema.storeUuid)
    public final String storeUuid;
    @SerializedName(Schema.menuUuid) @ColumnInfo(name = Schema.menuUuid)
    public final String menuUuid;
    @SerializedName(Schema.menuName) @ColumnInfo(name = Schema.menuName)
    public final String menuName;
    @SerializedName(Schema.menuOrderUuid) @ColumnInfo(name = Schema.menuOrderUuid)
    public final String menuOrderUuid;
    @SerializedName(Schema.menuOptionUuidList) @ColumnInfo(name = Schema.menuOptionUuidList)
    public final List<String> menuOptionUuidList;
    @SerializedName(Schema.quantity) @ColumnInfo(name = Schema.quantity)
    public final int quantity;
    @SerializedName(Schema.price) @ColumnInfo(name = Schema.price)
    public final int price;
    @SerializedName(Schema.menuOrderName) @ColumnInfo(name = Schema.menuOrderName)
    public String menuOrderName;

    public OrderDetail(@NonNull final String uuid, final String storeUuid, final String menuUuid,
                       final String menuName, final String menuOrderUuid,
                       final List<String> menuOptionUuidList, final int quantity, final int price) {

        this.uuid = uuid;
        this.storeUuid = storeUuid;
        this.menuUuid = menuUuid;
        this.menuName = menuName;
        this.menuOrderUuid = menuOrderUuid;
        this.menuOptionUuidList = menuOptionUuidList;
        this.quantity = quantity;
        this.price = price;
        this.menuOrderName = menuName;
    }

    public static OrderDetail create(@NonNull final String uuid, final String storeUuid, final String menuName,
                                     final List<String> menuOptionUuidList, final int quantity, final int price) {
        return new OrderDetail("", storeUuid, uuid, menuName, "", menuOptionUuidList, quantity, price);
    }

    public static OrderDetail fromMenu(Menu menu) {
        return new OrderDetail("",
                menu.storeUuid,
                menu.uuid,
                menu.name,
                "",
                new ArrayList<>(),
                1,
                menu.price
        );
    }

    @Override
    public String toString() {
        return EntityHelper.toString(this);
    }

    public boolean isSameMenu(@NonNull OrderDetail orderDetail) {
        return this.storeUuid.equals(orderDetail.storeUuid) &&
                this.menuUuid.equals(orderDetail.menuUuid) &&
                this.menuOptionUuidList.equals(orderDetail.menuOptionUuidList);
    }

    @Override
    public boolean equals(final Object obj) {
        return new EntityComparator<>().test(this, obj);
    }

    public interface Schema {
        String uuid = "order_detail_uuid";
        String menuUuid = "menu_uuid";
        String menuName = "menu_name";
        String menuOptionUuidList = "menu_option_uuid_list";
        String menuOrderUuid = "menu_order_uuid";
        String menuOrderName = "menu_order_name";
        String storeUuid = "store_uuid";
        String quantity = "ordered_quantity";
        String price = "ordered_price";
    }
}
