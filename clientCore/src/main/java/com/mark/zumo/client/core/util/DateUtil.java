/*
 * Copyright (c) 2018. Mark Soft - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.mark.zumo.client.core.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by mark on 18. 7. 11.
 */
public final class DateUtil {
    public static long getLocalTimeMills(long utcTimeInMills) {
        TimeZone currentTimeZone = TimeZone.getDefault();
        int offset = currentTimeZone.getRawOffset();
        return utcTimeInMills - offset;
    }

    public static String getLocalDate(long utcTimeInMills) {
        Date localDate = new Date(utcTimeInMills);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd (EEE)", Locale.getDefault());
        return dateFormat.format(localDate);
    }

    public static String getLocalTime(long utcTimeInMills) {
        Date localDate = new Date(utcTimeInMills);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return dateFormat.format(localDate);
    }

    public static String getLocalFormattedTime(long utcTimeInMills) {
        Date localDate = new Date(utcTimeInMills);
        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd a hh:mm", Locale.getDefault());
        return dateFormat.format(localDate);
    }

    public static String getLocalSimpleTime(long utcTimeInMills) {
        Date localDate = new Date(utcTimeInMills);
        return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault()).format(localDate);
    }
}
