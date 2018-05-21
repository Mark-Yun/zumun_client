package com.mark.zumo.client.core.dao;

import android.arch.persistence.room.TypeConverter;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by mark on 18. 4. 30.
 */

public class Converters {
    @TypeConverter
    public static List<Long> fromString(String value) {
        List<Long> arrayList = new ArrayList<>();

        String[] splitString = value.split(",");
        for (String token : splitString) {
            if (token.isEmpty()) continue;
            arrayList.add(Long.parseLong(token));
        }

        return arrayList;
    }

    @TypeConverter
    public static String fromList(List<Long> list) {
        String ret = "";
        for (long item : list) {
            ret += item + ",";
        }
        return ret;
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
