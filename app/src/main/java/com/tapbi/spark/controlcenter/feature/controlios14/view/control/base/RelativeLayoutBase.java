package com.tapbi.spark.controlcenter.feature.controlios14.view.control.base;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;

import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.data.repository.ThemesRepository;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlSettingIosModel;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.DataSetupViewControlModel;

import java.io.IOException;
import java.io.InputStream;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

public abstract class RelativeLayoutBase extends RelativeLayout {

    private final Handler handler = new Handler();
    protected ControlSettingIosModel controlSettingIosModel;
    protected DataSetupViewControlModel dataSetupViewControlModel;
    protected boolean isSelect = false;

    private String pathAssets = "";
    private ViewPropertyAnimator scaleY = null;
    private boolean down;
    private boolean isLongClick;
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            isLongClick = true;
            onLongClick();
        }
    };
    private Paint paint;
    private boolean isDrawBackground = true;
    //use viewParent to animate, fix the view container as ScrollView
    private View viewParent;

    public RelativeLayoutBase(Context context) {
        super(context);
        init(context);
    }

    public RelativeLayoutBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RelativeLayoutBase(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    protected abstract void click();

    protected abstract void onLongClick();

    protected abstract void onDown();

    protected abstract void onUp();

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Timber.e("event.getAction(): "+event.getAction());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                handler.postDelayed(runnable, ViewConfiguration.getLongPressTimeout());
                onDown();
                break;
            case MotionEvent.ACTION_CANCEL:
                handler.removeCallbacks(runnable);
                onUp();
                isLongClick = false;
            case MotionEvent.ACTION_UP:
                handler.removeCallbacks(runnable);
                onUp();
                if (checkClick(event.getX(), event.getY()) && !isLongClick && !ThemesRepository.isControlEditing()) {
                    click();
                }
                isLongClick = false;
                break;
        }
        return (!ThemesRepository.isControlEditing());
    }

    private boolean checkClick(float x, float y) {
        if ((x < this.getPaddingLeft()) || (x > this.getWidth() - this.getPaddingRight()) || (y > (this.getHeight() - this.getPaddingBottom())) || (y < this.getPaddingTop())) {
            return false;
        }
        return true;
    }

    protected void animationDown() {
        if (scaleY != null) {
            scaleY.cancel();
        }
        down = true;
        scaleY = animate().scaleX(0.85f).scaleY(0.85f);
        scaleY.setDuration(200).setInterpolator(new DecelerateInterpolator());
        scaleY.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (down) {
                    ViewPropertyAnimator scaleY = animate().scaleX(1f).scaleY(1f);
                    scaleY.start();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                down = false;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        scaleY.start();
    }

    protected void animationUp() {
        if (scaleY != null) {
            scaleY.cancel();
        }
        down = false;
        scaleY = animate().scaleX(1.0f).scaleY(1.0f);
        scaleY.setDuration(200).setInterpolator(new AccelerateInterpolator());
        scaleY.start();
    }

    public void animationShow() {
//        if (scaleY != null) {
//            scaleY.cancel();
//        }
//        setScaleX(0.8f);
//        setScaleY(0.8f);
//        setAlpha(0f);
//        scaleY = animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(200).withEndAction(() -> {
//            if (getAlpha() != 1f) {
//                setAlpha(1f);
//            }
//        }).setInterpolator(new DecelerateInterpolator());
//        scaleY.start();
    }

    public void animationHide() {
//        if (scaleY != null) {
//            scaleY.cancel();
//        }
//        setScaleX(1f);
//        setScaleY(1f);
//        setAlpha(1f);
//        scaleY = animate().scaleX(0.8f).scaleY(0.8f).alpha(0f).setDuration(300).setInterpolator(new AccelerateInterpolator());
//        scaleY.start();
    }

    public void setParentView(View view) {
        this.viewParent = view;
    }


    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (controlSettingIosModel != null) {
            if (controlSettingIosModel.getBackgroundImageViewItem() == null || controlSettingIosModel.getBackgroundImageViewItem().isEmpty() || controlSettingIosModel.getBackgroundImageViewItem().equals(Constant.SHAPE_DEFAULT)) {
                if (isSelect) {
                    if (controlSettingIosModel.getBackgroundColorSelectViewItem() != null && !controlSettingIosModel.getBackgroundColorSelectViewItem().isEmpty()) {
                        paint.setColor(Color.parseColor(controlSettingIosModel.getBackgroundColorSelectViewItem()));
                    }
                } else {
                    if (controlSettingIosModel.getBackgroundColorDefaultViewItem() != null && !controlSettingIosModel.getBackgroundColorDefaultViewItem().isEmpty()) {
                        paint.setColor(Color.parseColor(controlSettingIosModel.getBackgroundColorDefaultViewItem()));
                    }
                }
                float corner = controlSettingIosModel.getCornerBackgroundViewItem() * getHeight();
                canvas.drawRoundRect(new RectF(0f, 0f, getWidth(), getHeight()), corner, corner, paint);
            }
        }
        super.dispatchDraw(canvas);
    }

    private void setBackgroundImage() {
        Timber.e("hachung controlSettingIosModel:"+controlSettingIosModel);
        if (controlSettingIosModel != null) {
            String backgroundImageViewItem = controlSettingIosModel.getBackgroundImageViewItem();
            Timber.e("hachung backgroundImageViewItem:"+backgroundImageViewItem);
            if (backgroundImageViewItem != null && !backgroundImageViewItem.isEmpty() &&
                    !backgroundImageViewItem.equals(Constant.SHAPE_DEFAULT)) {
                String assetPath = "iconShade/" + backgroundImageViewItem;
                Timber.e("hachung assetPath:"+assetPath);
                if (pathAssets.isEmpty() || !pathAssets.equals(assetPath)) {
                    pathAssets = assetPath;
                    Completable.fromRunnable(() -> {
                                try (InputStream inputStream = getContext().getAssets().open(assetPath)) {
                                    Drawable drawable = Drawable.createFromStream(inputStream, null);

                                    if (drawable != null && controlSettingIosModel.isFilterBackgroundViewItem()) {
                                        int color = isSelect ?
                                                Color.parseColor(controlSettingIosModel.getBackgroundColorSelectViewItem()) :
                                                Color.parseColor(controlSettingIosModel.getBackgroundColorDefaultViewItem());
                                        drawable.setTint(color);
                                    }

                                    // Đẩy drawable lên main thread để set background
                                    new Handler(Looper.getMainLooper()).post(() -> {
                                        Timber.e("hachung path true");
                                        setBackground(drawable);
                                    });


                                } catch (IOException e) {
                                    Timber.e("Duongcv error loading image from assets: %s", e.getMessage());
                                    new Handler(Looper.getMainLooper()).post(() -> setBackground(null));
                                }
                            })
                            .subscribeOn(Schedulers.io())
                            .subscribe();
                } else {
                    Drawable drawable = getBackground();
                    if (drawable != null) {
                        int color = isSelect ?
                                Color.parseColor(controlSettingIosModel.getBackgroundColorSelectViewItem()) :
                                Color.parseColor(controlSettingIosModel.getBackgroundColorDefaultViewItem());
                        drawable.setTint(color);
                        setBackground(drawable);
                    }
                }


            } else {
                pathAssets = "";
                setBackground(null); // Không có ảnh, đặt null cho background
            }
        }
    }

//    public void changeCornerBackground(float cornerBackground){
//        this.isDrawBackground = true;
//        this.cornerBackground = cornerBackground;
//        invalidate();
//    }
//
//    public void changeShapeBackground(String shapeBackground){
//        this.isDrawBackground = true;
//        this.shapeBackground = shapeBackground;
//        invalidate();
//    }

    public void changeIsSelect(boolean isSelect) {
        this.isSelect = isSelect;
        initColorIcon();
        invalidate();
    }

    public void initColorIcon() {
        setBackgroundImage();
    }

    public void changeData(ControlSettingIosModel controlSettingIosModel) {
        this.isDrawBackground = true;
        this.controlSettingIosModel = controlSettingIosModel;
        initColorIcon();
        invalidate();
    }
}
