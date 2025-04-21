package com.tapbi.spark.controlcenter.feature.controlios14.view.noty.widget;

import static com.tapbi.spark.controlcenter.common.Constant.LIGHT;
import static com.tapbi.spark.controlcenter.common.Constant.STYLE_SELECTED;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.tapbi.spark.controlcenter.App;
import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.eventbus.EventCalendar;
import com.tapbi.spark.controlcenter.feature.controlios14.manager.PermissionManager;
import com.tapbi.spark.controlcenter.feature.controlios14.model.EventCalendarUpNext;
import com.tapbi.spark.controlcenter.feature.controlios14.view.noty.ImageBackgroundView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.noty.MaskView;
import com.tapbi.spark.controlcenter.utils.asynctask.LoadEventNextUp;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import timber.log.Timber;

public class EventCalendarView extends MaskView {

    private Context context;

    private ImageBackgroundView background;
    private TextView tvPermissionCalendar;
    private Group containerEvent;
    private TextView tvTitle, tvDes, tvTime;
    private View color;
    private EventCalendarUpNext eventUpNext;
    private View colorTitleBlur;
    private View colorContentBlur;

    private boolean mRegistered = false;

    public EventCalendarView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public EventCalendarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }    private ContentObserver mObserver = new ContentObserver(null) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            updateEvent();
        }

        @Override
        public void onChange(boolean selfChange) {

        }
    };

    public EventCalendarView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setRegistered();
        EventBus.getDefault().register(this);
    }

    public void setSetColorBlur() {
        int colorTitle;
        int colorContent;
        if (App.tinyDB.getInt(STYLE_SELECTED, LIGHT) == 0) {
            colorContent = R.color.colorBackgroundContentWidget;
            colorTitle = R.color.colorBackgroundTopWidget;
        } else {
            colorContent = R.color.color_background_item_dark;
            colorTitle = R.color.color_background_item;
        }

        colorTitleBlur.setBackgroundColor(colorTitle);
        colorContentBlur.setBackgroundColor(colorContent);
    }

    private void setRegistered() {
        try {
            if (PermissionManager.getInstance().checkPermission(context, Manifest.permission.READ_CALENDAR)) {
                if (!mRegistered) {
                    context.getContentResolver().registerContentObserver(
                            CalendarContract.Events.CONTENT_URI,
                            true,
                            mObserver
                    );
                    mRegistered = true;
                }
            }
        } catch (Exception e) {
            mRegistered = false;
        }
    }

    public void setBg() {
        setViewBackground(background);
    }

    private void init(Context context) {
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.view_event_calendar, this, true);

        background = findViewById(R.id.background);
//        setBg();
        tvPermissionCalendar = findViewById(R.id.tvPermissionCalendar);
        colorTitleBlur = findViewById(R.id.colorTitleBlur);
        colorContentBlur = findViewById(R.id.colorContentBlur);
        containerEvent = findViewById(R.id.containerEvent);
        tvTitle = findViewById(R.id.tvTitle);
        tvDes = findViewById(R.id.tvDes);
        tvTime = findViewById(R.id.tvTime);
        color = findViewById(R.id.color);

        if (!PermissionManager.getInstance().checkPermission(context, Manifest.permission.READ_CALENDAR)) {
            tvPermissionCalendar.setText(context.getString(R.string.access_permission_calendar));
            tvPermissionCalendar.setVisibility(VISIBLE);
            containerEvent.setVisibility(GONE);
            tvPermissionCalendar.setOnClickListener(onClickListener);
            color.setVisibility(GONE);
        }
    }

    public void updateEvent() {
        if (PermissionManager.getInstance().checkPermission(context, Manifest.permission.READ_CALENDAR)) {
            colorContentBlur.setOnClickListener(onClickListener);
            tvPermissionCalendar.setOnClickListener(null);

            new LoadEventNextUp(context, eventCalendarUpNext -> {
                eventUpNext = eventCalendarUpNext;
                if (eventCalendarUpNext != null && eventCalendarUpNext.getName() != null) {
                    tvPermissionCalendar.setVisibility(GONE);
                    containerEvent.setVisibility(VISIBLE);

                    tvTitle.setText(eventCalendarUpNext.getName());
                    Timber.e("hachung eventCalendarUpNext.getName:  " + eventCalendarUpNext.getName());
                    if (eventCalendarUpNext.getDescription() != null && !eventCalendarUpNext.getDescription().equals("")) {
                        tvDes.setVisibility(VISIBLE);
                        tvDes.setText(eventCalendarUpNext.getDescription());
                    } else {
                        tvDes.setVisibility(GONE);
                    }
                    tvTime.setText(context.getString(R.string.from) + " " + eventCalendarUpNext.getStartAt() + " " + context.getString(R.string.to) + " " + eventCalendarUpNext.getEndAt());
                    color.setVisibility(VISIBLE);
                    Timber.e("hoangld: " + eventCalendarUpNext.toString() + " ------- " + (0xff000000 + eventCalendarUpNext.getColor()));
                    color.setBackgroundColor(0xff000000 + eventCalendarUpNext.getColor());
                } else {
                    tvPermissionCalendar.setVisibility(VISIBLE);
                    containerEvent.setVisibility(GONE);
                    tvPermissionCalendar.setText(R.string.no_event_today_widget);
                }
            }).execute();
        }
    }

    @Subscribe
    public void onEVentCalendar(EventCalendar eventCalendar) {
        setRegistered();
        updateEvent();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
        try {
            if (mRegistered) {
                context.getContentResolver().unregisterContentObserver(mObserver);
            }
        } catch (Exception e) {

        }
    }




    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == tvPermissionCalendar) {
                if (PermissionManager.getInstance().checkPermission(context, Manifest.permission.READ_CALENDAR)) {
                    updateEvent();
                } else {
                    //todo: xin quyen
                    Intent intent = new Intent(Constant.ACTION_OPEN_APP);
                    intent.putExtra(Constant.PACKAGE_NAME_APP_OPEN, Constant.REQUEST_PERMISSION_CALENDAR);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
            } else if (v == colorContentBlur) {
                //todo: intent lich
                if (eventUpNext != null) {
                    Intent intent = new Intent(Constant.ACTION_OPEN_APP);
                    intent.putExtra(Constant.PACKAGE_NAME_APP_OPEN, Constant.OPEN_EVENT_NEXT_UP);
                    intent.putExtra(Constant.ID_EVENT_NEXT_UP, eventUpNext.getId());
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
            }
        }
    };


}

