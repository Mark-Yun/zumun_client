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
            Drawable drawable = context.getResources().getDrawable(R.drawable.test_menu_image);
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] bytes = stream.toByteArray();

            List<MenuItem> menuItemList = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                MenuItem menuItem = new MenuItem((long) i, "test_name", bytes, store.id, 500, 0, 0);
                menuItemList.add(menuItem);
            }
            return menuItemList;
        });
    }
}
