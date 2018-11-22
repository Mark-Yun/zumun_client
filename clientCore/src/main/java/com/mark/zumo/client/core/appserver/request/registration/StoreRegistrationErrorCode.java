/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.appserver.request.registration;

import com.mark.zumo.client.core.R;
import com.mark.zumo.client.core.util.context.ContextHolder;

/**
 * Created by mark on 18. 11. 4.
 */
public enum StoreRegistrationErrorCode {
    SUCCESS(R.string.store_registration_error_message_success),
    EMPTY_STORE_NAME(R.string.store_registration_error_message_empty_store_name),
    EMPTY_STORE_PHONE_NUMBER(R.string.store_registration_error_message_empty_store_phone_number),
    EMPTY_STORE_TYPE(R.string.store_registration_error_message_empty_store_type),
    EMPTY_CORPORATE_REGISTRATION_NUMBER(R.string.store_registration_error_message_empty_corporate_registration_number),
    EMPTY_CORPORATE_REGISTRATION_SCAN_URL(R.string.store_registration_error_message_empty_corporate_registration_scan_url),
    EMPTY_EMPTY_LOCATION(R.string.store_registration_error_message_empty_location),
    EMPTY_EMPTY_ADDRESS(R.string.store_registration_error_message_empty_address),
    EMPTY_COVER_IMAGE_URL(R.string.store_registration_error_message_empty_cover_image_url),
    EMPTY_THUMBNAIL_IMAGE_URL(R.string.store_registration_error_message_empty_thumbnail_image_url);

    public final String message;

    StoreRegistrationErrorCode(final int messageRes) {
        this.message = ContextHolder.getContext().getString(messageRes);
    }
}