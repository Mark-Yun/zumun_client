package com.mark.zumo.client.core.signup;

/**
 * Created by mark on 18. 5. 7.
 */

public interface SessionCallback {
    void onSuccess(int resultCode);
    void onFailure(int resultCode);
}
