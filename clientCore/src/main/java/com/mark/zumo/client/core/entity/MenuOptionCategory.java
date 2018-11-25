/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mark.zumo.client.core.entity.util.EntityComparator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mark on 18. 6. 27.
 */

@Entity(tableName = MenuOptionCategory.Schema.table)
public class MenuOptionCategory {

    @PrimaryKey @NonNull @ColumnInfo(name = Schema.uuid) @SerializedName(Schema.uuid)
    public final String uuid;
    @ColumnInfo(name = Schema.name) @SerializedName(Schema.name)
    public final String name;
    @ColumnInfo(name = Schema.storeUuid) @SerializedName(Schema.storeUuid)
    public final String storeUuid;
    @Ignore @Expose(deserialize = false, serialize = false)
    public List<MenuOption> optionList;

    public MenuOptionCategory(@NonNull final String uuid, final String name, final String storeUuid) {
        this.uuid = uuid;
        this.name = name;
        this.storeUuid = storeUuid;
        optionList = new ArrayList<>();
    }

    @Override
    public boolean equals(final Object obj) {
        return new EntityComparator<>().test(this, obj);
    }

    public interface Schema {
        String table = "menu_option_category";
        String uuid = "menu_option_category_uuid";
        String name = "menu_option_category_name";
        String storeUuid = "store_uuid";
    }
}
