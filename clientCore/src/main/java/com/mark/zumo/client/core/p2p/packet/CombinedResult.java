/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.p2p.packet;

/**
 * Created by mark on 18. 5. 7.
 */

public class CombinedResult<T, R> {
    public final T t;
    public final R r;

    public CombinedResult(T t, R r) {
        this.t = t;
        this.r = r;
    }
}
