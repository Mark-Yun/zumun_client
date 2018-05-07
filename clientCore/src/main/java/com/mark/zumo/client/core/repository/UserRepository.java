package com.mark.zumo.client.core.repository;

import android.content.Context;

import com.mark.zumo.client.core.appserver.AppServerService;
import com.mark.zumo.client.core.appserver.AppServerServiceProvider;
import com.mark.zumo.client.core.dao.AppDatabase;
import com.mark.zumo.client.core.dao.AppDatabaseProvider;
import com.mark.zumo.client.core.dao.UserDao;
import com.mark.zumo.client.core.entity.user.CustomerUser;
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

    public Observable<User> findById(long id) {
        return Observable.create(e -> {
        });
    }

    public Observable<CustomerUser> findCustomerUserById(long id) {
        return Observable.create(e -> {
        });
    }

    public Observable<User> findByName(String name) {
        return Observable.create(e -> {
        });
    }
}
