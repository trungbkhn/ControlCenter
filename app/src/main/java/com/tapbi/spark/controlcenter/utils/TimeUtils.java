package com.tapbi.spark.controlcenter.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import com.tapbi.spark.controlcenter.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {

    public static String getTimeAutoWithCurrentMini(Context context, int hour, int minute) {
        String timeAuto = "";
        if (hour == 12) {
            timeAuto += hour;
            timeAuto += ":";
            if (minute < 10) {
                timeAuto += "0";
            }
            timeAuto += minute;
            timeAuto += " " + context.getString(R.string.pm);
        } else if (hour == 0) {
            timeAuto += hour + 12;
            timeAuto += ":";
            if (minute < 10) {
                timeAuto += "0";
            }
            timeAuto += minute;
            timeAuto += " " + context.getString(R.string.am);
        } else if (hour > 12) {
            timeAuto += hour - 12;
            timeAuto += ":";
            if (minute < 10) {
                timeAuto += "0";
            }
            timeAuto += minute;
            timeAuto += " " + context.getString(R.string.pm);
        } else {
            timeAuto += hour;
            timeAuto += ":";
            if (minute < 10) {
                timeAuto += "0";
            }
            timeAuto += minute;
            timeAuto += " " + context.getString(R.string.am);
        }
        return timeAuto;

    }

    public static String getTimeAutoCurrentMini(Context context, int hour, int minute, int amPM) {
        String timeAuto = "";
        timeAuto += hour;
        timeAuto += ":";
        if (minute < 10) {
            timeAuto += "0";
        }
        timeAuto += minute;
        if (amPM == 0)
            timeAuto += " " + context.getString(R.string.am);
        if (amPM == 1)
            timeAuto += " " + context.getString(R.string.pm);

        return timeAuto;

    }

    public static String getTimeAutoWithCurrentMini(Context context, Long time) {
        String timeAuto = "";
        if (getHourWithTimeMini(time) == 12) {
            timeAuto += getHourWithTimeMini(time);
            timeAuto += ":";
            if (getMinuteWithTimeMini(time) < 10) {
                timeAuto += "0" + getMinuteWithTimeMini(time);
            } else {
                timeAuto += getMinuteWithTimeMini(time);
            }
            timeAuto += " " + context.getString(R.string.pm);
        } else if (getHourWithTimeMini(time) == 0) {
            timeAuto += getHourWithTimeMini(time) + 12;
            timeAuto += ":";
            if (getMinuteWithTimeMini(time) < 10) {
                timeAuto += "0" + getMinuteWithTimeMini(time);
            } else {
                timeAuto += getMinuteWithTimeMini(time);
            }
            timeAuto += " " + context.getString(R.string.am);
        } else if (getHourWithTimeMini(time) > 12) {
            timeAuto += getHourWithTimeMini(time) - 12;
            timeAuto += ":";
            if (getMinuteWithTimeMini(time) < 10) {
                timeAuto += "0" + getMinuteWithTimeMini(time);
            } else {
                timeAuto += getMinuteWithTimeMini(time);
            }
            timeAuto += " " + context.getString(R.string.pm);
        } else {
            timeAuto += getHourWithTimeMini(time);
            timeAuto += ":";
            if (getMinuteWithTimeMini(time) < 10) {
                timeAuto += "0" + getMinuteWithTimeMini(time);
            } else {
                timeAuto += getMinuteWithTimeMini(time);
            }
            timeAuto += " " + context.getString(R.string.am);
        }
        return timeAuto;
    }

    public static int getHourWithTimeMini(Long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public static int getMinuteWithTimeMini(Long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return calendar.get(Calendar.MINUTE);
    }

    public static Long getTimeWithHourStart(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static Long getTimeWithHourStartRepeat(int hour, int minute, boolean mon, boolean tue, boolean wed, boolean thu, boolean fri, boolean sat, boolean sun) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long time = calendar.getTimeInMillis();
        if (!checkDayOfWeek(mon, tue, wed, thu, fri, sat, sun)) {
            time += getTimeRepeat(mon, tue, wed, thu, fri, sat, sun);
        }
        return time;
    }

    public static Long getTimeWithHourEndRepeat(int hour, int minute, long timeStart, boolean mon, boolean tue, boolean wed, boolean thu, boolean fri, boolean sat, boolean sun) {
        long currentTime = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);
        int hour24hrs = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int millisecond = calendar.get(Calendar.MILLISECOND);

//        if (hour24hrs <= hour) {
//            currentTime = getTimeWithHourStartRepeat(hour, minute,mon,tue,wed,thu,fri,sat,sun);
//        } else {
        if (timeStart > getTimeWithHourStartRepeat(hour, minute, mon, tue, wed, thu, fri, sat, sun)) {

            currentTime += (24 - hour24hrs) * 60 * 60 * 1000 + hour * 60 * 60 * 1000 + minute * 60 * 1000 - minutes * 60 * 1000 - second * 1000 - millisecond;

        } else {
            currentTime = getTimeWithHourStartRepeat(hour, minute, mon, tue, wed, thu, fri, sat, sun);
        }
//        }
        if (!checkDayOfWeek(mon, tue, wed, thu, fri, sat, sun)) {
            currentTime += getTimeRepeat(mon, tue, wed, thu, fri, sat, sun);
        }
        return currentTime;
    }

    public static Long getTimeWithHourEnd(int hour, int minute, long timeStart) {
        long currentTime = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);
        int hour24hrs = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int millisecond = calendar.get(Calendar.MILLISECOND);

        if (timeStart > getTimeWithHourStart(hour, minute)) {

            currentTime += (24 - hour24hrs) * 60 * 60 * 1000 + hour * 60 * 60 * 1000 + minute * 60 * 1000 - minutes * 60 * 1000 - second * 1000 - millisecond;

        } else {
            currentTime = getTimeWithHourStart(hour, minute);
        }
        return currentTime;
    }

    public static String getDay(boolean mon, boolean tue, boolean wed, boolean thu, boolean fri, boolean sat, boolean sun, Context context) {
        String day = "";
        if (mon) day += context.getString(R.string.Monday) + ", ";
        if (tue) day += context.getString(R.string.Tuesday) + ", ";
        if (wed) day += context.getString(R.string.Wednesday) + ", ";
        if (thu) day += context.getString(R.string.Thursday) + ", ";
        if (fri) day += context.getString(R.string.Friday) + ", ";
        if (sat) day += context.getString(R.string.Saturday) + ", ";
        if (sun) day += context.getString(R.string.Sunday) + ", ";

        if (!day.isEmpty())
            day = day.substring(0, day.length() - 2);
        return day;
    }

    public static Boolean checkDayOfWeek(Boolean mont, Boolean tue, Boolean wed, Boolean third, Boolean fri, Boolean sat, Boolean sun) {
        switch (getDayOfWeekToday()) {
            case 1:
                return sun;
            case 2:
                return mont;
            case 3:
                return tue;
            case 4:
                return wed;
            case 5:
                return third;
            case 6:
                return fri;
            case 7:
                return sat;

        }
        return false;
    }

    public static Boolean checkNoRepeat(Boolean mont, Boolean tue, Boolean wed, Boolean third, Boolean fri, Boolean sat, Boolean sun) {
        return !mont && !tue && !wed && !third && !fri && !sat && !sun;
    }

    private static int getDayOfWeekToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    public static long getTimeRepeat(Boolean mont, Boolean tue, Boolean wed, Boolean third, Boolean fri, Boolean sat, Boolean sun) {
        switch (getDayOfWeekToday()) {
            case 1:
                if (mont) return 24 * 60 * 60 * 1000;
                else if (tue) return 24 * 2 * 60 * 60 * 1000;
                else if (wed) return 24 * 3 * 60 * 60 * 1000;
                else if (third) return 24 * 4 * 60 * 60 * 1000;
                else if (fri) return 24 * 5 * 60 * 60 * 1000;
                else if (sat) return 24 * 6 * 60 * 60 * 1000;
                else if (sun) return 24 * 7 * 60 * 60 * 1000;
                else return 0;
            case 2:
                if (tue) return 24 * 60 * 60 * 1000;
                else if (wed) return 24 * 2 * 60 * 60 * 1000;
                else if (third) return 24 * 3 * 60 * 60 * 1000;
                else if (fri) return 24 * 4 * 60 * 60 * 1000;
                else if (sat) return 24 * 5 * 60 * 60 * 1000;
                else if (sun) return 24 * 6 * 60 * 60 * 1000;
                else if (mont) return 24 * 7 * 60 * 60 * 1000;
                else return 0;
            case 3:
                if (wed) return 24 * 1 * 60 * 60 * 1000;
                else if (third) return 24 * 2 * 60 * 60 * 1000;
                else if (fri) return 24 * 3 * 60 * 60 * 1000;
                else if (sat) return 24 * 4 * 60 * 60 * 1000;
                else if (sun) return 24 * 5 * 60 * 60 * 1000;
                else if (mont) return 24 * 6 * 60 * 60 * 1000;
                else if (tue) return 24 * 7 * 60 * 60 * 1000;
                else return 0;
            case 4:
                if (third) return 24 * 1 * 60 * 60 * 1000;
                else if (fri) return 24 * 2 * 60 * 60 * 1000;
                else if (sat) return 24 * 3 * 60 * 60 * 1000;
                else if (sun) return 24 * 4 * 60 * 60 * 1000;
                else if (mont) return 24 * 5 * 60 * 60 * 1000;
                else if (tue) return 24 * 6 * 60 * 60 * 1000;
                else if (wed) return 24 * 7 * 60 * 60 * 1000;
                else return 0;
            case 5:
                if (fri) return 24 * 1 * 60 * 60 * 1000;
                else if (sat) return 24 * 2 * 60 * 60 * 1000;
                else if (sun) return 24 * 3 * 60 * 60 * 1000;
                else if (mont) return 24 * 4 * 60 * 60 * 1000;
                else if (tue) return 24 * 5 * 60 * 60 * 1000;
                else if (wed) return 24 * 6 * 60 * 60 * 1000;
                else if (third) return 24 * 7 * 60 * 60 * 1000;
                else return 0;
            case 6:
                if (sat) return 24 * 1 * 60 * 60 * 1000;
                else if (sun) return 24 * 2 * 60 * 60 * 1000;
                else if (mont) return 24 * 3 * 60 * 60 * 1000;
                else if (tue) return 24 * 4 * 60 * 60 * 1000;
                else if (wed) return 24 * 5 * 60 * 60 * 1000;
                else if (third) return 24 * 6 * 60 * 60 * 1000;
                else if (fri) return 24 * 7 * 60 * 60 * 1000;
                else return 0;
            case 7:
                if (sun) return 24 * 1 * 60 * 60 * 1000;
                else if (mont) return 24 * 2 * 60 * 60 * 1000;
                else if (tue) return 24 * 3 * 60 * 60 * 1000;
                else if (wed) return 24 * 4 * 60 * 60 * 1000;
                else if (third) return 24 * 5 * 60 * 60 * 1000;
                else if (fri) return 24 * 6 * 60 * 60 * 1000;
                else if (sat) return 24 * 7 * 60 * 60 * 1000;
                else return 0;

        }
        return 0;
    }

    public static boolean isEvening() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH");
        Date date = new Date(System.currentTimeMillis());
        String hour = simpleDateFormat.format(date);
        return Integer.parseInt(hour) >= 19;
    }

    public static int currentHour(long time) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH");
        Date date = new Date(time);
        String hour = simpleDateFormat.format(date);
        return Integer.parseInt(hour);
    }

    public static int currentMinute(long time) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm");
        Date date = new Date(time);
        String hour = simpleDateFormat.format(date);
        return Integer.parseInt(hour);
    }

    public static String formatTOHourAndMinute(long time) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm aa");
        Date date = new Date(time);
        return simpleDateFormat.format(date);
    }

    public static String formatToFull(long time) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss aa");
        Date date = new Date(time);
        return simpleDateFormat.format(date);
    }
}
