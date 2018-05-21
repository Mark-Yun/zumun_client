package com.mark.zumo.client.core.util;

import android.os.Build;

import com.mark.zumo.client.core.R;
import com.mark.zumo.client.core.entity.MenuItem;
import com.mark.zumo.client.core.entity.MenuOrder;
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

    public static GuestUser guestUser() {
        return new GuestUser();
    }

    public static Store store() {
        return new Store(UUID.randomUUID(), Build.MODEL, 0, 0);
    }

    public static MenuOrder menuOrder() {
        return new MenuOrder(1, 2, 3, null, 5, 100);
    }

    public static List<Store> storeList() {
        Store[] stores = {
                new Store(UUID.randomUUID(), "test1", 15, 15),
                new Store(UUID.randomUUID(), "testStore2", 15, 15),
                new Store(UUID.randomUUID(), "testTestStore3", 15, 15),
                new Store(UUID.randomUUID(), "COCOCOCOCOCOCO_@#!@$*I@", 15, 15),
                new Store(UUID.randomUUID(), "LAIIEJ!F#IFJASDJFP(!O@$I!P(", 15, 15),
        };
        return new ArrayList<>(Arrays.asList(stores));
    }

    public static List<MenuItem> menuItemList() {
        List<FakeData> dataList = createFakeData();

        List<MenuItem> menuItemList = new ArrayList<>();
        for (FakeData data : dataList) {
            MenuItem menuItem = new MenuItem(UUID.randomUUID(), data.name, "TEST", data.price);
            menuItemList.add(menuItem);
        }

        return menuItemList;
    }

    private static List<FakeData> createFakeData() {
        //TODO: remove test data
        List<FakeData> resultList = new ArrayList<>();
        resultList.add(new FakeData(R.drawable.data_1_ice, "아이스 카페 라떼", 4500));
        resultList.add(new FakeData(R.drawable.data_1_hot, "카페 라떼", 4500));
        resultList.add(new FakeData(R.drawable.data_2_ice, "아이스 카페 모카", 4500));
        resultList.add(new FakeData(R.drawable.data_2_hot, "카페 모카", 4500));
        resultList.add(new FakeData(R.drawable.data_3_hot, "에스프레", 4500));
        resultList.add(new FakeData(R.drawable.data_4_ice, "아이스 카라멜 마키아또", 4500));
        resultList.add(new FakeData(R.drawable.data_4_hot, "카라멜 마키아또", 4500));
        resultList.add(new FakeData(R.drawable.data_5_ice, "아이스 카페 아메리카노", 4500));
        resultList.add(new FakeData(R.drawable.data_5_hot, "카페 아메리카노", 4500));
        return resultList;
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
