/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

/**
 * Created by mark on 18. 5. 7.
 */

public class SignUpViewModel extends AndroidViewModel {

    public static final String TAG = "SignUpViewModel";

    public SignUpViewModel(@NonNull Application application) {
        super(application);

    }
}
