/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.setting.fragment.option;

/**
 * Created by mark on 18. 11. 22.
 */
interface MenuOptionSettingModeSelectee {
    MenuSettingMode getMode();
    void setMode(MenuSettingMode mode);

    enum MenuSettingMode {
        NONE,
        EDIT_MODE,
        REORDER_MODE,
        DELETE_MODE;

        boolean isEditMode() {
            return this.equals(EDIT_MODE);
        }

        boolean isReorderMode() {
            return this.equals(REORDER_MODE);
        }

        boolean isDeleteMode() {
            return this.equals(DELETE_MODE);
        }

        boolean isNone() {
            return this.equals(NONE);
        }
    }
}
