/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.mark.zumo.client.core.appserver.request.registration.StoreRegistrationRequest;
import com.mark.zumo.client.core.appserver.request.registration.result.StoreRegistrationResult;
import com.mark.zumo.client.core.database.dao.DiskRepository;
import com.mark.zumo.client.core.database.dao.MenuDao;
import com.mark.zumo.client.core.database.dao.MenuOptionDao;
import com.mark.zumo.client.core.database.dao.MenuOrderDao;
import com.mark.zumo.client.core.database.dao.PairedBluetoothDeviceDao;
import com.mark.zumo.client.core.database.dao.StoreDao;
import com.mark.zumo.client.core.database.dao.StoreSessionDao;
import com.mark.zumo.client.core.database.dao.StoreUserSessionDao;
import com.mark.zumo.client.core.database.entity.Menu;
import com.mark.zumo.client.core.database.entity.MenuCategory;
import com.mark.zumo.client.core.database.entity.MenuDetail;
import com.mark.zumo.client.core.database.entity.MenuOption;
import com.mark.zumo.client.core.database.entity.MenuOptionCategory;
import com.mark.zumo.client.core.database.entity.MenuOptionDetail;
import com.mark.zumo.client.core.database.entity.MenuOrder;
import com.mark.zumo.client.core.database.entity.OrderDetail;
import com.mark.zumo.client.core.database.entity.PairedBluetoothDevice;
import com.mark.zumo.client.core.database.entity.SessionStore;
import com.mark.zumo.client.core.database.entity.SnsToken;
import com.mark.zumo.client.core.database.entity.Store;
import com.mark.zumo.client.core.database.entity.user.GuestUser;
import com.mark.zumo.client.core.database.entity.user.store.StoreOwner;
import com.mark.zumo.client.core.database.entity.user.store.StoreUser;
import com.mark.zumo.client.core.database.entity.user.store.StoreUserContract;
import com.mark.zumo.client.core.database.entity.user.store.StoreUserSession;
import com.mark.zumo.client.core.payment.kakao.entity.PaymentToken;

/**
 * Created by mark on 18. 4. 30.
 */
@Database(
        entities = {
                Menu.class, MenuOrder.class, Store.class, GuestUser.class,
                OrderDetail.class, MenuOption.class, MenuOptionDetail.class, PaymentToken.class,
                MenuCategory.class, MenuDetail.class, SnsToken.class, StoreUserSession.class,
                MenuOptionCategory.class, StoreUser.class, StoreOwner.class, StoreUserContract.class,
                StoreRegistrationResult.class, StoreRegistrationRequest.class, SessionStore.class,
                PairedBluetoothDevice.class
        }, version = 2)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract DiskRepository diskRepository();
    public abstract MenuDao menuDao();
    public abstract MenuOptionDao menuOptionDao();
    public abstract MenuOrderDao menuOrderDao();
    public abstract StoreDao storeDao();
    public abstract StoreSessionDao storeSessionDao();
    public abstract StoreUserSessionDao storeUserSessionDao();
    public abstract PairedBluetoothDeviceDao pairedBluetoothDeviceDao();
}
