package com.tapbi.spark.controlcenter.feature.edge;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tapbi.spark.controlcenter.App;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.databinding.ViewTouchRightBinding;
import com.tapbi.spark.controlcenter.interfaces.OnTouchViewListener;

public class GroupTouchRight extends BaseGroupTouch {
    private ViewTouchRightBinding binding;

    public GroupTouchRight(@NonNull Context context) {
        super(context);
    }

    public GroupTouchRight(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GroupTouchRight(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setTouchListener(OnTouchViewListener onTouchViewListener) {
        binding.vTouchNoty.setOnTouch(onTouchViewListener, true, Constant.EDGE_RIGHT);
        binding.vTouchControl.setOnTouch(onTouchViewListener, false, Constant.EDGE_RIGHT);
    }

    @Override
    protected void setUpLayoutParams() {
        layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        int percentSize = App.tinyDB.getInt(Constant.SIZE_TOUCH_EDGE_RIGHT_PERCENT, Constant.SIZE_TOUCH_EDGE_PERCENT_DEFAULT);
        int percentLength = App.tinyDB.getInt(Constant.LENGTH_TOUCH_EDGE_RIGHT_PERCENT, Constant.LENGTH_TOUCH_EDGE_PERCENT_DEFAULT);
        int percentPosition = App.tinyDB.getInt(Constant.POSITION_TOUCH_EDGE_RIGHT_PERCENT, Constant.POSITION_TOUCH_EDGE_PERCENT_DEFAULT);

        int sizePlus = (int) (App.statusBarHeight * (percentSize - Constant.SIZE_TOUCH_EDGE_PERCENT_DEFAULT) / 100f);

        int length = (int) (App.widthHeightScreenCurrent.h * (Constant.LENGTH_TOUCH_EDGE_PERCENT_MIN + percentLength / 100f * (100f - Constant.LENGTH_TOUCH_EDGE_PERCENT_MIN)) / 100f);
        int size = App.statusBarHeight + sizePlus;
        float verticalBias = (float) percentPosition / 100f;
        layoutParams.x = App.widthHeightScreenCurrent.w - size;
        layoutParams.y = (int) ((App.widthHeightScreenCurrent.h - length) * verticalBias);
        //Timber.e("hoangld size " + size + " length " + length);
        setWidthParams(layoutParams, size, length);
    }

    @Override
    public void setUpView() {
        binding = ViewTouchRightBinding.inflate(LayoutInflater.from(getContext()), this, true);
        int colorNoty = App.tinyDB.getInt(Constant.COLOR_EDGE_NOTY_RIGHT, Color.TRANSPARENT);
        int colorControl = App.tinyDB.getInt(Constant.COLOR_EDGE_CONTROL_RIGHT, Color.TRANSPARENT);
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
        setVisibility(App.tinyDB.getBoolean(Constant.KEY_ENABLED_EDGE_RIGHT, Constant.DEFAULT_ENABLED_EDGE_LEFT_RIGHT_BOTTOM) ? View.VISIBLE : View.GONE);
        setEnabledTouchNoty(binding.vTouchNoty);
        setEnabledTouchControl(binding.vTouchControl);
    }
}
