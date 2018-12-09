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
                    showDebugToast(((HttpException) e).response().errorBody().string());
                    return;
                }
                showToast(R.string.error_message_on_http_exception);
                return;
            } else if (e instanceof UnknownHostException) {
                showToast(R.string.error_message_on_unknown_host_exception);
                return;
            } else if (e instanceof IOException) {
                showToast(R.string.error_message_on_io_exception);
                return;
            }

            if (e instanceof InterruptedException) {
                // fine, some blocking code was interrupted by a dispose call
                return;
            }

            Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
        });
    }
}
