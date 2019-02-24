/*
 * Copyright (c) 2019. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.appserver.response.crypto;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.mark.zumo.client.core.security.EncryptionUtil;

import java.lang.reflect.ParameterizedType;
import java.security.PrivateKey;

/**
 * Created by mark on 19. 2. 3.
 */
public class CryptoResponse<T> {
    @SerializedName(Schema.encryptedContent)
    public final String encryptedContent;

    private Class<T> type;

    public CryptoResponse(final String encryptedContent) {
        this.type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        this.encryptedContent = encryptedContent;
    }

    public T convert(PrivateKey privateKey) {
        return convertInternal(decryptInternal(privateKey));
    }

    private String decryptInternal(PrivateKey privateKey) {
        return EncryptionUtil.decryptRSA(privateKey, encryptedContent);
    }

    private T convertInternal(String json) {
        return new Gson().fromJson(json, getClass().getGenericSuperclass());
    }

    public interface Schema {
        String encryptedContent = "encrypted_content";
    }
}
