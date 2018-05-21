package com.mark.zumo.client.core.app;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.Arrays;

/**
 * Created by mark on 18. 5. 21.
 */
public class BaseActivity extends AppCompatActivity {

    public static final int R_PERM = 2822;
    public static final String TAG = "BaseActivity";
    private static final int REQUEST = 112;

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    @CallSuper
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 23) {
            try {
                PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_PERMISSIONS);
                String[] requestedPermissions = packageInfo.requestedPermissions;

                Log.d(TAG, "onCreate: requestedPermissions-" + Arrays.toString(requestedPermissions));
                if (!hasPermissions(this, requestedPermissions)) {
                    ActivityCompat.requestPermissions(this, requestedPermissions, REQUEST);
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "onCreate: ", e);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(this, "PERMISSIONS Denied", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }

}
