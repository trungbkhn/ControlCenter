package com.tapbi.spark.controlcenter.feature.edge;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.tapbi.spark.controlcenter.App;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.data.repository.ThemeHelper;
import com.tapbi.spark.controlcenter.interfaces.OnTouchViewListener;

import java.lang.reflect.Field;

public abstract class BaseGroupTouch extends ConstraintLayout {
    protected WindowManager.LayoutParams layoutParams;

    public BaseGroupTouch(@NonNull Context context) {
        super(context);
        initView();
    }

    public BaseGroupTouch(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public BaseGroupTouch(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public abstract void setTouchListener(OnTouchViewListener onTouchViewListener);

    protected abstract void setUpLayoutParams();

    public abstract void setUpView();

    public abstract void onStateEditChanged(boolean isEdit);

    public abstract void onColorNotyChanged(int color);

    public abstract void onColorControlChanged(int color);

    protected abstract void updateStateTouchShow();

    private void initView() {
        setUpLp();
        setUpView();
    }

    protected void setEnabledTouchNoty(View view) {
//        int typeNoty = App.tinyDB.getInt(Constant.TYPE_NOTY, Constant.VALUE_CONTROL_CENTER);
        int typeNoty= ThemeHelper.itemControl.getIdCategory();
        if (typeNoty == Constant.VALUE_SHADE) {
            view.setVisibility(View.GONE);
        } else {
            boolean enabledTouchControl = App.tinyDB.getBoolean(Constant.ENABLE_NOTY, Constant.DEFAULT_ENABLE_NOTY);
            view.setVisibility(enabledTouchControl ? View.VISIBLE : View.INVISIBLE);
        }
    }

    protected void setEnabledTouchControl(View view) {
        boolean enabledTouchControl = App.tinyDB.getBoolean(Constant.ENABLE_CONTROL, Constant.DEFAULT_ENABLE_CONTROL);
        view.setVisibility(enabledTouchControl ? View.VISIBLE : View.GONE);
    }

    public WindowManager.LayoutParams getLayoutParams() {
        return layoutParams;
    }

    private void setUpLp() {
        layoutParams = getLpEdgeBase();
    }

    public void setSizeTouch() {
        setUpLayoutParams();
    }

    private WindowManager.LayoutParams getLpEdgeBase() {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            lp.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY;
        } else {
            lp.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        }
        lp.format = PixelFormat.TRANSPARENT;
        setFlagParams(lp);
        return lp;
    }

    private void setFlagParams(WindowManager.LayoutParams flagParams) {
        flagParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
    }

    protected void setWidthParams(WindowManager.LayoutParams paramsManager, int w, int h) {
        //set no anim to fix case change size width and height jerky movement
        try {
            String className = "android.view.WindowManager$LayoutParams";
            Class<?> layoutParamsClass = Class.forName(className);
            Field privateFlags = layoutParamsClass.getField("privateFlags");
            Field noAnim = layoutParamsClass.getField("PRIVATE_FLAG_NO_MOVE_ANIMATION");

            int privateFlagsValue = privateFlags.getInt(layoutParams);
            int noAnimFlag = noAnim.getInt(null); // null because it's a static field
            privateFlagsValue = privateFlagsValue | noAnimFlag;
            privateFlags.setInt(layoutParams, privateFlagsValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        paramsManager.width = w;
        paramsManager.height = h;
    }

    public void setIsEdit(boolean isEdit) {
        onStateEditChanged(isEdit);
    }

    public void setColorNoty(int colorTouch) {
        onColorNotyChanged(colorTouch);
    }

    public void setColorControl(int colorTouch) {
        onColorControlChanged(colorTouch);
    }

    public void updateStateEnable() {
        updateStateTouchShow();
    }
}
