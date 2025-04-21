package com.tapbi.spark.controlcenter.feature.controlios14.view.control.base;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;


public class RelativeLayoutAnimation extends RelativeLayout {

    private ViewPropertyAnimator scaleY = null;
    private boolean down;

    public RelativeLayoutAnimation(Context context) {
        super(context);
    }

    public RelativeLayoutAnimation(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RelativeLayoutAnimation(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void animationShow() {
        if (scaleY != null) {
            scaleY.cancel();
        }
        setScaleX(0.8f);
        setScaleY(0.8f);
        setAlpha(0f);
        scaleY = animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(300).setInterpolator(new DecelerateInterpolator());
        scaleY.start();
    }

    public void animationHide() {
        try {
            if (scaleY != null) {
                scaleY.cancel();
            }
            setScaleX(1f);
            setScaleY(1f);
            setAlpha(1f);
            scaleY = animate().scaleX(0.8f).scaleY(0.8f).alpha(0f).setDuration(300).setInterpolator(new AccelerateInterpolator());
            scaleY.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
