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
import com.mark.zumo.client.core.R;
import com.mark.zumo.client.core.entity.util.EntityComparator;
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
    @ColumnInfo(name = Schema.orderName) @SerializedName(Schema.orderName)
    public final String orderName;
    @ColumnInfo(name = Schema.customerUuid) @SerializedName(Schema.customerUuid)
    public final String customerUuid;
    @ColumnInfo(name = Schema.storeUuid) @SerializedName(Schema.storeUuid)
    public final String storeUuid;
    @ColumnInfo(name = Schema.orderNumber) @SerializedName(Schema.orderNumber)
    public final String orderNumber;
    @ColumnInfo(name = Schema.createdDate) @SerializedName(Schema.createdDate)
    public final long createdDate;
    @ColumnInfo(name = Schema.totalQuantity) @SerializedName(Schema.totalQuantity)
    public final int totalQuantity;
    @ColumnInfo(name = Schema.totalPrice) @SerializedName(Schema.totalPrice)
    public final int totalPrice;
    @ColumnInfo(name = Schema.state) @SerializedName(Schema.state)
    public final int state;

    public MenuOrder(@NonNull final String uuid, final String orderName, final String customerUuid, final String storeUuid, final String orderNumber, final long createdDate, final int totalQuantity, final int totalPrice, final int state) {
        this.uuid = uuid;
        this.orderName = orderName;
        this.customerUuid = customerUuid;
        this.storeUuid = storeUuid;
        this.orderNumber = orderNumber;
        this.createdDate = createdDate;
        this.totalQuantity = totalQuantity;
        this.totalPrice = totalPrice;
        this.state = state;
    }

    public MenuOrder updateState(int state) {
        return new MenuOrder(this.uuid,
                this.orderName,
                this.customerUuid,
                this.storeUuid,
                this.orderNumber,
                this.createdDate,
                this.totalQuantity,
                this.totalPrice,
                state);
    }

    @Override
    public String toString() {
        return EntityHelper.toString(this);
    }

    public enum State {

        CREATED(R.string.order_state_created, R.color.order_state_created),
        REQUESTED(R.string.order_state_requested, R.color.order_state_requested),
        ACCEPTED(R.string.order_state_accepted, R.color.order_state_accepted),
        COMPLETE(R.string.order_state_complete, R.color.order_state_complete),
        REJECTED(R.string.order_state_rejected, R.color.order_state_rejected),
        CANCELED(R.string.order_state_canceled, R.color.order_state_canceled);

        public final int stringRes;
        public final int colorRes;

        State(final int stringRes, final int colorRes) {
            this.stringRes = stringRes;
            this.colorRes = colorRes;
        }

        public static State of(int orderState) {
            for (State state : values())
                if (orderState == state.ordinal())
                    return state;

            throw new UnsupportedOperationException();
        }
    }

    public interface Schema {
        String uuid = "menu_order_uuid";
        String orderName = "menu_order_name";
        String customerUuid = "customer_uuid";
        String storeUuid = "store_uuid";
        String orderNumber = "menu_order_num";
        String createdDate = "created_date";
        String totalQuantity = "total_quantity";
        String totalPrice = "total_price";
        String state = "menu_order_state";
    }

    @Override
    public boolean equals(final Object obj) {
        return new EntityComparator<>().test(this, obj);
    }
}
