package com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.horizontalseekbar;

import android.content.Context;
import android.util.AttributeSet;

public class HorizontalSeekBar extends BaseHorizontalSeekBar {

    private OnHorizonSeekBarListener onHorizonSeekBarListener;


    public interface OnHorizonSeekBarListener {
        void onStartTrackingTouch(HorizontalSeekBar horizontalSeekBar);

        void onProgressChanged(HorizontalSeekBar horizontalSeekBar, int i, boolean z);

        void onStopTrackingTouch(HorizontalSeekBar horizontalSeekBar);

        void onLongPress(HorizontalSeekBar horizontalSeekBar);
    }

    public HorizontalSeekBar(Context context) {
        this(context, null);
    }

    public HorizontalSeekBar(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 16842875);
    }

    public HorizontalSeekBar(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public final void onStartTrackingTouch() {
        if (this.onHorizonSeekBarListener != null) {
            this.onHorizonSeekBarListener.onStartTrackingTouch(this);
        }
    }

    public final void mo2184a(float f, boolean z) {
        super.mo2184a(f, z);
        if (this.onHorizonSeekBarListener != null) {
            this.onHorizonSeekBarListener.onProgressChanged(this, getProgress(), z);
        }
    }

    public final void mo2402b() {
        if (this.onHorizonSeekBarListener != null) {
//            this.onVerticalSeekBarListener.mo1783d();
        }
    }

    public final void mo2403c() {
        if (this.onHorizonSeekBarListener != null) {
            this.onHorizonSeekBarListener.onLongPress(this);
        }
    }

    public final void onStopTrackingTouch() {
        if (this.onHorizonSeekBarListener != null) {
            this.onHorizonSeekBarListener.onStopTrackingTouch(this);
        }
    }

    public void setOnSeekBarChangeListener(OnHorizonSeekBarListener listener) {
        this.onHorizonSeekBarListener = listener;
    }
}