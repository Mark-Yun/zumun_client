/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.appserver.request;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by mark on 18. 8. 12.
 */
public class CategoryOrderRequest {

    @SerializedName(MenuCategoryUpdateRequest.Schema.storeUuid)
    public String storeUuid;
    @SerializedName(MenuCategoryUpdateRequest.Schema.menuCategoryUuidList)
    public List<String> menuCategoryUuidList;

    public CategoryOrderRequest(final String storeUuid, final List<String> menuCategoryUuidList) {
        this.storeUuid = storeUuid;
        this.menuCategoryUuidList = menuCategoryUuidList;
    }

    public interface Schema {
        String storeUuid = "store_uuid";
        String menuCategoryUuidList = "menu_category_uuid_list";
        String menuUuid = "menu_uuid";
    }
}
