/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.mark.zumo.client.core.appserver.request.registration;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mark.zumo.client.core.appserver.request.registration.result.StoreRegistrationResult;
import com.mark.zumo.client.core.entity.util.EntityHelper;

import java.util.List;

/**
 * Created by mark on 18. 11. 6.
 */
@Entity(tableName = StoreRegistrationRequest.Schema.table)
public class StoreRegistrationRequest {

    @PrimaryKey @SerializedName(Schema.id) @ColumnInfo(name = Schema.id)
    public final long id;
    @SerializedName(Schema.storeUserUuid) @ColumnInfo(name = Schema.storeUserUuid)
    public final String storeUserUuid;
    @SerializedName(Schema.storeName) @ColumnInfo(name = Schema.storeName)
    public final String storeName;
    @SerializedName(Schema.storePhoneNumber) @ColumnInfo(name = Schema.storePhoneNumber)
    public final String storePhoneNumber;
    @SerializedName(Schema.storeType) @ColumnInfo(name = Schema.storeType)
    public final String storeType;
    @SerializedName(Schema.corporateRegistrationName) @ColumnInfo(name = Schema.corporateRegistrationName)
    public final String corporateRegistrationName;
    @SerializedName(Schema.corporateRegistrationOwnerName) @ColumnInfo(name = Schema.corporateRegistrationOwnerName)
    public final String corporateRegistrationOwnerName;
    @SerializedName(Schema.corporateRegistrationNumber) @ColumnInfo(name = Schema.corporateRegistrationNumber)
    public final String corporateRegistrationNumber;
    @SerializedName(Schema.corporateRegistrationAddress) @ColumnInfo(name = Schema.corporateRegistrationAddress)
    public final String corporateRegistrationAddress;
    @SerializedName(Schema.corporateRegistrationScanUrl) @ColumnInfo(name = Schema.corporateRegistrationScanUrl)
    public final String corporateRegistrationScanUrl;
    @SerializedName(Schema.latitude) @ColumnInfo(name = Schema.latitude)
    public final double latitude;
    @SerializedName(Schema.longitude) @ColumnInfo(name = Schema.longitude)
    public final double longitude;
    @SerializedName(Schema.address) @ColumnInfo(name = Schema.address)
    public final String address;
    @SerializedName(Schema.coverImageRrl) @ColumnInfo(name = Schema.coverImageRrl)
    public final String coverImageRrl;
    @SerializedName(Schema.thumbnailImageUrl) @ColumnInfo(name = Schema.thumbnailImageUrl)
    public final String thumbnailImageUrl;
    @SerializedName(Schema.createdDate) @ColumnInfo(name = Schema.createdDate)
    public final long createdDate;

    @Expose @Ignore
    public List<StoreRegistrationResult> resultList;

    public StoreRegistrationRequest(final long id, final String storeUserUuid, final String storeName, final String storePhoneNumber, final String storeType, final String corporateRegistrationName, final String corporateRegistrationOwnerName, final String corporateRegistrationNumber, final String corporateRegistrationAddress, final String corporateRegistrationScanUrl, final double latitude, final double longitude, final String address, final String coverImageRrl, final String thumbnailImageUrl, final long createdDate) {
        this.id = id;
        this.storeUserUuid = storeUserUuid;
        this.storeName = storeName;
        this.storePhoneNumber = storePhoneNumber;
        this.storeType = storeType;
        this.corporateRegistrationName = corporateRegistrationName;
        this.corporateRegistrationOwnerName = corporateRegistrationOwnerName;
        this.corporateRegistrationNumber = corporateRegistrationNumber;
        this.corporateRegistrationAddress = corporateRegistrationAddress;
        this.corporateRegistrationScanUrl = corporateRegistrationScanUrl;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.coverImageRrl = coverImageRrl;
        this.thumbnailImageUrl = thumbnailImageUrl;
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return EntityHelper.toString(this);
    }

    public interface Schema {
        String table = "store_registration_request";
        String id = "id";
        String storeUserUuid = "store_user_uuid";
        String storeName = "store_name";
        String storePhoneNumber = "store_phone_number";
        String storeType = "store_type";
        String corporateRegistrationName = "corporate_registration_name";
        String corporateRegistrationOwnerName = "corporate_registration_owner_name";
        String corporateRegistrationNumber = "corporate_registration_number";
        String corporateRegistrationAddress = "corporate_registration_address";
        String corporateRegistrationScanUrl = "corporate_registration_scan_url";
        String latitude = "latitude";
        String longitude = "longitude";
        String address = "address";
        String coverImageRrl = "cover_image_url";
        String thumbnailImageUrl = "thumbnail_image_url";
        String createdDate = "create_date";
    }

    public static class Builder {
        private long id;
        private String storeUserUuid;
        private String storeName;
        private String storePhoneNumber;
        private String storeType;
        private String corporateRegistrationName;
        private String corporateRegistrationOwnerName;
        private String corporateRegistrationNumber;
        private String corporateRegistrationAddress;
        private String corporateRegistrationScanUrl;
        private double latitude;
        private double longitude;
        private String address;
        private String coverImageRrl;
        private String thumbnailImageUrl;
        private long createdDate;

        public Builder() {
        }

        public Builder(StoreRegistrationRequest storeRegistrationRequest) {
            id = storeRegistrationRequest.id;
            storeUserUuid = storeRegistrationRequest.storeUserUuid;
            storeName = storeRegistrationRequest.storeName;
            storePhoneNumber = storeRegistrationRequest.storePhoneNumber;
            storeType = storeRegistrationRequest.storeType;
            corporateRegistrationName = storeRegistrationRequest.corporateRegistrationName;
            corporateRegistrationOwnerName = storeRegistrationRequest.corporateRegistrationOwnerName;
            corporateRegistrationAddress = storeRegistrationRequest.corporateRegistrationAddress;
            corporateRegistrationScanUrl = storeRegistrationRequest.corporateRegistrationScanUrl;
            latitude = storeRegistrationRequest.latitude;
            longitude = storeRegistrationRequest.longitude;
            address = storeRegistrationRequest.address;
            coverImageRrl = storeRegistrationRequest.coverImageRrl;
            thumbnailImageUrl = storeRegistrationRequest.thumbnailImageUrl;
            createdDate = storeRegistrationRequest.createdDate;
        }

        public Builder setCorporateRegistrationAddress(final String corporateRegistrationAddress) {
            this.corporateRegistrationAddress = corporateRegistrationAddress;
            return this;
        }

        public Builder setCorporateRegistrationName(final String corporateRegistrationName) {
            this.corporateRegistrationName = corporateRegistrationName;
            return this;
        }

        public Builder setCorporateRegistrationOwnerName(final String corporateRegistrationOwnerName) {
            this.corporateRegistrationOwnerName = corporateRegistrationOwnerName;
            return this;
        }

        public Builder setCreatedDate(final long createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public Builder setId(final long id) {
            this.id = id;
            return this;
        }

        public Builder setStoreUserUuid(final String storeUserUuid) {
            this.storeUserUuid = storeUserUuid;
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

        public StoreRegistrationRequest build() {

            StoreRegistrationRequest storeRegistrationRequest = new StoreRegistrationRequest(id, storeUserUuid, storeName, storePhoneNumber, storeType, corporateRegistrationName, corporateRegistrationOwnerName, corporateRegistrationNumber, corporateRegistrationAddress, corporateRegistrationScanUrl, latitude, longitude, address, coverImageRrl, thumbnailImageUrl, createdDate);
//            for (Validator validator : Validator.values()) {
//                if (!validator.verify(storeRegistrationRequest)) {
//                    throw new StoreRegistrationException(validator.ofErrorCode());
//                }
//            }
            return storeRegistrationRequest;
        }
    }
}
