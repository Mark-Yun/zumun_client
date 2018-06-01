/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Created by mark on 18. 5. 2.
 */

public class DebugMainActivity extends Activity {

    public static final String TAG = "DebugMainActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.debug_main_activity);

        LinearLayout linearLayout = findViewById(R.id.debug_main_activity);

        setupPackageActivities(linearLayout, "com.mark.zumo.client.customer.debug");
        setupPackageActivities(linearLayout, "com.mark.zumo.client.customer");
    }

    private void setupPackageActivities(LinearLayout linearLayout, String packageName) {
        ActivityInfo[] activityInfos = listUpActivities(packageName);
        buildActivityButtons(packageName, linearLayout, activityInfos);
    }

    private void startDebugActivity(Class clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }

    private ActivityInfo[] listUpActivities(String packageName) {
        PackageInfo packageInfo;

        try {
            packageInfo = getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }

        return packageInfo.activities;
    }

    private void buildActivityButtons(String packageName, LinearLayout linearLayout, ActivityInfo[] activityInfos) {
        if (activityInfos == null) {
            return;
        }
        for (ActivityInfo activityInfo : activityInfos) {
            if (!activityInfo.name.startsWith("com.mark.zumo")) continue;
            Button button = buildButton(activityInfo);
            if (button == null) continue;
            linearLayout.addView(button);
        }
    }

    private Button buildButton(ActivityInfo activityInfo) {
        String name = activityInfo.name;
        Class clazz;
        try {
            clazz = Class.forName(name);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "buildButton: ", e);
            return null;
        }

        String simpleName = clazz.getSimpleName();
        Button button = new Button(this);

        button.setText(simpleName);
        button.setOnClickListener(v -> startDebugActivity(clazz));

        return button;
    }
}
