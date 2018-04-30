package com.mark.zumo.client.core.repository;

import android.content.Context;

import com.mark.zumo.client.core.appserver.AppServerService;
import com.mark.zumo.client.core.appserver.AppServerServiceProvider;
import com.mark.zumo.client.core.dao.AppDatabase;
import com.mark.zumo.client.core.dao.AppDatabaseProvider;
import com.mark.zumo.client.core.dao.UserDao;
import com.mark.zumo.client.core.entity.user.User;

import io.reactivex.Observable;

/**
 * Created by mark on 18. 4. 30.
 */

public class UserRepository {

    private volatile static UserRepository instance;

    private AppDatabase database;

    private UserRepository(Context context) {
        database = AppDatabaseProvider.getDatabase(context);
    }

    public static UserRepository from(Context context) {
        if (instance == null) {
            synchronized (UserRepository.class) {
                if (instance == null) instance = new UserRepository(context);
            }
        }
        return instance;
    }

    private UserDao userDao() {
        return database.userDao();
    }

    private AppServerService service() {
        return AppServerServiceProvider.INSTANCE.service;
    }

    public Observable findById(long id) {
        return Observable.create(e -> {
            User user1 = userDao().findById(id);
            if (user1 != null)
                e.onNext(user1);

            User user2 = service().findById(id).execute().body();
            if (user2 != null)
                e.onNext(user2);

            e.onComplete();
        });
    }

    public Observable findByName(String name) {
        return Observable.create(e -> {
            User user1 = userDao().findByName(name);
            if (user1 != null)
                e.onNext(user1);

            User user2 = service().findByName(name).execute().body();
            if (user2 != null)
                e.onNext(user2);

            e.onComplete();
        });
    }
}
