/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.mark.zumo.client.core.appserver.request;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by mark on 18. 8. 10.
 */
public class RequestUpdateMenusOfCategory {

    @SerializedName(Schema.storeUuid)
    public String storeUuid;
    @SerializedName(Schema.menuUuidList)
    public List<String> menuCategoryUuidList;

    public RequestUpdateMenusOfCategory(final String storeUuid, final List<String> menuCategoryUuidList) {
        this.storeUuid = storeUuid;
        this.menuCategoryUuidList = menuCategoryUuidList;
    }

    public interface Schema {
        String storeUuid = "store_uuid";
        String menuUuidList = "menu_uuid_list";
    }
}
