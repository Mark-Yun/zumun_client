/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.app;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.mark.zumo.client.core.R;
import com.mark.zumo.client.core.util.context.ContextHolder;

import java.io.IOException;
import java.net.UnknownHostException;

import io.reactivex.exceptions.OnErrorNotImplementedException;
import io.reactivex.exceptions.UndeliverableException;
import io.reactivex.plugins.RxJavaPlugins;
import okhttp3.ResponseBody;
import retrofit2.HttpException;

/**
 * Created by mark on 18. 7. 1.
 */
public class AppErrorHandler {

    public static final String TAG = "AppErrorHandler";

    private static void showToast(int stringResId) {
        Context context = ContextHolder.getContext();
        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(context, stringResId, Toast.LENGTH_SHORT).show());
    }

    private static void showDebugToast(String string) {
        Context context = ContextHolder.getContext();
        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(context, string, Toast.LENGTH_LONG).show());
    }

    public static void setup() {
        RxJavaPlugins.setErrorHandler(e -> {
            e.printStackTrace();
            if (e instanceof UndeliverableException ||
                    e instanceof OnErrorNotImplementedException ||
                    e instanceof NullPointerException) {
                e = e.getCause();
            }

            Log.e(TAG, "an error caught.", e);
            if (e != null && e.getMessage() != null) {
                Log.e(TAG, e.getMessage());
                Log.e(TAG, e.getMessage());
            }

            if (e instanceof HttpException) {
                if (BuildConfig.BUILD_TYPE == AppConfig.DEBUG) {
                    ResponseBody responseBody = ((HttpException) e).response().errorBody();
                    if (responseBody != null) {
                        showDebugToast(responseBody.string());
                    }
                    return;
                }
                Log.e(TAG, "setup: ", e);
                return;
            } else if (e instanceof UnknownHostException) {
                showToast(R.string.error_message_on_unknown_host_exception);
                Log.e(TAG, "setup: ", e);
                return;
            } else if (e instanceof IOException) {
                Log.e(TAG, "setup: ", e);
                return;
            } else if (e instanceof SQLiteConstraintException) {
                if (BuildConfig.BUILD_TYPE == AppConfig.DEBUG) {
                    showDebugToast(e.getMessage());
                    return;
                }
                Log.e(TAG, "setup: ", e);
            }

            if (e instanceof InterruptedException) {
                // fine, some blocking code was interrupted by a dispose call
                return;
            }

            Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
        });
    }
}
