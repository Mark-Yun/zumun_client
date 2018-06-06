/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.mark.zumo.client.core.entity.util.EntityHelper;

import java.io.Serializable;

import static com.mark.zumo.client.core.entity.MenuOrder.TABLE;

/**
 * Created by mark on 18. 4. 30.
 */

@Entity(tableName = TABLE)
public class MenuOrder implements Serializable {
    public static final String TABLE = "menu_order";

    @PrimaryKey @NonNull @ColumnInfo(name = Schema.uuid) @SerializedName(Schema.uuid)
    public final String uuid;
    @ColumnInfo(name = Schema.name) @SerializedName(Schema.name)
    public final String name;
    @ColumnInfo(name = Schema.customerUuid) @SerializedName(Schema.customerUuid)
    public final String customerUuid;
    @ColumnInfo(name = Schema.storeUuid) @SerializedName(Schema.storeUuid)
    public final String storeUuid;
    @ColumnInfo(name = Schema.orderNumber) @SerializedName(Schema.orderNumber)
    public final String orderNumber;
    @ColumnInfo(name = Schema.createdDate) @SerializedName(Schema.createdDate)
    public final String createdDate;
    @ColumnInfo(name = Schema.totalQuantity) @SerializedName(Schema.totalQuantity)
    public final int totalQuantity;
    @ColumnInfo(name = Schema.totalPrice) @SerializedName(Schema.totalPrice)
    public final int totalPrice;

    public MenuOrder(@NonNull final String uuid, final String name, final String customerUuid, final String storeUuid, final String orderNumber, final String createdDate, final int totalQuantity, final int totalPrice) {
        this.uuid = uuid;
        this.name = name;
        this.customerUuid = customerUuid;
        this.storeUuid = storeUuid;
        this.orderNumber = orderNumber;
        this.createdDate = createdDate;
        this.totalQuantity = totalQuantity;
        this.totalPrice = totalPrice;
    }

    @Override
    public String toString() {
        return EntityHelper.toString(this, this.getClass());
    }

    private interface Schema {
        String uuid = "menu_order_uuid";
        String name = "menu_order_name";
        String customerUuid = "customer_uuid";
        String storeUuid = "store_uuid";
        String orderNumber = "order_number";
        String createdDate = "create_date";
        String totalQuantity = "total_quantity";
        String totalPrice = "total_price";
    }
}
