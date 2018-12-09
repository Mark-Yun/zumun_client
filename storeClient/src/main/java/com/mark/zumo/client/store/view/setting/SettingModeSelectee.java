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

/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.store.view.setting;

/**
 * Created by mark on 18. 11. 22.
 */
public interface SettingModeSelectee {
    SettingMode getMode();
    void setMode(SettingMode mode);

    enum SettingMode {
        NONE,
        EDIT_MODE,
        REORDER_MODE,
        DELETE_MODE;

        public boolean isEditMode() {
            return this.equals(EDIT_MODE);
        }

        public boolean isReorderMode() {
            return this.equals(REORDER_MODE);
        }

        public boolean isDeleteMode() {
            return this.equals(DELETE_MODE);
        }

        public boolean isNone() {
            return this.equals(NONE);
        }
    }
}
