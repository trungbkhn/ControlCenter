package com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.verticalseekbar;

import android.content.Context;
import android.util.AttributeSet;

import timber.log.Timber;

public class VerticalSeekBar extends BaseVerticalSeekBar {

    private OnVerticalSeekBarListener onVerticalSeekBarListener;


    public interface OnVerticalSeekBarListener {
        void onStartTrackingTouch(VerticalSeekBar verticalSeekBar);

        void onProgressChanged(VerticalSeekBar verticalSeekBar, int i, boolean z);

        void onStopTrackingTouch(VerticalSeekBar verticalSeekBar);

        void onLongPress(VerticalSeekBar verticalSeekBar);
    }

    public VerticalSeekBar(Context context) {
        this(context, null);
    }

    public VerticalSeekBar(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 16842875);
    }

    public VerticalSeekBar(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public final void mo2401a() {
        if (this.onVerticalSeekBarListener != null) {
            this.onVerticalSeekBarListener.onStartTrackingTouch(this);
        }
    }

    public final void mo2184a(float f, boolean z) {
        super.mo2184a(f, z);
        if (this.onVerticalSeekBarListener != null) {
            this.onVerticalSeekBarListener.onProgressChanged(this, getProgress(), z);
        }
    }

    public final void mo2402b() {
        if (this.onVerticalSeekBarListener != null) {
//            this.onVerticalSeekBarListener.mo1783d();
        }
    }

    public final void mo2403c() {
        if (this.onVerticalSeekBarListener != null) {
            this.onVerticalSeekBarListener.onLongPress(this);
        }
    }

    public final void onStopTrackingTouch() {
        if (this.onVerticalSeekBarListener != null) {
            this.onVerticalSeekBarListener.onStopTrackingTouch(this);
        }
    }

    public void setOnSeekBarChangeListener(OnVerticalSeekBarListener listener) {
        this.onVerticalSeekBarListener = listener;
    }
}
