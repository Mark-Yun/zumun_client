/*
 * Copyright (c) 2019. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.model;

import android.text.TextUtils;

import com.mark.zumo.client.core.appserver.request.bank.InquiryAccountRequest;
import com.mark.zumo.client.core.appserver.response.InquiryAccountResponse;
import com.mark.zumo.client.core.repository.BankRepository;

import io.reactivex.Maybe;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 19. 1. 13.
 */
public enum BankManager {
    INSTANCE;

    private BankRepository bankRepository;

    BankManager() {
        bankRepository = BankRepository.INSTANCE;
    }

    public Maybe<Boolean> inquiryBankAccount(final String email, final String holderInfo,
                                             final String bankCode, final String accountNumber) {

        InquiryAccountRequest inquiryAccountRequest = new InquiryAccountRequest(bankCode, holderInfo, accountNumber);
        return bankRepository.inquiryBankAccount(email, inquiryAccountRequest)
                .map(inquiryAccountResponse -> matchInquiryBankInfo(inquiryAccountRequest, inquiryAccountResponse))
                .subscribeOn(Schedulers.io());
    }

    private boolean matchInquiryBankInfo(final InquiryAccountRequest request,
                                         final InquiryAccountResponse response) {
        return TextUtils.equals(request.accountHolderInfo, response.accountHolderInfo)
                && TextUtils.equals(request.bankAccountNumber, response.bankAccountNumber)
                && TextUtils.equals(request.bankCode, response.bankCode);
    }

}
