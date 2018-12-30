/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.supervisor.view.permission;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mark.zumo.client.core.util.context.ContextHolder;
import com.mark.zumo.client.supervisor.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mark on 18. 10. 9.
 */
public class PermissionFragment extends Fragment {

    private static final String TAG = "PermissionFragment";

    private static final int REQUEST_CODE = 1414;

    @BindView(R.id.title) AppCompatTextView title;
    @BindView(R.id.content) AppCompatTextView content;

    private Runnable listener;
    private String[] permissions;

    private int titleRes;
    private int messageRes;

    public static PermissionFragment instantiate(String[] permissions, Runnable listener) {
        PermissionFragment permissionFragment = new PermissionFragment();
        permissionFragment.permissions = permissions;
        permissionFragment.listener = listener;
        return permissionFragment;
    }

    public static boolean isGrantedPermissions(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(ContextHolder.getContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    public PermissionFragment setTitle(int titleRes) {
        this.titleRes = titleRes;
        return this;
    }

    public PermissionFragment setMessage(int messageRes) {
        this.messageRes = messageRes;
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_permission, container, false);
        ButterKnife.bind(this, view);
        inflatePermissionDescription();
        return view;
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
    }

    private void inflatePermissionDescription() {
        title.setText(titleRes);
        content.setText(messageRes);
    }

    @OnClick(R.id.request_permission)
    void onClickRequestPermission() {
        //TODO
        if (!isGrantedPermissions(permissions)) {
            ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_CODE);
        } else {
            listener.run();
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode,
                                           @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    listener.run();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
