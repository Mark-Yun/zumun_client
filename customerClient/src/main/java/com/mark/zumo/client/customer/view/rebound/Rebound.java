/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.customer.view.rebound;

import android.arch.lifecycle.GenericLifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.view.MotionEvent;
import android.view.View;

import com.facebook.rebound.BaseSpringSystem;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.facebook.rebound.SpringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mark on 18. 6. 2.
 */
public enum Rebound {
    INSTANCE;

    private static final SpringConfig CONFIG = SpringConfig.fromOrigamiTensionAndFriction(60, 8);
    private final BaseSpringSystem mSpringSystem = SpringSystem.create();
    private final Map<View, Spring> mSpringMap;

    Rebound() {
        mSpringMap = new HashMap<>();
    }

    public View.OnTouchListener onTouchListener(LifecycleOwner lifecycleOwner) {
        return new View.OnTouchListener() {
            private Spring mScaleSpring;
            private ScaleSpringListener mSpringListener;

            @Override
            public boolean onTouch(final View v, final MotionEvent motionEvent) {
                if (mScaleSpring == null) {
                    mScaleSpring = mSpringMap.computeIfAbsent(v, unused -> mSpringSystem.createSpring().setSpringConfig(CONFIG));
                    mSpringListener = new ScaleSpringListener(v);

                    lifecycleOwner.getLifecycle().addObserver((GenericLifecycleObserver) (source, event) -> {
                        switch (event) {
                            case ON_RESUME:
                                mScaleSpring.addListener(mSpringListener);
                                if (v.getScaleX() != 1 || v.getScaleY() != 1) {
                                    mScaleSpring.setCurrentValue(1);
                                    mScaleSpring.setEndValue(0);
                                }
                                break;

                            case ON_PAUSE:
                                mScaleSpring.removeListener(mSpringListener);
                                break;
                        }
                    });
                }
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // When pressed start solving the spring to 1.
                        mScaleSpring.setEndValue(1);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        // When released start solving the spring to 0.
                        mScaleSpring.setEndValue(0);
                        break;
                }
                return false;
            }
        };
    }

    private float getMappedValue(final double value) {
        return (float) SpringUtil.mapValueFromRangeToRange(value,
                0, 1,
                1, 0.8
        );
    }

    private class ScaleSpringListener extends SimpleSpringListener {
        private final View view;

        private ScaleSpringListener(View view) {
            this.view = view;
        }

        @Override
        public void onSpringUpdate(Spring spring) {
            // On each update of the spring value, we adjust the scale of the image view to match the
            // springs new value. We use the SpringUtil linear interpolation function mapValueFromRangeToRange
            // to translate the spring's 0 to 1 scale to a 100% to 50% scale range and apply that to the View
            // with setScaleX/Y. Note that rendering is an implementation detail of the application and not
            // Rebound itself. If you need Gingerbread compatibility consider using NineOldAndroids to update
            // your view properties in a backwards compatible manner.
            float mappedValue = getMappedValue(spring.getCurrentValue());

            view.setScaleX(mappedValue);
            view.setScaleY(mappedValue);
        }
    }

}
