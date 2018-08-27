/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.app;

/**
 * Created by mark on 18. 8. 15.
 * for debug build config
 */
public class BuildConfig {
    public static final BuildType BUILD_TYPE = BuildType.DEBUG;

    public enum BuildType {
        DEBUG,
        RELEASE;

        public boolean is(BuildType buildType) {
            return this == buildType;
        }
    }
}
