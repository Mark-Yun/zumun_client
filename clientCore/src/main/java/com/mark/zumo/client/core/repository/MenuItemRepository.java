package com.mark.zumo.client.core.repository;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.mark.zumo.client.core.R;
import com.mark.zumo.client.core.appserver.AppServerService;
import com.mark.zumo.client.core.appserver.AppServerServiceProvider;
import com.mark.zumo.client.core.dao.AppDatabase;
import com.mark.zumo.client.core.dao.AppDatabaseProvider;
import com.mark.zumo.client.core.dao.MenuItemDao;
import com.mark.zumo.client.core.entity.MenuItem;
import com.mark.zumo.client.core.entity.Store;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;

/**
 * Created by mark on 18. 4. 30.
 */

public class MenuItemRepository {

    private volatile static MenuItemRepository instance;

    private AppDatabase database;
    private Context context;

    private MenuItemRepository(Context context) {
        database = AppDatabaseProvider.getDatabase(context);
        this.context = context;
    }

    public static MenuItemRepository from(Context context) {
        if (instance == null) {
            synchronized (MenuItemRepository.class) {
                if (instance == null) instance = new MenuItemRepository(context);
            }
        }
        return instance;
    }

    private MenuItemDao menuItemDao() {
        return database.menuItemDao();
    }

    private AppServerService appServerService() {
        return AppServerServiceProvider.INSTANCE.service;
    }

    public Single<List<MenuItem>> getMenuItemsOfStore(Store store) {
        return Single.fromCallable(() -> {
            //TODO: remove test data
            List<FakeData> dataList = createFakeData();

            List<MenuItem> menuItemList = new ArrayList<>();
            int i = 0;
            for (FakeData data : dataList) {
                MenuItem menuItem = new MenuItem(String.valueOf(i++), data.name, "TEST", data.price);
                menuItemList.add(menuItem);
            }
            return menuItemList;
        });
    }

    private byte[] getByteArrayOfDrawable(int id) {
        Drawable drawable = context.getResources().getDrawable(id);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    private List<FakeData> createFakeData() {
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

    private class FakeData {
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
