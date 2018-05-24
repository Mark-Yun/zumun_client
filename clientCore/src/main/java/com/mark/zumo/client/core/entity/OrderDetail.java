package com.mark.zumo.client.core.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.mark.zumo.client.core.dao.OrderDetailDao;

import java.io.Serializable;

/**
 * Created by mark on 18. 5. 22.
 */
@Entity(tableName = OrderDetailDao.TABLE_NAME)
public class OrderDetail implements Serializable {

    @PrimaryKey @NonNull @SerializedName(Schema.uuid) @ColumnInfo(name = Schema.uuid)
    public final String uuid;
    @SerializedName(Schema.menuUuid) @ColumnInfo(name = Schema.menuUuid)
    public final String menuUuid;
    @SerializedName(Schema.menuOrderUuid) @ColumnInfo(name = Schema.menuOrderUuid)
    public final String menuOrderUuid;
    @SerializedName(Schema.menuOptionUuid) @ColumnInfo(name = Schema.menuOptionUuid)
    public final String menuOptionUuid;

    public OrderDetail(@NonNull final String uuid, final String menuUuid, final String menuOrderUuid, final String menuOptionUuid) {
        this.uuid = uuid;
        this.menuUuid = menuUuid;
        this.menuOrderUuid = menuOrderUuid;
        this.menuOptionUuid = menuOptionUuid;
    }

    private interface Schema {
        String uuid = "order_detail_uuid";
        String menuUuid = "menu_uuid";
        String menuOptionUuid = "menu_option_uuid";
        String menuOrderUuid = "menu_order_uuid";
    }
}
