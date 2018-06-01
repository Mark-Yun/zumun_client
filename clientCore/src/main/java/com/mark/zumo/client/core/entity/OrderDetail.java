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
import java.util.ArrayList;
import java.util.List;

import static com.mark.zumo.client.core.entity.OrderDetail.TABLE;

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
    @SerializedName(Schema.menuOrderUuid) @ColumnInfo(name = Schema.menuOrderUuid)
    public final String menuOrderUuid;
    @SerializedName(Schema.menuOptionUuidList) @ColumnInfo(name = Schema.menuOptionUuidList)
    public final List<String> menuOptionUuidList;
    @SerializedName(Schema.amount) @ColumnInfo(name = Schema.amount)
    public final int amount;

    public OrderDetail(@NonNull final String uuid, final String storeUuid, final String menuUuid, final String menuOrderUuid, final List<String> menuOptionUuidList, final int amount) {
        this.uuid = uuid;
        this.storeUuid = storeUuid;
        this.menuUuid = menuUuid;
        this.menuOrderUuid = menuOrderUuid;
        this.menuOptionUuidList = menuOptionUuidList;
        this.amount = amount;
    }

    public static OrderDetail fromMenu(Menu menu) {
        return new OrderDetail("", menu.storeUuid, menu.uuid, "", new ArrayList<>(), 1);
    }

    @Override
    public String toString() {
        return EntityHelper.toString(this, OrderDetail.class);
    }

    private interface Schema {
        String uuid = "order_detail_uuid";
        String menuUuid = "menu_uuid";
        String menuOptionUuidList = "menu_option_uuid_list";
        String menuOrderUuid = "menu_order_uuid";
        String storeUuid = "store_uuid";
        String amount = "menu_quantity";
    }
}
