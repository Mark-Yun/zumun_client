package com.mark.zumo.client.core.entity;

import android.util.Log;

import java.lang.reflect.Field;

/**
 * Created by mark on 18. 4. 30.
 */

public class EntityHelper {

    private static final String TAG = "EntityHelper";

    public static String toString(Object object, Class clazz) {
        String ret = clazz.getSimpleName() + " [";
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            String name = field.getName();
            Object value = null;
            try {
                value = field.get(object);
            } catch (IllegalAccessException e) {
                Log.e(TAG, "toString: ", e);
            }
            ret += name + "=" + value + ", ";
        }

        return ret + "]";
    }
}
