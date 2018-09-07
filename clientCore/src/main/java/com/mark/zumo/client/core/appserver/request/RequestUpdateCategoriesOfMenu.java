/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.appserver.request;

import com.google.gson.annotations.SerializedName;

import java.util.Set;

/**
 * Created by mark on 18. 8. 10.
 */
public class RequestUpdateCategoriesOfMenu {

    @SerializedName(Schema.storeUuid)
    public String storeUuid;
    @SerializedName(Schema.menuCategoryUuidList)
    public Set<String> menuCategoryUuidList;

    public RequestUpdateCategoriesOfMenu(final String storeUuid, final Set<String> menuCategoryUuidList) {
        this.storeUuid = storeUuid;
        this.menuCategoryUuidList = menuCategoryUuidList;
    }

    public interface Schema {
        String storeUuid = "store_uuid";
        String menuCategoryUuidList = "menu_category_uuid_list";
        String menuUuid = "menu_uuid";
    }
}
