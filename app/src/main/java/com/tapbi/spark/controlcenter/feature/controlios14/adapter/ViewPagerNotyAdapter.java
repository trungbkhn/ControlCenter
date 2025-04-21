package com.tapbi.spark.controlcenter.feature.controlios14.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.tapbi.spark.controlcenter.feature.controlios14.view.PermissionNotificationView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.noty.NotyView2;
import com.tapbi.spark.controlcenter.feature.controlios14.view.noty.WidgetView;

public class ViewPagerNotyAdapter extends PagerAdapter {
    public WidgetView widgetView;
    public NotyView2 notyView2;
    public ConstraintLayout viewBlack;
    private final Context context;
    private final ViewGroup containerView;
    private final OnNotyCenterListener onNotyCenterListener;
    private PermissionNotificationView.ClickListener clickListener;

    public ViewPagerNotyAdapter(Context context, ViewGroup container, OnNotyCenterListener onNotyCenterListener, PermissionNotificationView.ClickListener clickListener) {
        this.context = context;
        this.containerView = container;
        this.onNotyCenterListener = onNotyCenterListener;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ConstraintLayout rootView;
        switch (position) {
            case 0:
                if (widgetView == null) {
                    widgetView = new WidgetView(context);
                }
                widgetView.setViewGroup(containerView);
                widgetView.setOnWidgetListener(new WidgetView.OnWidgetListener() {
                    @Override
                    public void onOpenSearch() {
                        if (onNotyCenterListener != null) {
                            onNotyCenterListener.onOpenSearch();
                        }
                    }

                    @Override
                    public void onCloseSearch() {
                        if (onNotyCenterListener != null) {
                            onNotyCenterListener.onCloseSearch();
                        }
                    }
                });
                rootView = widgetView;
                break;
            case 1:
                if (notyView2 == null) {
                    notyView2 = new NotyView2(context, onNotyCenterListener, clickListener);
                }
                rootView = notyView2;
                break;
            case 2:
            default:
                if (viewBlack == null) {
                    viewBlack = new ConstraintLayout(context);
                    viewBlack.setBackgroundColor(Color.BLACK);
                }
                rootView = viewBlack;
                break;
        }
        container.addView(rootView);
        return rootView;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        try {
            container.removeView((View) object);
        } catch (Exception e){}
    }

    public void translation(int positionOffsetPixels) {
        if (widgetView != null) {
            widgetView.translation(positionOffsetPixels);
        }
    }

    public void updateAppRecent() {
        if (widgetView != null) {
            widgetView.updateAppRecent();
        }
    }

    public void updateEvent() {
        if (widgetView != null) {
            widgetView.updateEvent();
        }
    }

    public interface OnNotyCenterListener {
        void onOpenSearch();

        void onCloseSearch();

        void onItemNotyScrolling(boolean isScrolling);
    }
}
