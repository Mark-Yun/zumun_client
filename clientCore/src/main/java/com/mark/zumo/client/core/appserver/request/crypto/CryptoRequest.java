/*
 * Copyright (c) 2019. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.appserver.request.crypto;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.mark.zumo.client.core.security.EncryptionUtil;

import java.security.KeyPair;
import java.security.PrivateKey;

/**
 * Created by mark on 19. 2. 3.
 */
public class CryptoRequest {
    @SerializedName(Schema.encryptedContent)
    public final String encryptedContent;
    @SerializedName(Schema.publicKey)
    public final String publicKey;
    public transient final PrivateKey privateKey;

    private CryptoRequest(final String encryptedContent) {
        KeyPair keyPair = EncryptionUtil.generateKeyPair();
        this.publicKey = EncryptionUtil.convertPublicKey(keyPair.getPublic());
        this.privateKey = keyPair.getPrivate();
        this.encryptedContent = encryptedContent;
    }

    public static CryptoRequest of(Object object, String serverPublicKey) {
        String originContent = new Gson().toJson(object);
        String encryptedContent = EncryptionUtil.encryptRSA(serverPublicKey, originContent);
        return new CryptoRequest(encryptedContent);
    }

    public interface Schema {
        String encryptedContent = "encrypted_content";
        String publicKey = "client_public_key";
    }
}
