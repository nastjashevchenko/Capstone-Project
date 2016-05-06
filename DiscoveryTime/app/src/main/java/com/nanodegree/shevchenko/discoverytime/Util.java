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
        // TODO refactor this method
        List<String> dates = new ArrayList<>();
        dates.add("Not planned yet");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startDate);
        int i = 1;

        while (calendar.getTimeInMillis() < endDate) {
            String result = longDateToString(calendar.getTimeInMillis());
            dates.add("Day " + String.valueOf(i) + "        " + result);
            calendar.add(Calendar.DATE, 1);
            i++;
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
