package com.tapbi.spark.controlcenter.feature.controlios14.view.control.group3;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.ImageBase;

public class ImageBackground extends ImageBase {

    public ImageBackground(Context context) {
        super(context);
    }

    public ImageBackground(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageBackground(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int paddingIcon = (int) (w * 0.227f);
        setPadding(paddingIcon, paddingIcon, paddingIcon, paddingIcon);
    }
    @Override
    protected void click() {

    }

    @Override
    protected void longClick() {

    }

    @Override
    protected void onDown() {

    }

    @Override
    protected void onUp() {

    }
}
