package com.tapbi.spark.controlcenter.feature.mishade.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.tapbi.spark.controlcenter.feature.controlios14.view.control.ControlCenterIOSView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.ControlCenterViewOS;
import com.tapbi.spark.controlcenter.feature.mishade.interfaces.DataAction;
import com.tapbi.spark.controlcenter.feature.mishade.view.LayoutActionMiShade;

public class ViewPagerAdapterNotification extends PagerAdapter {
    private final UpdateHeight updateHeight;
    private final ControlCenterIOSView.OnControlCenterListener onControlCenterListener;
    public LayoutActionMiShade notificationOneFragment;
    public LayoutActionMiShade notificationTwoFragment;
    private DataAction dataAction;

    public ViewPagerAdapterNotification(UpdateHeight updateHeight, ControlCenterIOSView.OnControlCenterListener onControlCenterListener, DataAction dataAction) {
        this.updateHeight = updateHeight;
        this.onControlCenterListener = onControlCenterListener;
        this.dataAction = dataAction;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ConstraintLayout rootView;
        if (position == 0) {
            notificationOneFragment = new LayoutActionMiShade(container.getContext(), dataAction, position);
            notificationOneFragment.setCallBackUpdateHeight(updateHeight);
            notificationOneFragment.setOnControlCenterListener(onControlCenterListener);
            rootView = notificationOneFragment;
        } else {
            notificationTwoFragment = new LayoutActionMiShade(container.getContext(), dataAction, position);
            notificationTwoFragment.setCallBackUpdateHeight(updateHeight);
            notificationTwoFragment.setOnControlCenterListener(onControlCenterListener);
            rootView = notificationTwoFragment;
        }
        container.addView(rootView);
        return rootView;
    }

    public void upgradeBgItem(DataAction dataAction) {
        this.dataAction = dataAction;
//    notificationTwoFragment.invalidate();
        notificationTwoFragment.invalidate();

    }

    public void updateActionView(String action, boolean b) {
        if (notificationOneFragment != null) {
            notificationOneFragment.updateActionView(action, b);
        }
        if (notificationTwoFragment != null) {
            notificationTwoFragment.updateActionView(action, b);
        }

    }



    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    public void unregisterItemBaseRcv() {
        if (notificationOneFragment != null) {
            notificationOneFragment.unregisterItemBaseRcv();
        }
        if (notificationTwoFragment != null) {
            notificationTwoFragment.unregisterItemBaseRcv();
        }
    }

    public interface UpdateHeight {
        void updateNewHeight(int height, float percent, boolean enableSwipeVpg);

        void animationView(boolean b);
    }
}
