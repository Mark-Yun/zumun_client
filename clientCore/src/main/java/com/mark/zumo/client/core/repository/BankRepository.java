/*
 * Copyright (c) 2019. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.repository;

import com.mark.zumo.client.core.appserver.AppServerServiceProvider;
import com.mark.zumo.client.core.appserver.NetworkRepository;
import com.mark.zumo.client.core.appserver.request.bank.InquiryAccountRequest;
import com.mark.zumo.client.core.appserver.request.crypto.CryptoRequest;
import com.mark.zumo.client.core.appserver.response.InquiryAccountResponse;

import io.reactivex.Maybe;

/**
 * Created by mark on 19. 1. 13.
 */
public enum BankRepository {
    INSTANCE;

    private static final String TAG = "MenuRepository";

    private NetworkRepository networkRepository() {
        return AppServerServiceProvider.INSTANCE.networkRepository();
    }

    public Maybe<InquiryAccountResponse> inquiryBankAccount(String email, InquiryAccountRequest inquiryAccountRequest) {
        return networkRepository().signInHandShake(email)
                .map(storeUserHandShakeResponse -> storeUserHandShakeResponse.publicKey)
                .map(publicKey -> CryptoRequest.of(inquiryAccountRequest, publicKey))
                .flatMap(cryptoRequest -> networkRepository().inquiryBankAccount(cryptoRequest)
                        .map(cryptoResponse -> cryptoResponse.convert(cryptoRequest.privateKey))
                );
    }
}
