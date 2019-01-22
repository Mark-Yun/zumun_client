/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.supervisor.view.storeregistration.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.github.chrisbanes.photoview.PhotoView;
import com.mark.zumo.client.core.util.glide.GlideApp;
import com.mark.zumo.client.supervisor.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 18. 12. 30.
 */
public class CorporateScanImageActivity extends AppCompatActivity {

    static final String CORPORATE_SCAN_IMAGE_URL_KEY = "corporate_registration_scan_url";

    @BindView(R.id.photo_view) PhotoView photoView;

    public static void startImageDetail(Context context, String url) {
        Intent intent = new Intent(context, CorporateScanImageActivity.class);
        intent.putExtra(CORPORATE_SCAN_IMAGE_URL_KEY, url);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_corporate_scan_image);
        ButterKnife.bind(this);

        GlideApp.with(this)
                .load(getIntent().getStringExtra(CORPORATE_SCAN_IMAGE_URL_KEY))
                .into(photoView);
    }
}
