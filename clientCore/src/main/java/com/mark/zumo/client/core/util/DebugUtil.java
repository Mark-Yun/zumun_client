/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.util;

import android.os.Build;

import com.mark.zumo.client.core.R;
import com.mark.zumo.client.core.entity.Menu;
import com.mark.zumo.client.core.entity.MenuOption;
import com.mark.zumo.client.core.entity.MenuOrder;
import com.mark.zumo.client.core.entity.OrderDetail;
import com.mark.zumo.client.core.entity.Store;
import com.mark.zumo.client.core.entity.user.GuestUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by mark on 18. 5. 3.
 */

public class DebugUtil {

    public static final String TEST_STORE_UUID = "FD8BC4DD00B04E60A166A5FBD3454E8F";

    public static GuestUser guestUser() {
        return new GuestUser(UUID.randomUUID().toString());
    }

    public static Store store() {
        return new Store(TEST_STORE_UUID, Build.MODEL, 0, 0, "TEST", "TEST", null);
    }

    public static MenuOrder menuOrder() {
        return new MenuOrder(UUID.randomUUID().toString(),
                "TESTNAME",
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                "24", "3/6/2018 15:33", 5, 25500);
    }

    public static List<OrderDetail> orderDetailList(String orderUuid) {
        OrderDetail[] orderDetails = {
                new OrderDetail(UUID.randomUUID().toString(),
                        TEST_STORE_UUID,
                        "AE62797461FA4F2292AF11F48D2F5C30",
                        "Iced Americano TTTTTTTTTTTTTTTTTTTTT",
                        orderUuid,
                        Arrays.asList("5007D8547CAC47D38C41C4F419986D49", "A530720210564C93A2CDE072B640D31F"),
                        1,
                        100000
                ),
                new OrderDetail(UUID.randomUUID().toString(),
                        TEST_STORE_UUID,
                        "AE62797461FA4F2292AF11F48D2F5C30",
                        "Iced Americano",
                        orderUuid,
                        Arrays.asList("5007D8547CAC47D38C41C4F419986D49", "A530720210564C93A2CDE072B640D31F"),
                        2,
                        100000
                ),
                new OrderDetail(UUID.randomUUID().toString(),
                        TEST_STORE_UUID,
                        "AE62797461FA4F2292AF11F48D2F5C30",
                        "Iced Americano",
                        orderUuid,
                        Arrays.asList("5007D8547CAC47D38C41C4F419986D49"),
                        3,
                        100000
                ),
                new OrderDetail(UUID.randomUUID().toString(),
                        TEST_STORE_UUID,
                        "AE62797461FA4F2292AF11F48D2F5C30",
                        "Iced Americano",
                        orderUuid,
                        Arrays.asList("5007D8547CAC47D38C41C4F419986D49"),
                        5,
                        100000
                ),
                new OrderDetail(UUID.randomUUID().toString(),
                        TEST_STORE_UUID,
                        "AE62797461FA4F2292AF11F48D2F5C30",
                        "Iced Americano",
                        orderUuid,
                        null,
                        11,
                        100000
                ),
                new OrderDetail(UUID.randomUUID().toString(),
                        TEST_STORE_UUID,
                        "AE62797461FA4F2292AF11F48D2F5C30",
                        "Iced Americano",
                        orderUuid,
                        Arrays.asList("5007D8547CAC47D38C41C4F419986D49", "A530720210564C93A2CDE072B640D31F"),
                        51,
                        100000
                )

        };

        return Arrays.asList(orderDetails);
    }

    public static List<Store> storeList() {
        Store[] stores = {
                new Store(TEST_STORE_UUID, "test1", 15, 15, null, null, null),
                new Store(TEST_STORE_UUID, "testStore2", 15, 15, null, null, null),
                new Store(TEST_STORE_UUID, "testTestStore3", 15, 15, null, null, null),
                new Store(TEST_STORE_UUID, "COCOCOCOCOCOCO_@#!@$*I@", 15, 15, null, null, null),
                new Store(TEST_STORE_UUID, "LAIIEJ!F#IFJASDJFP(!O@$I!P(", 15, 15, null, null, null),
                new Store(TEST_STORE_UUID, "test1", 15, 15, null, null, null),
                new Store(TEST_STORE_UUID, "testStore2", 15, 15, null, null, null),
                new Store(TEST_STORE_UUID, "testTestStore3", 15, 15, null, null, null),
                new Store(TEST_STORE_UUID, "COCOCOCOCOCOCO_@#!@$*I@", 15, 15, null, null, null),
                new Store(TEST_STORE_UUID, "LAIIEJ!F#IFJASDJFP(!O@$I!P(", 15, 15, null, null, null),
                new Store(TEST_STORE_UUID, "test1", 15, 15, null, null, null),
                new Store(TEST_STORE_UUID, "testStore2", 15, 15, null, null, null),
                new Store(TEST_STORE_UUID, "testTestStore3", 15, 15, null, null, null),
                new Store(TEST_STORE_UUID, "COCOCOCOCOCOCO_@#!@$*I@", 15, 15, null, null, null),
                new Store(TEST_STORE_UUID, "LAIIEJ!F#IFJASDJFP(!O@$I!P(", 15, 15, null, null, null)
        };
        return Arrays.asList(stores);
    }

    public static List<Menu> menuItemList() {
        List<FakeData> dataList = createFakeData();

        List<Menu> menuList = new ArrayList<>();
        for (FakeData data : dataList) {
            Menu menu = new Menu("TEST", "TEST", "TEST", data.name, data.price, "TT");
            menuList.add(menu);
        }

        return menuList;
    }

    private static List<FakeData> createFakeData() {
        //TODO: remove test data
        List<FakeData> resultList = new ArrayList<>();
        resultList.add(new FakeData(R.drawable.data_1_ice, "아이스 카페 라떼", 4500));
        resultList.add(new FakeData(R.drawable.data_1_hot, "카페 라떼", 4500));
        resultList.add(new FakeData(R.drawable.data_2_ice, "아이스 카페 모카", 4500));
        resultList.add(new FakeData(R.drawable.data_2_hot, "카페 모카", 4500));
        resultList.add(new FakeData(R.drawable.data_3_hot, "에스프레소", 4500));
        resultList.add(new FakeData(R.drawable.data_4_ice, "아이스 카라멜 마키아또", 4500));
        resultList.add(new FakeData(R.drawable.data_4_hot, "카라멜 마키아또", 4500));
        resultList.add(new FakeData(R.drawable.data_5_ice, "아이스 카페 아메리카노", 4500));
        resultList.add(new FakeData(R.drawable.data_5_hot, "카페 아메리카노", 4500));
        resultList.add(new FakeData(R.drawable.data_4_ice, "아이스 카라멜 마키아또", 4500));
        resultList.add(new FakeData(R.drawable.data_4_hot, "카라멜 마키아또", 4500));
        resultList.add(new FakeData(R.drawable.data_5_ice, "아이스 카페 아메리카노", 4500));
        resultList.add(new FakeData(R.drawable.data_5_hot, "카페 아메리카노", 4500));
        resultList.add(new FakeData(R.drawable.data_4_ice, "아이스 카라멜 마키아또", 4500));
        resultList.add(new FakeData(R.drawable.data_4_hot, "카라멜 마키아또", 4500));
        resultList.add(new FakeData(R.drawable.data_5_ice, "아이스 카페 아메리카노", 4500));
        resultList.add(new FakeData(R.drawable.data_5_hot, "카페 아메리카노", 4500));
        return resultList;
    }

    public static List<MenuOption> menuOptionListFromServer(String menuUuid) {
        List<MenuOption> result = new ArrayList<>();
        result.add(new MenuOption("TEST", menuUuid, "size", "small", -200));
        result.add(new MenuOption("TEST", menuUuid, "size", "tall", 0));
        result.add(new MenuOption("TEST", menuUuid, "size", "grande", 500));
        result.add(new MenuOption("TEST", menuUuid, "시럽", "없음", 0));
        result.add(new MenuOption("TEST", menuUuid, "시럽", "많이", 0));
        result.add(new MenuOption("TEST", menuUuid, "test", "1", 1));
        result.add(new MenuOption("TEST", menuUuid, "test", "2", 2));
        result.add(new MenuOption("TEST", menuUuid, "test", "3", 3));
        result.add(new MenuOption("TEST", menuUuid, "test", "4", 4));
        result.add(new MenuOption("TEST", menuUuid, "test", "5", 5));
        result.add(new MenuOption("TEST", menuUuid, "test", "6", 6));
        result.add(new MenuOption("TEST", menuUuid, "1", "tall", 0));
        result.add(new MenuOption("TEST", menuUuid, "2", "tall", 0));
        result.add(new MenuOption("TEST", menuUuid, "3", "tall", 0));
        result.add(new MenuOption("TEST", menuUuid, "4", "tall", 0));
        result.add(new MenuOption("TEST", menuUuid, "5", "tall", 0));
        result.add(new MenuOption("TEST", menuUuid, "6", "tall", 0));
        result.add(new MenuOption("TEST", menuUuid, "7", "tall", 0));
        return result;
    }

    public static List<MenuOption> menuOptionListFromOrder() {
        List<MenuOption> result = new ArrayList<>();
        result.add(new MenuOption("TEST", UUID.randomUUID().toString(), "size", "grande", 500));
        result.add(new MenuOption("TEST", UUID.randomUUID().toString(), "시럽", "없음", 0));
        result.add(new MenuOption("TEST", UUID.randomUUID().toString(), "test", "1", 1));
        result.add(new MenuOption("TEST", UUID.randomUUID().toString(), "1", "tall", 0));
        result.add(new MenuOption("TEST", UUID.randomUUID().toString(), "2", "tall", 0));
        return result;
    }

    private static class FakeData {
        public int drawableId;
        public String name;
        public int price;

        public FakeData(int drawableId, String name, int price) {
            //TODO: remove test data
            this.drawableId = drawableId;
            this.name = name;
            this.price = price;
        }
    }
}
