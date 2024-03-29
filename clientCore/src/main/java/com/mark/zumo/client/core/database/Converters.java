/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.database;

import android.arch.persistence.room.TypeConverter;
import android.support.annotation.NonNull;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by mark on 18. 4. 30.
 */

public class Converters {
    @TypeConverter
    public static List<Long> longListfromString(String value) {
        List<Long> arrayList = new ArrayList<>();

        String[] splitString = parseListString(value);

        for (String token : splitString) {
            if (token.isEmpty()) continue;
            arrayList.add(Long.parseLong(token));
        }
        return arrayList;
    }

    @TypeConverter
    public static List<String> stringListfromString(String value) {
        List<String> arrayList = new ArrayList<>();

        String[] splitString = parseListString(value);

        for (String token : splitString) {
            if (token.isEmpty()) continue;
            arrayList.add(token);
        }
        return arrayList;
    }

    @NonNull
    private static String[] parseListString(final String value) {
        return value.replaceAll("\\[", "")
                .replaceAll("\\]", "")
                .split(",");
    }

    @TypeConverter
    public static String fromLongList(List<Long> list) {
        StringBuilder ret = new StringBuilder("[");
        for (long item : list) {
            ret.append(item).append(",");
        }
        return ret + "]";
    }

    @TypeConverter
    public static String fromStringList(List<String> list) {
        StringBuilder ret = new StringBuilder("[");
        for (String item : list) {
            ret.append(item).append(",");
        }
        return ret + "]";
    }

    @TypeConverter
    public static UUID fromBinary(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        long firstLong = bb.getLong();
        long secondLong = bb.getLong();
        return new UUID(firstLong, secondLong);
    }

    @TypeConverter
    public static byte[] fromUuid(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }
}
