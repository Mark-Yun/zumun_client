package com.mark.zumo.client.core.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by mark on 18. 5. 22.
 */
@Entity
public class OrderDetail implements Serializable {

    @PrimaryKey @NonNull @SerializedName("order_detail_uuid") @ColumnInfo(name = "order_detail_uuid")
    public final UUID uuid;
    @SerializedName("menu_uuid") @ColumnInfo(name = "menu_uuid")
    public final UUID menuUuid;
    @SerializedName("menu_option_uuid") @ColumnInfo(name = "menu_option_uuid")
    public final UUID menuOptionUuid;
    @SerializedName("menu_order_uuid") @ColumnInfo(name = "menu_order_uuid")
    public final UUID menuOrderUuid;

    public OrderDetail(@NonNull final UUID uuid, final UUID menuUuid, final UUID menuOptionUuid, final UUID menuOrderUuid) {
        this.uuid = uuid;
        this.menuUuid = menuUuid;
        this.menuOptionUuid = menuOptionUuid;
        this.menuOrderUuid = menuOrderUuid;
    }
}
