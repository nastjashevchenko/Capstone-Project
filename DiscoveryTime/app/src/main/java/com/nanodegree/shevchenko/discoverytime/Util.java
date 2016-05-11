package com.nanodegree.shevchenko.discoverytime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Util {
    public static String longDateToString(long dateInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy", Locale.US);
        return dateFormat.format(new Date(dateInMillis));
    }

    public static String getDateByDayNumber(long startDate, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startDate);
        calendar.add(Calendar.DATE, day);
        return longDateToString(calendar.getTimeInMillis());
    }
}
