package com.mark.zumo.client.core.appserver;

import com.mark.zumo.client.core.entity.user.User;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by mark on 18. 4. 30.
 */

public interface AppServerService {
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("TODO: input server url")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    @GET("users/{id}")
    Call<User> getUser(@Path("id") long id);
}
