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

package com.mark.zumo.client.store.view.setting.fragment.category;

/**
 * Created by mark on 18. 11. 22.
 */
interface MenuCategorySettingModeSelectee {
    SettingMode getMode();
    void setMode(SettingMode mode);

    enum SettingMode {
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
