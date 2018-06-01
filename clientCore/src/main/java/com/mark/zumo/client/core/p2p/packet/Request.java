/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.p2p.packet;

/**
 * Created by mark on 18. 5. 7.
 */

public enum Request {
    REQ_MENU_ITEM_LIST;

    public static Request valueOf(int ordinal) {
        for (Request req : values()) {
            if (ordinal == req.ordinal())
                return req;
        }

        throw new UnsupportedOperationException();
    }
}
