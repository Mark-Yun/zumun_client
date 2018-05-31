package com.mark.zumo.client.core.dao;

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
        String ret = "[";
        for (long item : list) {
            ret += item + ",";
        }
        return ret + "]";
    }

    @TypeConverter
    public static String fromStringList(List<String> list) {
        String ret = "[";
        for (String item : list) {
            ret += item + ",";
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
