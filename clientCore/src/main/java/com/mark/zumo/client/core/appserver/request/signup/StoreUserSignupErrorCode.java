/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.appserver.request.signup;

import com.mark.zumo.client.core.R;
import com.mark.zumo.client.core.util.context.ContextHolder;

/**
 * Created by mark on 18. 11. 4.
 */
public enum StoreUserSignupErrorCode {
    SUCCESS(R.string.sign_up_error_message_success),
    EMPTY_EMAIL(R.string.sign_up_error_message_empty_email),
    EMPTY_PASSWORD(R.string.sign_up_error_message_empty_password),
    EMPTY_PASSWORD_CONFIRM(R.string.sign_up_error_message_empty_password_confirm),
    EMPTY_NAME(R.string.sign_up_error_message_empty_name),
    EMPTY_PHONE_NUMBER(R.string.sign_up_error_message_empty_phone_number),
    EMPTY_BANK_NAME(R.string.sign_up_error_message_empty_bank_name),
    EMPTY_BANK_ACCOUNT(R.string.sign_up_error_message_empty_bank_account),
    EMPTY_BANK_ACCOUNT_URL(R.string.sign_up_error_message_empty_bank_account_url),
    PASSWORD_DISCORD(R.string.sign_up_error_message_password_discord),
    EMAIL_INCORRECT(R.string.sign_up_error_message_email_incorrect);

    public final String message;

    StoreUserSignupErrorCode(final int messageRes) {
        this.message = ContextHolder.getContext().getString(messageRes);
    }
}
