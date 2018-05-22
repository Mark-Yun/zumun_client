package com.mark.zumo.client.core.entity;

import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;
import java.util.UUID;

import io.reactivex.annotations.NonNull;

/**
 * Created by mark on 18. 5. 22.
 */
public class OrderDetail implements Serializable {
    @PrimaryKey @NonNull private UUID order_detail_uuid;
    private UUID menu_uuid;
    private UUID menu_option_uuid;
    private UUID menu_order_uuid;
}
