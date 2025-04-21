package com.tapbi.spark.controlcenter.utils.asynctask;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.CalendarContract;


import com.tapbi.spark.controlcenter.feature.controlios14.model.EventCalendarUpNext;
import com.tapbi.spark.controlcenter.utils.MethodUtils;

import java.util.Calendar;

public class LoadEventNextUp extends AsyncTask<Void, EventCalendarUpNext, EventCalendarUpNext> {

    private Context context;
    private OnLoadEventNextUpListener onLoadEventNextUpListener;

    public LoadEventNextUp(Context context, OnLoadEventNextUpListener onLoadEventNextUpListener) {
        this.context = context;
        this.onLoadEventNextUpListener = onLoadEventNextUpListener;
    }

    @Override
    protected EventCalendarUpNext doInBackground(Void... voids) {
        EventCalendarUpNext eventCalendarUpNext = new EventCalendarUpNext();
        Calendar calendar = Calendar.getInstance();
        long after = calendar.getTimeInMillis();
        Uri calendarURI = Uri.parse("content://com.android.calendar/events");
        try (Cursor cursor = context.getContentResolver().query(calendarURI,
                (new String[]{CalendarContract.Events._ID,
                        CalendarContract.Events.TITLE,
                        CalendarContract.Events.DESCRIPTION,
                        CalendarContract.Events.DTSTART,
                        CalendarContract.Events.DTEND,
                        CalendarContract.Events.EVENT_COLOR}),
                "(" + CalendarContract.Events.DTSTART + ">=" + after + ")", null, "dtstart ASC")) {
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();

                eventCalendarUpNext.setId(cursor.getString(0));
                eventCalendarUpNext.setName(cursor.getString(1));
                eventCalendarUpNext.setDescription(cursor.getString(2));
                eventCalendarUpNext.setStartAt(MethodUtils.getDateEvent(cursor.getLong(3)));
                eventCalendarUpNext.setEndAt(MethodUtils.getDateEvent(cursor.getLong(4)));
                eventCalendarUpNext.setColor(cursor.getInt(5));
                return eventCalendarUpNext;
            }
        } catch (Exception ignored){}

        return null;
    }

    @Override
    protected void onPostExecute(EventCalendarUpNext eventCalendarUpNext) {
        super.onPostExecute(eventCalendarUpNext);
        if (onLoadEventNextUpListener != null) {
            onLoadEventNextUpListener.onLoadSuccess(eventCalendarUpNext);
        }
    }

    public interface OnLoadEventNextUpListener {
        void onLoadSuccess(EventCalendarUpNext eventCalendarUpNext);
    }
}
