/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.server.store.view.order.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by mark on 18. 5. 17.
 */
public class CenteringTabLayout extends TabLayout {

    public CenteringTabLayout(Context context) {
        super(context);
    }

    public CenteringTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CenteringTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        View firstTab = ((ViewGroup) getChildAt(0)).getChildAt(0);
        View lastTab = ((ViewGroup) getChildAt(0)).getChildAt(((ViewGroup) getChildAt(0)).getChildCount() - 1);
        if (firstTab == null || lastTab == null) {
            return;
        }
        ViewCompat.setPaddingRelative(getChildAt(0), (getWidth() / 4) - (firstTab.getWidth() / 2), 0, (getWidth() * 3 / 4) - (lastTab.getWidth() / 2), 0);
    }

    @Nullable
    @Override
    public Tab getTabAt(final int index) {
        Tab tabAt = super.getTabAt(index);
        return tabAt;
    }
}
