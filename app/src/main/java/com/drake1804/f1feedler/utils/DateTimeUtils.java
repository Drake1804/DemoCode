package com.drake1804.f1feedler.utils;

import android.support.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by stanislav.perchenko on 4/8/2016.
 */
public class DateTimeUtils {

    /**********************************************************************************************/
    private static final SimpleDateFormat SERVER_TIME_FORMAT = new SimpleDateFormat("EEE, dd MM yy HH:mm:ss ZZZZ", Locale.US);

    public static Date parseServerTime(String time) throws ParseException {
        synchronized (SERVER_TIME_FORMAT) {
            return SERVER_TIME_FORMAT.parse(time);
        }
    }

    public static String formatServerTime(@NonNull Date d) {
        synchronized (SERVER_TIME_FORMAT) {
            return SERVER_TIME_FORMAT.format(d);
        }
    }




    public static String formatUiDateFull(Date d) {
        if (d != null) {
            return String.format("%1$tF %1$tT", d);
        } else {
            return null;
        }
    }

    public static Date stringToDate(String srt){
        Date date = null;
        try {
            date = SERVER_TIME_FORMAT.parse(srt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

}
