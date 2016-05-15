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

    public static String getDateByDayNumber(long startDate, int day) {
        if (day == 0) return "";
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startDate);
        calendar.add(Calendar.DATE, day - 1);
        return longDateToString(calendar.getTimeInMillis());
    }

    public static List<String> getAllDates(long startDate, long endDate,
                                           String notPlanned, String dayDateTmpl) {
        List<String> dates = new ArrayList<>();
        dates.add(notPlanned);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startDate);
        int i = 1;

        while (calendar.getTimeInMillis() < endDate) {
            String result = Util.longDateToString(calendar.getTimeInMillis());
            dates.add(String.format(dayDateTmpl, i, result));
            calendar.add(Calendar.DATE, 1);
            i++;
        }
        return dates;
    }
}
