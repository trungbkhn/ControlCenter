package com.tapbi.spark.controlcenter.feature.edge;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tapbi.spark.controlcenter.App;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.databinding.ViewTouchTopBinding;
import com.tapbi.spark.controlcenter.interfaces.OnTouchViewListener;

public class GroupTouchTop extends BaseGroupTouch {
    public ViewTouchTopBinding binding;

    public GroupTouchTop(@NonNull Context context) {
        super(context);
    }

    public GroupTouchTop(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GroupTouchTop(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setTouchListener(OnTouchViewListener onTouchViewListener) {
        binding.vTouchNoty.setOnTouch(onTouchViewListener, true, Constant.EDGE_TOP);
        binding.vTouchControl.setOnTouch(onTouchViewListener, false, Constant.EDGE_TOP);
    }

    @Override
    protected void setUpLayoutParams() {
        layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        int percentSize = App.tinyDB.getInt(Constant.SIZE_TOUCH_EDGE_TOP_PERCENT, Constant.SIZE_TOUCH_EDGE_PERCENT_DEFAULT);
        int sizePlus = (int) (App.statusBarHeight * (percentSize - Constant.SIZE_TOUCH_EDGE_PERCENT_DEFAULT) / 100f);
        setWidthParams(layoutParams, App.widthHeightScreenCurrent.w, App.statusBarHeight + sizePlus);
    }

    @Override
    public void setUpView() {
        binding = ViewTouchTopBinding.inflate(LayoutInflater.from(getContext()), this, true);
        int colorNoty = App.tinyDB.getInt(Constant.COLOR_EDGE_NOTY_TOP, Color.TRANSPARENT);
        int colorControl = App.tinyDB.getInt(Constant.COLOR_EDGE_CONTROL_TOP, Color.TRANSPARENT);
        setColorNoty(colorNoty);
        setColorControl(colorControl);
        updateStateEnable();

    }

    @Override
    public void onStateEditChanged(boolean isEdit) {
        binding.vTouchNoty.setShowEdit(isEdit);
        binding.vTouchControl.setShowEdit(isEdit);
    }

    @Override
    public void onColorNotyChanged(int color) {
        binding.vTouchNoty.setColorEdge(color);
    }

    @Override
    public void onColorControlChanged(int color) {
        binding.vTouchControl.setColorEdge(color);
    }

    @Override
    protected void updateStateTouchShow() {
        setEnabledTouchNoty(binding.vTouchNoty);
        setEnabledTouchControl(binding.vTouchControl);
    }

    public boolean isTouchViewInv() {
        return binding.viewTouchInv.isTouch();
    }

    public boolean isTouchDownViewControl(){
        return binding.vTouchControl.isTouchDown();
    }
}
