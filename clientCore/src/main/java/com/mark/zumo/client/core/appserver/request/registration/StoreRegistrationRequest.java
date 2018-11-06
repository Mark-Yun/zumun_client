/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.mark.zumo.client.core.appserver.request.registration;

import com.google.gson.annotations.SerializedName;
import com.mark.zumo.client.core.entity.util.EntityHelper;

/**
 * Created by mark on 18. 11. 6.
 */
public class StoreRegistrationRequest {

    @SerializedName(Schema.id)
    public final long id;
    @SerializedName(Schema.storeName)
    public final String storeName;
    @SerializedName(Schema.storePhoneNumber)
    public final String storePhoneNumber;
    @SerializedName(Schema.storeType)
    public final String storeType;
    @SerializedName(Schema.corporateRegistrationNumber)
    public final String corporateRegistrationNumber;
    @SerializedName(Schema.corporateRegistrationScanUrl)
    public final String corporateRegistrationScanUrl;
    @SerializedName(Schema.latitude)
    public final double latitude;
    @SerializedName(Schema.longitude)
    public final double longitude;
    @SerializedName(Schema.address)
    public final String address;
    @SerializedName(Schema.coverImageRrl)
    public final String coverImageRrl;
    @SerializedName(Schema.thumbnailImageUrl)
    public final String thumbnailImageUrl;

    private StoreRegistrationRequest(final long id, final String storeName, final String storePhoneNumber, final String storeType, final String corporateRegistrationNumber, final String corporateRegistrationScanUrl, final double latitude, final double longitude, final String address, final String coverImageRrl, final String thumbnailImageUrl) {
        this.id = id;
        this.storeName = storeName;
        this.storePhoneNumber = storePhoneNumber;
        this.storeType = storeType;
        this.corporateRegistrationNumber = corporateRegistrationNumber;
        this.corporateRegistrationScanUrl = corporateRegistrationScanUrl;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.coverImageRrl = coverImageRrl;
        this.thumbnailImageUrl = thumbnailImageUrl;
    }

    @Override
    public String toString() {
        return EntityHelper.toString(this);
    }

    public interface Schema {
        String id = "id";
        String storeName = "store_name";
        String storePhoneNumber = "store_phone_number";
        String storeType = "store_type";
        String corporateRegistrationNumber = "corporate_registration_number";
        String corporateRegistrationScanUrl = "corporate_registration_scan_url";
        String latitude = "latitude";
        String longitude = "longitude";
        String address = "address";
        String coverImageRrl = "cover_image_url";
        String thumbnailImageUrl = "thumbnail_image_url";
    }

    public static class Builder {
        private long id;
        private String storeName;
        private String storePhoneNumber;
        private String storeType;
        private String corporateRegistrationNumber;
        private String corporateRegistrationScanUrl;
        private double latitude;
        private double longitude;
        private String address;
        private String coverImageRrl;
        private String thumbnailImageUrl;

        public Builder() {
        }

        public Builder setId(final long id) {
            this.id = id;
            return this;
        }

        public Builder setStoreName(final String storeName) {
            this.storeName = storeName;
            return this;
        }

        public Builder setStorePhoneNumber(final String storePhoneNumber) {
            this.storePhoneNumber = storePhoneNumber;
            return this;
        }

        public Builder setStoreType(final String storeType) {
            this.storeType = storeType;
            return this;
        }

        public Builder setCorporateRegistrationNumber(final String corporateRegistrationNumber) {
            this.corporateRegistrationNumber = corporateRegistrationNumber;
            return this;
        }

        public Builder setCorporateRegistrationScanUrl(final String corporateRegistrationScanUrl) {
            this.corporateRegistrationScanUrl = corporateRegistrationScanUrl;
            return this;
        }

        public Builder setLatitude(final double latitude) {
            this.latitude = latitude;
            return this;
        }

        public Builder setLongitude(final double longitude) {
            this.longitude = longitude;
            return this;
        }

        public Builder setAddress(final String address) {
            this.address = address;
            return this;
        }

        public Builder setCoverImageRrl(final String coverImageRrl) {
            this.coverImageRrl = coverImageRrl;
            return this;
        }

        public Builder setThumbnailImageUrl(final String thumbnailImageUrl) {
            this.thumbnailImageUrl = thumbnailImageUrl;
            return this;
        }

        public StoreRegistrationRequest build() throws StoreRegistrationException {

            StoreRegistrationRequest storeRegistrationRequest = new StoreRegistrationRequest(id, storeName, storePhoneNumber, storeType, corporateRegistrationNumber, corporateRegistrationScanUrl, latitude, longitude, address, coverImageRrl, thumbnailImageUrl);
            for (Validator validator : Validator.values()) {
                if (!validator.verify(storeRegistrationRequest)) {
                    throw new StoreRegistrationException(validator.ofErrorCode());
                }
            }
            return storeRegistrationRequest;
        }
    }
}
