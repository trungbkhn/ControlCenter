package com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.horizontalseekbar;

import android.content.Context;
import android.util.AttributeSet;

import com.tapbi.spark.controlcenter.R;

public class HorizontalThumbSeekbar extends BaseHorizontalSeekBar {

    private OnHorizonThumbSeekBarListener onHorizonThumbSeekBarListener;


    public interface OnHorizonThumbSeekBarListener {
        void onStartTrackingTouch(HorizontalThumbSeekbar horizontalSeekBar);

        void onProgressChanged(HorizontalThumbSeekbar horizontalSeekBar, int i, boolean z);

        void onStopTrackingTouch(HorizontalThumbSeekbar horizontalSeekBar);

        void onLongPress(HorizontalThumbSeekbar horizontalSeekBar);
    }

    public HorizontalThumbSeekbar(Context context) {
        this(context, null);
//        setThumb(context.getDrawable(R.drawable.custom_thumb_seekbar));
    }

    public HorizontalThumbSeekbar(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 16842875);
//        setThumb(context.getDrawable(R.drawable.custom_thumb_seekbar));
    }

    public HorizontalThumbSeekbar(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
//        setThumb(context.getDrawable(R.drawable.custom_thumb_seekbar));
    }

    public final void onStartTrackingTouch() {
        if (this.onHorizonThumbSeekBarListener != null) {
            this.onHorizonThumbSeekBarListener.onStartTrackingTouch(this);
        }
    }

    public final void mo2184a(float f, boolean z) {
        super.mo2184a(f, z);
        if (this.onHorizonThumbSeekBarListener != null) {
            this.onHorizonThumbSeekBarListener.onProgressChanged(this, getProgress(), z);
        }
    }

    public final void mo2402b() {
        if (this.onHorizonThumbSeekBarListener != null) {
//            this.onVerticalSeekBarListener.mo1783d();
        }
    }

    public final void mo2403c() {
        if (this.onHorizonThumbSeekBarListener != null) {
            this.onHorizonThumbSeekBarListener.onLongPress(this);
        }
    }

    public final void onStopTrackingTouch() {
        if (this.onHorizonThumbSeekBarListener != null) {
            this.onHorizonThumbSeekBarListener.onStopTrackingTouch(this);
        }
    }

    public void setOnSeekBarChangeListener(OnHorizonThumbSeekBarListener listener) {
        this.onHorizonThumbSeekBarListener = listener;
    }
}