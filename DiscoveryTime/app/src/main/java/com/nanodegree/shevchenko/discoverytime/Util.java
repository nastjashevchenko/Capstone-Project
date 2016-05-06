package com.nanodegree.shevchenko.discoverytime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Util {
    public static String longDateToString(long dateInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy", Locale.US);
        return dateFormat.format(new Date(dateInMillis));
    }

    public static List<String> getDaysBetweenDates(long startDate, long endDate) {
        List<String> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startDate);

        while (calendar.getTimeInMillis() < endDate) {
            String result = longDateToString(calendar.getTimeInMillis());
            dates.add(result);
            calendar.add(Calendar.DATE, 1);
        }
        return dates;
    }

    public static String getDateByDayNumber(long startDate, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startDate);
        calendar.add(Calendar.DATE, day);
        return longDateToString(calendar.getTimeInMillis());
    }
}
