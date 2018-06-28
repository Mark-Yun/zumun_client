/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.entity;

import com.google.gson.annotations.SerializedName;
import com.mark.zumo.client.core.entity.util.EntityHelper;

/**
 * Created by mark on 18. 6. 15.
 */
public class SnsToken {
    @SerializedName(Schema.uuid) public final String uuid;
    @SerializedName(Schema.tokenType) public final String tokenType;
    @SerializedName(Schema.tokenValue) public final String tokenValue;

    public SnsToken(final String uuid, final String tokenType, final String tokenValue) {
        this.uuid = uuid;
        this.tokenType = tokenType;
        this.tokenValue = tokenValue;
    }

    public interface Schema {
        String uuid = "uuid";
        String tokenValue = "token_value";
        String tokenType = "token_type";
    }

    public interface TokenType {
        String ANDROID = "android";
    }

    @Override
    public String toString() {
        return EntityHelper.toString(this, SnsToken.class);
    }
}
