package com.tapbi.spark.controlcenter.feature.controlios14.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.feature.controlios14.view.noty.ImageBackgroundItemView;
import com.tapbi.spark.controlcenter.utils.MethodUtils;


public class MenuRightLayout extends ConstraintLayout {

    private Path path;
    private int radius;
    private int margin;
    private int widthShow;
    private int widthItem;
    private int height;
    private int spaceSwipeDelete;
    private boolean isTypeCanDelete;
    private TextView tvViewNoty, tvViewRight;
    private ImageBackgroundItemView bgTvRight;
    private final ValueAnimator animShowTvLeft = new ValueAnimator();
    private final ValueAnimator animHideTvLeft = new ValueAnimator();
    private boolean isSwipeToDelete;
    private int widthDefault;

    public MenuRightLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public MenuRightLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MenuRightLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        path = new Path();
        radius = MethodUtils.dpToPx(getContext(),12);
        margin = MethodUtils.dpToPx(getContext(),8);
        spaceSwipeDelete = MethodUtils.dpToPx(getContext(),30);
        //setBackgroundColor(Color.RED);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.clipPath(path);
        super.dispatchDraw(canvas);
    }

    public void setSize(TextView tvViewNoty, TextView tvViewRight, ImageBackgroundItemView bgTvRight, int widthShow, int height, boolean isCanDeleteGroup) {
        this.tvViewNoty = tvViewNoty;
        this.bgTvRight = bgTvRight;
        tvViewNoty.setContentDescription(Constant.SHOW);
        this.tvViewRight = tvViewRight;
        this.isTypeCanDelete = isCanDeleteGroup;
        this.widthShow = widthShow;
        this.height = height;
        this.isSwipeToDelete = false;
        LayoutParams layoutParams = (LayoutParams) getLayoutParams();
        layoutParams.width = widthDefault;
        layoutParams.height = height;
        requestLayout();

        post(() -> {
            widthDefault = getWidth();
            widthItem = (int) ((widthDefault - MethodUtils.dpToPx(getContext(),9)) * 0.5F);
            tvViewNoty.getLayoutParams().width = widthItem;
            tvViewNoty.requestLayout();
        });

    }


    public void updateShow(int spaceShow) {
        float alpha = (float) Math.abs(spaceShow) / (widthShow / 1.5F);
        if (alpha > 1) {
            alpha = 1;
        }
        setAlpha(alpha);
        path.reset();


        float left;
        float leftOrigin = 0;
        if (isTypeCanDelete) {
            left = widthShow - spaceShow + margin;
            leftOrigin = left;
            if (left < margin) {
                left = margin;
            }

            //Timber.e("width: " + widthShow + " /w:"+getWidth()+ " /left: " + left + " /spaceShow: " + spaceShow + " /margin: " + margin + " /khoang cach: " + (getWidth() - left));
        } else {
            left = getWidth() - spaceShow + margin;
            if (left < margin + widthShow) {
                left = margin + widthShow;
            }
        }

        path.addRoundRect(new RectF(left, 0, getWidth(), height), radius, radius, Path.Direction.CW);
        if (isTypeCanDelete) {
            if (left == margin) {
                LayoutParams layoutParams = (LayoutParams) getLayoutParams();
                layoutParams.width = spaceShow;
//            Timber.e("leftOrigin: " + leftOrigin + " /spaceSwipeDelete: " + spaceSwipeDelete + " /tvViewNoty " + tvViewNoty.getVisibility());
                //Timber.e("hoangld: "+tvViewNoty.getWidth());
                path.reset();
                path.addRoundRect(new RectF(left, 0, spaceShow, height), radius, radius, Path.Direction.CW);
                requestLayout();
                if (Math.abs(leftOrigin) > spaceSwipeDelete) {
                    if (tvViewNoty.getContentDescription() == Constant.SHOW) {
                        setExpandDelete(true);
                    }
                } else {
                    if (tvViewNoty.getContentDescription() == Constant.HIDE) {
                        setExpandDelete(false);
                    }
                }
            } else if ((getWidth() - left) != spaceShow) {
                LayoutParams layoutParams = (LayoutParams) getLayoutParams();
                layoutParams.width = widthDefault;
                path.reset();
                path.addRoundRect(new RectF(left, 0, widthDefault, height), radius, radius, Path.Direction.CW);
                requestLayout();
            }

        } else {
            invalidate();
        }
    }


    private void setExpandDelete(boolean isExpand) {
        tvViewNoty.setContentDescription(isExpand ? Constant.HIDE : Constant.SHOW);
        isSwipeToDelete = isExpand;
        //Timber.e("hoangld: "+isExpand);
        if (isExpand) {
            animShowTvLeft.cancel();
            animHideTvLeft.setIntValues(tvViewNoty.getWidth(), 0);
            animHideTvLeft.addUpdateListener(valueAnimator -> {
                LayoutParams lp = (LayoutParams) tvViewNoty.getLayoutParams();
                lp.width = (int) valueAnimator.getAnimatedValue();
                //Timber.e("hoangld hide: "+ tvViewNoty.getWidth());
                tvViewNoty.requestLayout();
            });
            animHideTvLeft.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    tvViewNoty.setText("");
                }
            });
            animHideTvLeft.setDuration(100);
            animHideTvLeft.start();
        } else {
            animHideTvLeft.cancel();
            animShowTvLeft.setIntValues(tvViewNoty.getWidth(), widthItem);
            animShowTvLeft.addUpdateListener(valueAnimator -> {
                LayoutParams lp = (LayoutParams) tvViewNoty.getLayoutParams();
                lp.width = (int) valueAnimator.getAnimatedValue();
                tvViewNoty.requestLayout();
            });
            animShowTvLeft.setDuration(100);
            animShowTvLeft.start();
        }
        if (tvViewNoty.getText().toString().length() == 0) {
            tvViewNoty.setText(getContext().getText(R.string.view_noty));
        }
    }

    public boolean isSwipeToDelete() {
        return isSwipeToDelete;
    }

    public int getWidthShow() {
        return widthShow;
    }

    public boolean isTypeCanDelete() {
        return isTypeCanDelete;
    }

}
