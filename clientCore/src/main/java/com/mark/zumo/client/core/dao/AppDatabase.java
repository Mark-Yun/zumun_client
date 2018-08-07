/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.dao;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.core.entity.MenuCategory;
import com.mark.zumo.client.core.entity.MenuDetail;
import com.mark.zumo.client.core.entity.MenuOption;
import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.entity.OrderDetail;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.entity.VisitStore;
import com.mark.zumo.client.core.entity.user.GuestUser;
import com.mark.zumo.client.core.payment.kakao.entity.PaymentToken;

/**
 * Created by mark on 18. 4. 30.
 */
@Database(
        entities = {
                Menu.class, MenuOrder.class, Store.class, GuestUser.class,
                OrderDetail.class, MenuOption.class, VisitStore.class, PaymentToken.class,
                MenuCategory.class, MenuDetail.class
        }, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public static final String NAME = "com.mark.zumo";

    public abstract DiskRepository diskRepository();
}
