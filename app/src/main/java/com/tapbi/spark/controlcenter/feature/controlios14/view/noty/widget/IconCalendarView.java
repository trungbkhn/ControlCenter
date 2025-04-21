package com.tapbi.spark.controlcenter.feature.controlios14.view.noty.widget;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.tapbi.spark.controlcenter.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class IconCalendarView extends LinearLayout {

    private Context context;
    private TextView tvDayOfWeek;
    private TextView tvDayOfMonth;

    public IconCalendarView(Context context) {
        super(context);
        init(context);
    }

    public IconCalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public IconCalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.view_icon_calendar, this, true);
        tvDayOfWeek = findViewById(R.id.tvDayOfWeek);
        tvDayOfMonth = findViewById(R.id.tvDayOfMonth);
        update();
    }

    public void update() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dayofWeek = new SimpleDateFormat("EEE");
        SimpleDateFormat dayofMonth = new SimpleDateFormat("dd");
        tvDayOfWeek.setText(dayofWeek.format(cal.getTime()));
        tvDayOfMonth.setText(dayofMonth.format(cal.getTime()));
    }
}

