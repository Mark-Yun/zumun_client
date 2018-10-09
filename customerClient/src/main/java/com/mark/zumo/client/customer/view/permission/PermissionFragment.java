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

package com.mark.zumo.client.customer.view.permission;

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

import com.mark.zumo.client.customer.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mark on 18. 10. 9.
 */
public class PermissionFragment extends Fragment {

    private static final int REQUEST_CODE = 1414;
    @BindView(R.id.title) AppCompatTextView title;
    @BindView(R.id.content) AppCompatTextView content;

    private PermissionGrantListener listener;
    private String[] permissions;

    public static Fragment instantiate(String[] permissions, PermissionGrantListener listener) {
        PermissionFragment permissionFragment = new PermissionFragment();
        permissionFragment.permissions = permissions;
        permissionFragment.listener = listener;
        return permissionFragment;
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
        String[] split = permissions[0].split("\\.");
        PermissionResource permissionResource = PermissionResource.valueOf(split[split.length - 1]);
        title.setText(permissionResource.titleRes);
        content.setText(permissionResource.contentRes);
    }

    @OnClick(R.id.request_permission)
    void onClickRequestPermission() {
        //TODO
        if (ContextCompat.checkSelfPermission(getActivity(), permissions[0]) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_CODE);
        } else {
            listener.onPermissionGranted();
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
                    listener.onPermissionGranted();

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

    private enum PermissionResource {
        ACCESS_FINE_LOCATION(R.string.permission_location_title, R.string.permission_location_content),;

        private final int titleRes;
        private final int contentRes;

        PermissionResource(final int titleRes, final int contentRes) {
            this.titleRes = titleRes;
            this.contentRes = contentRes;
        }
    }

    public interface PermissionGrantListener {
        void onPermissionGranted();
    }
}
