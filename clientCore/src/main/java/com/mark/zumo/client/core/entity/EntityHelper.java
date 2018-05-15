package com.mark.zumo.client.core.entity;

import java.lang.reflect.Field;

/**
 * Created by mark on 18. 4. 30.
 */

public class EntityHelper {

    public static String toString(Object object, Class clazz) {
        StringBuilder ret = new StringBuilder(clazz.getSimpleName() + "[");
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            String name = field.getName();
            try {
                ret.append(name).append("=").append(field.get(object)).append(", ");
            } catch (IllegalAccessException ignored) {
            }
        }

        return ret + "]";
    }
}
